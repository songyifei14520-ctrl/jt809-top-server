package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.baseMsg.VehicleAddedEntity;
import org.dromara.common.jt809.netty.codec.entity.exgMsg.DrvlineAckEntity;
import org.dromara.common.jt809.netty.codec.entity.exgMsg.DrvlineReqEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 基础消息定时调度器
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("basemsg")
public class BaseMsgScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每分钟模拟下发一次DOWN_BASE_MSG_VEHICLE_ADDED指令
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendVehicleAddedInstruction() {
        // 使用VehicleAddedEntity.create构造消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        VehicleAddedEntity entity = VehicleAddedEntity.create(vehicleNo, vehicleColor);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_BASE_MSG_VEHICLE_ADDED报文:{}", hex);
        log.info("DOWN_BASE_MSG_VEHICLE_ADDED报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_BASE_MSG_VEHICLE_ADDED指令: vehicleNo={}, vehicleColor={}", vehicleNo, vehicleColor);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每17分钟模拟下发一次DOWN_BASE_MSG_DRVLINE_REQ指令（车辆单向监听请求消息）
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendDrvlineReqInstruction() {
        // 使用DrvlineReqEntity.create构造车辆单向监听请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        byte[] data = new byte[8]; // 模拟数据
        DrvlineReqEntity entity = DrvlineReqEntity.create(vehicleNo, vehicleColor);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_BASE_MSG_DRVLINE_REQ报文:{}",hex);
        log.info("DOWN_BASE_MSG_DRVLINE_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_BASE_MSG_DRVLINE_REQ指令（车辆单向监听请求消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每18分钟模拟下发一次DOWN_BASE_MSG_DRVLINE_ACK指令（车辆单向监听应答消息）
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendDrvlineAckInstruction() {
        // 使用DrvlineAckEntity.create构造车辆单向监听应答消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        byte result = 0; // 应答结果：0-成功
        DrvlineAckEntity entity = DrvlineAckEntity.create(vehicleNo, vehicleColor,0, result, "监听成功");

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_BASE_MSG_DRVLINE_ACK报文:{}", hex);
        log.info("DOWN_BASE_MSG_DRVLINE_ACK报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_BASE_MSG_DRVLINE_ACK指令（车辆单向监听应答消息），车牌号: {}, 应答结果: {}", vehicleNo, result);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每27分钟模拟下发一次DOWN_BASE_MSG_VEHICLE_ADDEDK指令（补报车辆静态信息应答消息）
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendVehicleAddedAckInstruction() {
        // 使用VehicleAddedAckEntity.create构造补报车辆静态信息应答消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        VehicleAddedEntity entity = VehicleAddedEntity.create(vehicleNo, vehicleColor);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_BASE_MSG_VEHICLE_ADDED报文:{}", hex);
        log.info("DOWN_BASE_MSG_VEHICLE_ADDEDK报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_BASE_MSG_VEHICLE_ADDED指令（补报车辆静态信息应答消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
