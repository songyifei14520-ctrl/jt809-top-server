package cn.com.onlinetool.jt809.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JT809消息缓存处理器
 * 解决TCP分包问题，实现消息的缓存和重组
 *
 * @author system
 * @date 2025-01-21
 */
@Slf4j
@Component
public class MessageCacheHandler extends ChannelInboundHandlerAdapter {

    /**
     * JT809协议消息头标识
     */
    private static final byte MSG_HEAD = 0x5B;

    /**
     * JT809协议消息尾标识
     */
    private static final byte MSG_TAIL = 0x5D;

    /**
     * 消息缓存Map，key为channelId，value为缓存的消息数据
     */
    private final ConcurrentHashMap<String, ByteBuf> messageCache = new ConcurrentHashMap<>();

    /**
     * 消息缓存时间戳Map，用于超时清理
     */
    private final ConcurrentHashMap<String, Long> cacheTimestamp = new ConcurrentHashMap<>();

    /**
     * 定时清理器
     */
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "JT809-MessageCache-Cleanup");
        t.setDaemon(true);
        return t;
    });

    /**
     * 缓存超时时间（毫秒）- 5分钟
     */
    private static final long CACHE_TIMEOUT_MS = 5 * 60 * 1000;

    public MessageCacheHandler() {
        // 启动定时清理任务，每30秒执行一次
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredCache, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            ctx.fireChannelRead(msg);
            return;
        }

        ByteBuf incomingData = (ByteBuf) msg;
        String channelId = ctx.channel().id().asShortText();
        boolean dataProcessed = false;

        try {
            // 处理消息缓存和重组
            ByteBuf completeMessage = processMessage(channelId, incomingData);
            dataProcessed = true; // processMessage已处理incomingData

            if (completeMessage != null) {
                // 找到完整消息，传递给下一个处理器
                log.info("通道[{}]TCP分包重组完成，完整消息长度: {} 字节", channelId, completeMessage.readableBytes());
                ctx.fireChannelRead(completeMessage);
            } else {
                log.info("通道[{}]接收到TCP分包数据，长度: {} 字节，已缓存等待后续分包", channelId, incomingData.readableBytes());
            }
        } catch (Exception e) {
            log.error("通道[{}]处理消息时发生异常: {}", channelId, e.getMessage(), e);
            // 清理该通道的缓存
            clearChannelCache(channelId);
            // 只有在processMessage未处理时才释放
            if (!dataProcessed) {
                safeRelease(incomingData);
            }
        }
    }

    /**
     * 处理消息缓存和重组
     *
     * @param channelId 通道ID
     * @param incomingData 接收到的数据
     * @return 完整的消息，如果消息不完整则返回null
     */
    private ByteBuf processMessage(String channelId, ByteBuf incomingData) {
        // 获取或创建缓存
        ByteBuf cachedData = messageCache.get(channelId);
        ByteBuf currentData;

        if (cachedData != null) {
            // 有缓存数据，合并新数据
            currentData = Unpooled.buffer(cachedData.readableBytes() + incomingData.readableBytes());
            currentData.writeBytes(cachedData);
            currentData.writeBytes(incomingData);

            log.info("通道[{}]TCP分包合并：第一包长度: {} 字节，第二包长度: {} 字节，合并后总长度: {} 字节",
                    channelId, cachedData.readableBytes(), incomingData.readableBytes(), currentData.readableBytes());

            // 安全释放原缓存和输入数据
            safeRelease(cachedData);
            safeRelease(incomingData);
        } else {
            // 没有缓存数据，直接使用新数据
            currentData = incomingData.retain();
            safeRelease(incomingData);
            log.info("通道[{}]接收到单包数据，长度: {} 字节", channelId, currentData.readableBytes());
        }

        // 检查消息完整性
        ByteBuf completeMessage = extractCompleteMessage(currentData);

        if (completeMessage != null) {
            // 找到完整消息
            int completeMessageLength = completeMessage.readableBytes();
            int remainingLength = currentData.readableBytes() - completeMessageLength;

            if (remainingLength > 0) {
                // 还有剩余数据，继续缓存
                ByteBuf remainingData = currentData.slice(completeMessageLength, remainingLength).retain();
                messageCache.put(channelId, remainingData);
                cacheTimestamp.put(channelId, System.currentTimeMillis());
                log.debug("通道[{}]提取完整消息后还有剩余数据，长度: {}", channelId, remainingLength);
            } else {
                // 没有剩余数据，清理缓存
                clearChannelCache(channelId);
            }

            safeRelease(currentData);
            return completeMessage;
        } else {
            // 消息不完整，缓存数据
            messageCache.put(channelId, currentData);
            cacheTimestamp.put(channelId, System.currentTimeMillis());
            return null;
        }
    }

    /**
     * 从数据中提取完整的消息
     *
     * @param data 数据缓冲区
     * @return 完整的消息，如果没有找到完整消息则返回null
     */
    private ByteBuf extractCompleteMessage(ByteBuf data) {
        if (data.readableBytes() < 2) {
            return null; // 数据太短，无法包含头尾标识
        }

        int readerIndex = data.readerIndex();

        // 查找消息头 0x5B
        int headIndex = -1;
        for (int i = readerIndex; i < data.writerIndex(); i++) {
            if (data.getByte(i) == MSG_HEAD) {
                headIndex = i;
                break;
            }
        }

        if (headIndex == -1) {
            log.warn("JT809报文格式错误：未找到消息头标识0x5B，数据长度: {} 字节", data.readableBytes());
            return null;
        }

        // 从消息头开始查找消息尾 0x5D
        int tailIndex = -1;
        for (int i = headIndex + 1; i < data.writerIndex(); i++) {
            if (data.getByte(i) == MSG_TAIL) {
                tailIndex = i;
                break;
            }
        }

        if (tailIndex == -1) {
            log.info("JT809报文不完整：找到消息头0x5B但未找到消息尾0x5D，当前数据长度: {} 字节，等待更多TCP分包数据", data.readableBytes());
            return null;
        }

        // 找到完整消息
        int messageLength = tailIndex - headIndex + 1;
        ByteBuf completeMessage = data.slice(headIndex, messageLength).retain();

        log.info("JT809报文解析成功：消息头位置: {}, 消息尾位置: {}, 完整报文长度: {} 字节", headIndex, tailIndex, messageLength);

        return completeMessage;
    }

    /**
     * 安全释放ByteBuf
     *
     * @param buf 要释放的ByteBuf
     */
    private void safeRelease(ByteBuf buf) {
        if (buf != null && buf.refCnt() > 0) {
            buf.release();
        }
    }

    /**
     * 清理指定通道的缓存
     *
     * @param channelId 通道ID
     */
    private void clearChannelCache(String channelId) {
        ByteBuf cached = messageCache.remove(channelId);
        if (cached != null) {
            safeRelease(cached);
        }
        cacheTimestamp.remove(channelId);
        log.debug("清理通道[{}]的缓存数据", channelId);
    }

    /**
     * 清理过期的缓存数据
     */
    private void cleanupExpiredCache() {
        long currentTime = System.currentTimeMillis();

        cacheTimestamp.entrySet().removeIf(entry -> {
            String channelId = entry.getKey();
            long timestamp = entry.getValue();

            if (currentTime - timestamp > CACHE_TIMEOUT_MS) {
                log.warn("通道[{}]缓存数据超时，清理缓存", channelId);
                ByteBuf cached = messageCache.remove(channelId);
                if (cached != null) {
                    safeRelease(cached);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通道关闭时清理缓存
        String channelId = ctx.channel().id().asShortText();
        clearChannelCache(channelId);
        log.info("通道[{}]关闭，清理相关缓存", channelId);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常时清理缓存
        String channelId = ctx.channel().id().asShortText();
        clearChannelCache(channelId);
        log.error("通道[{}]发生异常，清理相关缓存: {}", channelId, cause.getMessage(), cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    public String getCacheStats() {
        return String.format("当前缓存通道数: %d, 总缓存大小: %d字节",
                messageCache.size(),
                messageCache.values().stream().mapToInt(ByteBuf::readableBytes).sum());
    }

    /**
     * 销毁资源
     */
    public void destroy() {
        // 清理所有缓存
        messageCache.values().forEach(this::safeRelease);
        messageCache.clear();
        cacheTimestamp.clear();

        // 关闭定时器
        cleanupExecutor.shutdown();

        log.info("MessageCacheHandler资源已清理");
    }
}
