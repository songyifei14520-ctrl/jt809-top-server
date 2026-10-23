package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.linkManager.SubCloseLinkInformEntity;
import org.dromara.common.jt809.netty.codec.entity.linkManager.SubDisConnectInformEntity;
import org.dromara.common.jt809.netty.codec.entity.linkManager.SubDisConnectReqEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;

import org.springframework.stereotype.Component;

/**
 * 链路管理消息定时调度器
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("linkmanager")
public class LinkManagerScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每2分钟模拟下发一次DOWN_DISCONNECT_REQ指令（从链路注销请求）
     */
//    @Scheduled(cron = "0 */10 * * * ?")
    public void sendDisconnectInstruction() {
        // 使用SubDisConnectReqEntity.create构造从链路注销请求消息体
        SubDisConnectReqEntity entity = SubDisConnectReqEntity.create(0);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_DISCONNECT_REQ报文:{}", hex);
        log.info("DOWN_DISCONNECT_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_DISCONNECT_REQ指令（从链路注销请求）");
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每3分钟模拟下发一次DOWN_DISCONNECT_INFORM指令（主链路中断通知消息）
     */
//    @Scheduled(cron = "0 */10 * * * ?")
    public void sendDisconnectInformInstruction() {
        // 使用MainDisConnectInformEntity.create构造主链路中断通知消息体
        byte errorCode = 1; // 主链路中断错误码
        SubDisConnectInformEntity entity = SubDisConnectInformEntity.create(errorCode);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_DISCONNECT_INFORM报文:{}", hex);
        log.info("DOWN_DISCONNECT_INFORM报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_DISCONNECT_INFORM指令（主链路中断通知消息），错误码: {}", errorCode);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每4分钟模拟下发一次DOWN_CLOSELINK_INFORM指令（下级平台主动关闭链路通知消息）
     */
//    @Scheduled(cron = "0 */10 * * * ?")
    public void sendCloseLinkInformInstruction() {
        // 使用MainCloseLinkInformEntity.create构造下级平台主动关闭链路通知消息体
        byte reasonCode = 1; // 关闭链路原因码
        SubCloseLinkInformEntity entity = SubCloseLinkInformEntity.create(reasonCode);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_CLOSELINK_INFORM报文:{}", hex);
        log.info("DOWN_CLOSELINK_INFORM报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_CLOSELINK_INFORM指令（下级平台主动关闭链路通知消息），原因码: {}", reasonCode);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
