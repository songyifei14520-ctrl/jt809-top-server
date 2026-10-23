package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.entity.platformMsg.DownRetranReqEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.platformMsg.InfoReqEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.platformMsg.PostQueryReqEntity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 平台消息定时调度器
 * 模拟下发平台查岗、平台间报文和消息补传请求指令
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("platformmsg")
public class PlatformMsgScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每2分钟模拟下发一次DOWN_PLATFORM_MSG_POST_QUERY_REQ指令（平台查岗请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendPostQueryReqInstruction() {
        // 构造平台查岗请求消息
        long infoId = 25L; // 使用当前时间戳作为信息ID
        String infoContent = "平台查岗请求是否是人类" ;
        byte objectType = 2;
        String objectId = "测B01844";
        byte answerTime = 10;

        PostQueryReqEntity entity = PostQueryReqEntity.create( infoId,  infoContent,  objectType,   objectId,  answerTime);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_PLATFORM_MSG_POST_QUERY_REQ报文:{}", hex);
        log.info("DOWN_PLATFORM_MSG_POST_QUERY_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_PLATFORM_MSG_POST_QUERY_REQ指令（平台查岗请求消息），信息ID: {}, 内容: {}", infoId, infoContent);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }


    /**
     * 每3分钟模拟下发一次DOWN_PLATFORM_MSG_INFO_REQ指令（下发平台间报文请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendInfoReqInstruction() {

        // 构造下发平台间报文请求消息
        long infoId = System.currentTimeMillis(); // 使用当前时间戳作为信息ID
        String infoContent = "平台间报文请求 - 是否属动物";
        byte objectType = 2;
        String objectId = "测B01844";

        InfoReqEntity entity = InfoReqEntity.create( infoId,  infoContent,  objectType,  objectId);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_PLATFORM_MSG_INFO_REQ报文:{}", hex);
        log.info("DOWN_PLATFORM_MSG_INFO_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_PLATFORM_MSG_INFO_REQ指令（下发平台间报文请求消息），信息ID: {}, 内容: {}", infoId, infoContent);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每4分钟模拟下发一次DOWN_PLATFORM_MSG_RETRAN_REQ指令（下发平台间消息补传请求）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendRetranReqInstruction() {
        // 构造下发平台间消息补传请求
        int retranDataType = 0x1200; // 需要重传消息的子业务类型标识
        byte serialTotal = 3; // 重传消息总数
        long[] serialList = new long[]{ 10870, 10870}; // 序列号列表
        long time = 1755157177599L; // 重传起始系统UTC时间（秒级）

        DownRetranReqEntity entity = DownRetranReqEntity.create(retranDataType, serialTotal, serialList, time);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_PLATFORM_MSG_RETRAN_REQ报文:{}", hex);
        log.info("DOWN_PLATFORM_MSG_RETRAN_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_PLATFORM_MSG_RETRAN_REQ指令（下发平台间消息补传请求），重传数据类型: 0x{}, 重传消息总数: {}, 起始时间: {}",
                    Integer.toHexString(retranDataType).toUpperCase(), serialTotal, time);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
