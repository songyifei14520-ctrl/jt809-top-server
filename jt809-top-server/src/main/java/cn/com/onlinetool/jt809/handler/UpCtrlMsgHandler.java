package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.util.PacketUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.dto.GnssData2019;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.ctrlMsg.*;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpCtrlMsgHandler implements CommonHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {

        ByteBuf copiedBuf = Unpooled.copiedBuffer(PacketUtil.message2Bytes(msg));
        BaseEntity baseEntity = new BaseEntity().decode(copiedBuf);
        switch (baseEntity.getHeader().getMsgId()){
            case MsgConstant.SubServiceType.UP_CTRL_MSG_MONITOR_VEHICLE_ACK:
                MonitorVehicleAckEntity monitorVehicleAckEntity = new MonitorVehicleAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-车辆单向监听应答：" + JSONObject.toJSONString(monitorVehicleAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_PHOTO_ACK:
                TakePhotoAckEntity takePhotoAckEntity = new TakePhotoAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-车辆拍照应答：" + JSONObject.toJSONString(takePhotoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_TEXT_INFO_ACK:
                TextInfoAckEntity textInfoAckEntity = new TextInfoAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-文本信息应答：" + JSONObject.toJSONString(textInfoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_TRAVEL_ACK:
                TakeTravelAckEntity takeTravelAckEntity = new TakeTravelAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-车辆轨迹回传应答：" + JSONObject.toJSONString(takeTravelAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_EMERGENCY_MONITORING_ACK:
                EmergencyMonitoringAckEntity emergencyMonitoringAckEntity = new EmergencyMonitoringAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-紧急报警应答：" + JSONObject.toJSONString(emergencyMonitoringAckEntity));
                break;
            default:
                break;
        }
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

        ByteBuf copiedBuf = msg;
        BaseCtrlMsgEntity baseEntity = new BaseCtrlMsgEntity().decode(msg);
        switch (baseEntity.getDataType()){
            case MsgConstant.SubServiceType.UP_CTRL_MSG_MONITOR_VEHICLE_ACK:
                MonitorVehicleAckEntity monitorVehicleAckEntity = new MonitorVehicleAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-车辆单向监听应答：" + JSONObject.toJSONString(monitorVehicleAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_PHOTO_ACK:
                TakePhotoAckEntity takePhotoAckEntity = new TakePhotoAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-车辆拍照应答：" + JSONObject.toJSONString(takePhotoAckEntity));
                log.info("车辆拍照应答 GnssData2019 is {}", JSONObject.toJSONString(GnssData2019.decode(takePhotoAckEntity.getGnssData())));
                log.info("车辆拍照应答 GnssData hex is {}", ByteUtil.bytes2Hex(GnssData2019.decode(takePhotoAckEntity.getGnssData()).getGnssData()) );
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_TEXT_INFO_ACK:
                TextInfoAckEntity textInfoAckEntity = new TextInfoAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-文本信息应答：" + JSONObject.toJSONString(textInfoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_TRAVEL_ACK:
                TakeTravelAckEntity takeTravelAckEntity = new TakeTravelAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-车辆轨迹回传应答：" + JSONObject.toJSONString(takeTravelAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_CTRL_MSG_EMERGENCY_MONITORING_ACK:
                EmergencyMonitoringAckEntity emergencyMonitoringAckEntity = new EmergencyMonitoringAckEntity().decode(copiedBuf);
                log.info("UP_CTRL-紧急报警应答：" + JSONObject.toJSONString(emergencyMonitoringAckEntity));
                break;
            default:
                break;
        }

    }
}
