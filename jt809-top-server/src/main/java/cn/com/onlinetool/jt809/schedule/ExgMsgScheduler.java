package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.entity.exgMsg.*;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 交换消息定时调度器
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("exgmsg")
public class ExgMsgScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每7分钟模拟下发一次DOWN_EXG_MSG_REGISTER_ACK指令（车辆注册应答消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendRegisterAckInstruction() {
        // 使用RegisterAckEntity.create构造车辆注册应答消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 黄色
        int dataType = 1;
        long msgSn = 72L;
        byte result = 0; // 成功
        RegisterAckEntity entity = RegisterAckEntity.create(vehicleNo, vehicleColor, dataType, msgSn, result);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_REGISTER_ACK报文:{}", hex);
        log.info("DOWN_EXG_MSG_REGISTER_ACK报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_REGISTER_ACK指令（车辆注册应答消息），车牌号: {}, 应答结果: {}", vehicleNo, result);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每8分钟模拟下发一次DOWN_EXG_MSG_CAR_LOCATION指令（车辆定位信息消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendCarLocationInstruction() {
        // 使用CarLocationEntity.create构造车辆定位信息消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        byte[] gnssData = new byte[36]; // 模拟GNSS数据
        CarLocationEntity entity = CarLocationEntity.create(vehicleNo, vehicleColor, gnssData);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_CAR_LOCATION报文:{}", hex);
        log.info("DOWN_EXG_MSG_CAR_LOCATION报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_CAR_LOCATION指令（车辆定位信息消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每9分钟模拟下发一次DOWN_EXG_MSG_HISTORY_ARCOSSAREA指令（车辆跨域行驶路线信息上报消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendHistoryArcossareaInstruction() {
        // 使用HistoryArcossareaEntity.create构造跨域行驶路线信息消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        List<byte[]> gnssDataList = new ArrayList<>();
        gnssDataList.add(new byte[36]); // 模拟GNSS数据
        HistoryArcossareaEntity entity = HistoryArcossareaEntity.create(vehicleNo, vehicleColor, gnssDataList);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_HISTORY_ARCOSSAREA报文:{}", hex);
        log.info("DOWN_EXG_MSG_HISTORY_ARCOSSAREA报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_HISTORY_ARCOSSAREA指令（车辆跨域行驶路线信息上报消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每10分钟模拟下发一次DOWN_EXG_MSG_CAR_INFO指令（车辆静态信息交换消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendCarInfoInstruction() {
        // 使用CarInfoEntity.create构造车辆静态信息消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        String carInfo = "模拟车辆静态信息";
        CarInfoEntity entity = CarInfoEntity.create(vehicleNo, vehicleColor, carInfo);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_CAR_INFO报文:{}", hex);
        log.info("DOWN_EXG_MSG_CAR_INFO报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_CAR_INFO指令（车辆静态信息交换消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每11分钟模拟下发一次DOWN_EXG_MSG_RETURN_STARTUP指令（启动车辆定位信息交换请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendReturnStartupInstruction() {
        // 使用ReturnStartupEntity.create构造启动车辆定位信息交换请求消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        byte reasonCode = 1; // 启动原因代码
        ReturnStartupEntity entity = ReturnStartupEntity.create(vehicleNo, vehicleColor, reasonCode);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_RETURN_STARTUP报文:{}", hex);
        log.info("DOWN_EXG_MSG_RETURN_STARTUP报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_RETURN_STARTUP指令（启动车辆定位信息交换请求消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每12分钟模拟下发一次DOWN_EXG_MSG_RETURN_END指令（结束车辆定位信息交换请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendReturnEndInstruction() {
        // 使用ReturnEndEntity.create构造结束车辆定位信息交换请求消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        byte reasonCode = 1; // 结束原因代码
        ReturnEndEntity entity = ReturnEndEntity.create(vehicleNo, vehicleColor, reasonCode);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_RETURN_END报文:{}", hex);
        log.info("DOWN_EXG_MSG_RETURN_END报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_RETURN_END指令（结束车辆定位信息交换请求消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每13分钟模拟下发一次DOWN_EXG_MSG_APPLY_FOR_MONITOR_STARTUP_ACK指令（申请交换指定车辆定位信息应答消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendApplyForMonitorStartupAckInstruction() {
        // 使用ApplyForMonitorStartupAckEntity.create构造申请交换指定车辆定位信息应答消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        byte result = 0; // 应答结果：0-成功
        byte sourceDateType = 0;
        long sourceMsgSn = 233;
        ApplyForMonitorStartupAckEntity entity = ApplyForMonitorStartupAckEntity.create( vehicleNo,  vehicleColor,  sourceDateType,  sourceMsgSn,  result);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_APPLY_FOR_MONITOR_STARTUP_ACK报文:{}", hex);
        log.info("DOWN_EXG_MSG_APPLY_FOR_MONITOR_STARTUP_ACK报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_APPLY_FOR_MONITOR_STARTUP_ACK指令（申请交换指定车辆定位信息应答消息），车牌号: {}, 应答结果: {}", vehicleNo, result);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每14分钟模拟下发一次DOWN_EXG_MSG_APPLY_FOR_MONITOR_END_ACK指令（取消交换指定车辆定位信息应答消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendApplyForMonitorEndAckInstruction() {
        // 使用ApplyForMonitorEndAckEntity.create构造取消交换指定车辆定位信息应答消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        byte result = 0; // 应答结果：0-成功
        int sourceDateType = 0;
        long sourceMsgSn = 233;
        ApplyForMonitorEndAckEntity entity = ApplyForMonitorEndAckEntity.create( vehicleNo,  vehicleColor,  sourceDateType,  sourceMsgSn,  result);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_APPLY_FOR_MONITOR_END_ACK报文:{}", hex);
        log.info("DOWN_EXG_MSG_APPLY_FOR_MONITOR_END_ACK报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_APPLY_FOR_MONITOR_END_ACK指令（取消交换指定车辆定位信息应答消息），车牌号: {}, 应答结果: {}", vehicleNo, result);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每15分钟模拟下发一次DOWN_EXG_MSG_REPORT_DRIVER_INFO指令（上报驾驶员身份信息请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendReportDriverInfoInstruction() {
        // 使用DownReportDriverInfoEntity.create构造上报驾驶员身份信息请求消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        byte flag = 1; // 标志位
        DownReportDriverInfoEntity entity = DownReportDriverInfoEntity.create(vehicleNo, vehicleColor, flag);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_REPORT_DRIVER_INFO报文:{}", hex);
        log.info("DOWN_EXG_MSG_REPORT_DRIVER_INFO报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_REPORT_DRIVER_INFO指令（上报驾驶员身份信息请求消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每16分钟模拟下发一次DOWN_EXG_MSG_TAKE_EWAYBILL_REQ指令（上报车辆电子运单请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendTakeEwaybillReqInstruction() {
        // 使用TakeEwaybillReqEntity.create构造上报车辆电子运单请求消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        TakeEwaybillReqEntity entity = TakeEwaybillReqEntity.create(vehicleNo, vehicleColor);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_TAKE_EWAYBILL_REQ报文:{}", hex);
        log.info("DOWN_EXG_MSG_TAKE_EWAYBILL_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_TAKE_EWAYBILL_REQ指令（上报车辆电子运单请求消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每16分钟模拟下发一次DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK指令（补发车辆定位信息应答）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendApplyHisgnssDataAckInstruction() {
        // 使用ApplyHisgnssDataAckEntity.create构造补发车辆定位信息应答消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        int sourceDateType = 0;
        long sourceMsgSn = 233;
        byte result = 0;
        ApplyHisgnssDataAckEntity entity = ApplyHisgnssDataAckEntity.create( vehicleNo,  vehicleColor,  sourceDateType,  sourceMsgSn,  result);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK报文:{}", hex);
        log.info("DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK指令（补发车辆定位信息应答），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }


    /**
     * 每16分钟模拟下发一次DOWN_BASE_MSG_DRVLINE_REQ指令（上报车辆行驶路线请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendDrvlineReq() {
        // 使用DrvlineReqEntity.create构造上报车辆行驶路线请求消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        DrvlineReqEntity entity = DrvlineReqEntity.create(vehicleNo, vehicleColor);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_BASE_MSG_DRVLINE_REQ报文:{}", hex);
        log.info("DOWN_BASE_MSG_DRVLINE_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_BASE_MSG_DRVLINE_REQ指令（上报车辆行驶路线请求消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每16分钟模拟下发一次DOWN_BASE_MSG_DRVLINE_ACK指令（车辆行驶路线应答消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendDrvlineAck() {
        // 使用DrvlineAckEntity.create构造车辆行驶路线应答消息体
        String vehicleNo = "京A12345";
        byte vehicleColor = 1; // 蓝色
        long drvLineId = 233;
        byte result = 0;
        String reason = "无";
        DrvlineAckEntity entity = DrvlineAckEntity.create(vehicleNo, vehicleColor, drvLineId, result, reason);

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
            log.info("定时任务下发DOWN_BASE_MSG_DRVLINE_ACK指令（车辆行驶路线应答消息），车牌号: {}", vehicleNo);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
