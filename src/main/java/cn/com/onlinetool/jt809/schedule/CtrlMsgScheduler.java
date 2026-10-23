package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.ctrlMsg.*;
import org.dromara.common.jt809.netty.codec.util.BufUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 控制消息定时调度器
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("ctrlmsg")
public class CtrlMsgScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每22分钟模拟下发一次DOWN_CTRL_MSG_MONITOR_VEHICLE_REQ指令（车辆监听请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendMonitorVehicleReqInstruction() {
        // 使用MonitorVehicleReqEntity.create构造车辆监听请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        byte monitorType = 1; // 监听类型
        long startTime = System.currentTimeMillis(); // 开始时间
        long endTime = System.currentTimeMillis() + 3600000L; // 结束时间（1小时后）
        MonitorVehicleReqEntity entity = MonitorVehicleReqEntity.create(vehicleNo, vehicleColor,"15581818788");

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
//        String hex = "5b00000052000000019500052df12c01020f010507de340000000068a2947d994df0f3982212ab0de3ceedae58dfc1dff0d285c7bc12e855d4704c701ef6c29e14c0aa30ae04c1a0516807ff06518c30bb5d";
        log.info("DOWN_CTRL_MSG_MONITOR_VEHICLE_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_CTRL_MSG_MONITOR_VEHICLE_REQ指令（车辆监听请求消息），车牌号: {}, 监听类型: {}", vehicleNo, monitorType);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每23分钟模拟下发一次DOWN_CTRL_MSG_TAKE_PHOTO_REQ指令（车辆拍照请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendTakePhotoReqInstruction() {
        // 使用TakePhotoReqEntity.create构造车辆拍照请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        byte lensId = 1; // 镜头ID
        int photoSize = 1024; // 照片尺寸
        int photoQuality = 80; // 照片质量
        int brightness = 50; // 亮度
        int contrast = 50; // 对比度
        int saturation = 50; // 饱和度
        int chroma = 50; // 色度
        TakePhotoReqEntity entity = TakePhotoReqEntity.create(vehicleNo, vehicleColor, lensId, (byte)photoSize);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_CTRL_MSG_TAKE_PHOTO_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_CTRL_MSG_TAKE_PHOTO_REQ指令（车辆拍照请求消息），车牌号: {}, 镜头ID: {}", vehicleNo, lensId);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每24分钟模拟下发一次DOWN_CTRL_MSG_TEXT_REQ指令（下发车辆报文请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendTextInfoReqInstruction() {
        // 使用TextInfoReqEntity.create构造下发车辆报文请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        byte msgType = 1; // 消息类型
        String msgContent = "请注意安全驾驶，当前路段限速80km/h"; // 消息内容
        TextInfoReqEntity entity = TextInfoReqEntity.create(vehicleNo, vehicleColor,0, msgType, msgContent);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_CTRL_MSG_TEXT_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_CTRL_MSG_TEXT_REQ指令（下发车辆报文请求消息），车牌号: {}, 消息类型: {}", vehicleNo, msgType);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每25分钟模拟下发一次DOWN_CTRL_MSG_TAKE_TRAVEL_REQ指令（上报车辆行驶记录请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendTakeTravelReqInstruction() {
        // 使用TakeTravelReqEntity.create构造上报车辆行驶记录请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        byte commandType = 1; // 命令类型
        byte[] commandData = new byte[]{0x01, 0x02, 0x03, 0x04}; // 命令数据
        TakeTravelReqEntity entity = TakeTravelReqEntity.create(vehicleNo, vehicleColor, 0, 0,(byte) 1);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_CTRL_MSG_TAKE_TRAVEL_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_CTRL_MSG_TAKE_TRAVEL_REQ指令（上报车辆行驶记录请求消息），车牌号: {}, 命令类型: {}", vehicleNo, commandType);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每26分钟模拟下发一次DOWN_CTRL_MSG_EMERGENCY_MONITORING_REQ指令（车辆应急接入监管平台请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendEmergencyMonitoringReqInstruction() {
        // 使用EmergencyMonitoringReqEntity.create构造车辆应急接入监管平台请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        String authenticationCode = "AUTH123456"; // 鉴权码
        String accessPointName = "CMNET"; // 接入点名称
        String username = "user123"; // 用户名
        String password = "pass123"; // 密码
        String serverIp = "192.168.1.100"; // 服务器IP
        int tcpPort = 8080; // TCP端口
        int udpPort = 8081; // UDP端口
        long endTime = System.currentTimeMillis() + 86400000L; // 24小时后结束
        EmergencyMonitoringReqEntity entity = EmergencyMonitoringReqEntity.create(vehicleNo, vehicleColor,
                authenticationCode, accessPointName, username, password, serverIp, tcpPort, udpPort, endTime);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_CTRL_MSG_EMERGENCY_MONITORING_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_CTRL_MSG_EMERGENCY_MONITORING_REQ指令（车辆应急接入监管平台请求消息），车牌号: {}, 服务器IP: {}", vehicleNo, serverIp);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
