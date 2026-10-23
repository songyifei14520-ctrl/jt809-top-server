package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.bean.UpExgMsg;
import cn.com.onlinetool.jt809.util.ByteArrayUtil;
import cn.com.onlinetool.jt809.util.PacketUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.MsgConstant;
import org.dromara.common.jt809.netty.codec.entity.BaseEntity;
import org.dromara.common.jt809.netty.codec.entity.exgMsg.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author choice
 * 车辆动态信息交换逻辑处理
 * @date 2019-06-28 15:49
 *
 */
@Slf4j
@Component
public class UpExgMsgHandler implements CommonHandler{

    // 车辆实时定位信息报文计数器
    private final AtomicLong realLocationCounter = new AtomicLong(0);

    // 上次打印统计信息的时间
    private volatile long lastPrintTime = System.currentTimeMillis();

    // 统计信息打印间隔（毫秒）- 每30秒打印一次
    private static final long PRINT_INTERVAL = 30000;

    // 日期时间格式化器
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {

        ByteBuf copiedBuf = Unpooled.copiedBuffer(PacketUtil.message2Bytes(msg));
        BaseEntity baseEntity = new BaseEntity().decode(copiedBuf);

        switch (baseEntity.getHeader().getMsgId()){
            case MsgConstant.SubServiceType.UP_EXG_MSG_REGISTER:
                RegisterEntity registerEntity = new RegisterEntity().decode(copiedBuf);
                log.info("UP_EXG-车辆注册信息：" + JSONObject.toJSONString(registerEntity));
                //upExgRegisterHandler(upExgMsg);
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_SAFETY_TERMINAL:
                org.dromara.common.jt809.netty.codec.entity.exgMsg.SafetyTerminalEntity safetyTerminalEntity = new org.dromara.common.jt809.netty.codec.entity.exgMsg.SafetyTerminalEntity().decode(copiedBuf);
                log.info("UP_EXG-车辆智能监控终端安装信息：" + JSONObject.toJSONString(safetyTerminalEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REAL_LOCATION:
                RealLocationEntity realLocationEntity = new RealLocationEntity().decode(copiedBuf);
                // 增加实时定位信息计数
                long currentCount = realLocationCounter.incrementAndGet();
                log.info("UP_EXG-车辆实时定位信息：" + JSONObject.toJSONString(realLocationEntity));
                // 打印统计信息
                printLocationStatistics(currentCount);
                //upExgMsgRealLocationHandler(upExgMsg);
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_HISTORY_LOCATION:
                HistoryLocationEntity historyLocationEntity = new HistoryLocationEntity().decode(copiedBuf);
                log.info("UP_EXG-车辆历史定位信息：" + JSONObject.toJSONString(historyLocationEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_RETURN_STARTUP_ACK:
                ReturnStartupAckEntity returnStartupAckEntity = new ReturnStartupAckEntity().decode(copiedBuf);
                log.info("UP_EXG-启动车辆定位信息交换应答：" + JSONObject.toJSONString(returnStartupAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_RETURN_END_ACK:
                ReturnEndAckEntity returnEndAckEntity = new ReturnEndAckEntity().decode(copiedBuf);
                log.info("UP_EXG-结束车辆定位信息交换应答：" + JSONObject.toJSONString(returnEndAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_APPLY_FOR_MONITOR_STARTUP:
                ApplyForMonitorStartupEntity applyForMonitorStartupEntity = new ApplyForMonitorStartupEntity().decode(copiedBuf);
                log.info("UP_EXG-申请监控开始：" + JSONObject.toJSONString(applyForMonitorStartupEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_APPLY_FOR_MONITOR_END:
                ApplyForMonitorEndEntity applyForMonitorEndEntity = new ApplyForMonitorEndEntity().decode(copiedBuf);
                log.info("UP_EXG-申请监控结束：" + JSONObject.toJSONString(applyForMonitorEndEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_APPLY_HISGNSSDATA_REQ:
                ApplyHisgnssDataReqEntity applyHisgnssDataReqEntity = new ApplyHisgnssDataReqEntity().decode(copiedBuf);
                log.info("UP_EXG-申请历史GPS数据：" + JSONObject.toJSONString(applyHisgnssDataReqEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO_ACK:
                ReportDriverInfoAckEntity reportDriverInfoAckEntity = new ReportDriverInfoAckEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(reportDriverInfoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_TAKE_EWAYBILL_ACK:
                TakeEwaybillAckEntity takeEwaybillAckEntity = new TakeEwaybillAckEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(takeEwaybillAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO:
                AutoReportDriverInfoEntity reportDriverInfoEntity = new AutoReportDriverInfoEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(reportDriverInfoEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_EWAYBILL_INFO:
                ReportEwaybillInfoEntity reportEwaybillInfoEntity = new ReportEwaybillInfoEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(reportEwaybillInfoEntity));
                break;
            case MsgConstant.SubServiceType.UP_BASE_MSG_DRVLINE_INFO:
                DrvlineInfoEntity drvlineInfoEntity = new DrvlineInfoEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(drvlineInfoEntity));
                break;
            default:
                break;
        }

    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

        ByteBuf copiedBuf = msg;
        BaseExgMsgEntity baseEntity = new BaseExgMsgEntity().decode(copiedBuf);

        switch (baseEntity.getDataType()){
            case MsgConstant.SubServiceType.UP_EXG_MSG_REGISTER:
                RegisterEntity registerEntity = new RegisterEntity().decode(copiedBuf);
                log.info("UP_EXG-车辆注册信息：" + JSONObject.toJSONString(registerEntity));
                //upExgRegisterHandler(upExgMsg);
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REAL_LOCATION:
                RealLocationEntity realLocationEntity = new RealLocationEntity().decode(copiedBuf);
                // 增加实时定位信息计数
                long currentCount = realLocationCounter.incrementAndGet();
                log.info("UP_EXG-车辆实时定位信息：" + JSONObject.toJSONString(realLocationEntity));
                // 打印统计信息
                printLocationStatistics(currentCount);
                //upExgMsgRealLocationHandler(upExgMsg);
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_HISTORY_LOCATION:
                HistoryLocationEntity historyLocationEntity = new HistoryLocationEntity().decode(copiedBuf);
                log.info("UP_EXG-车辆历史定位信息：" + JSONObject.toJSONString(historyLocationEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_RETURN_STARTUP_ACK:
                ReturnStartupAckEntity returnStartupAckEntity = new ReturnStartupAckEntity().decode(copiedBuf);
                log.info("UP_EXG-启动车辆定位信息交换应答：" + JSONObject.toJSONString(returnStartupAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_RETURN_END_ACK:
                ReturnEndAckEntity returnEndAckEntity = new ReturnEndAckEntity().decode(copiedBuf);
                log.info("UP_EXG-结束车辆定位信息交换应答：" + JSONObject.toJSONString(returnEndAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_APPLY_FOR_MONITOR_STARTUP:
                ApplyForMonitorStartupEntity applyForMonitorStartupEntity = new ApplyForMonitorStartupEntity().decode(copiedBuf);
                log.info("UP_EXG-申请监控开始：" + JSONObject.toJSONString(applyForMonitorStartupEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_APPLY_FOR_MONITOR_END:
                ApplyForMonitorEndEntity applyForMonitorEndEntity = new ApplyForMonitorEndEntity().decode(copiedBuf);
                log.info("UP_EXG-申请监控结束：" + JSONObject.toJSONString(applyForMonitorEndEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_APPLY_HISGNSSDATA_REQ:
                ApplyHisgnssDataReqEntity applyHisgnssDataReqEntity = new ApplyHisgnssDataReqEntity().decode(copiedBuf);
                log.info("UP_EXG-申请历史GPS数据：" + JSONObject.toJSONString(applyHisgnssDataReqEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO_ACK:
                ReportDriverInfoAckEntity reportDriverInfoAckEntity = new ReportDriverInfoAckEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(reportDriverInfoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_TAKE_EWAYBILL_ACK:
                TakeEwaybillAckEntity takeEwaybillAckEntity = new TakeEwaybillAckEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(takeEwaybillAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO:
                AutoReportDriverInfoEntity reportDriverInfoEntity = new AutoReportDriverInfoEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(reportDriverInfoEntity));
                break;
            case MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_EWAYBILL_INFO:
                ReportEwaybillInfoEntity reportEwaybillInfoEntity = new ReportEwaybillInfoEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(reportEwaybillInfoEntity));
                break;
            case MsgConstant.SubServiceType.UP_BASE_MSG_DRVLINE_INFO:
                DrvlineInfoEntity drvlineInfoEntity = new DrvlineInfoEntity().decode(copiedBuf);
                log.info("UP_EXG-上报驾驶员信息：" + JSONObject.toJSONString(drvlineInfoEntity));
                break;
            default:
                break;
        }

    }


    /**
     * 车辆注册信息处理
     * @param msg
     */
    private void upExgRegisterHandler(UpExgMsg msg){
        int index = 0;
        String platformId = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getData(),index,11));
        index += 11;
        String producerId = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getData(),index,11));
        index += 11;
        String terminalModelType = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getData(),index,8));
        index += 8;
        String terminalId = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getData(),index,7));
        index += 7;
        String terminalSimCode = ByteArrayUtil.bytes2gbkString(ByteArrayUtil.subBytes(msg.getData(),index,12));
        index += 12;

        UpExgMsg.UpExgMsgRegister register = new UpExgMsg.UpExgMsgRegister();
        register.setPlatformId(platformId);
        register.setProducerId(producerId);
        register.setTerminalModelType(terminalModelType);
        register.setTerminalId(terminalId);
        register.setTerminalSimCode(terminalSimCode);
        msg.setUpExgMsgRegister(register);
        log.info("车辆动态信息交换-车辆注册信息：" + JSONObject.toJSONString(msg));

    }

    /**
     * 车辆实时定位信息处理
     * @param msg
     */
    private void upExgMsgRealLocationHandler(UpExgMsg msg){
        int index = 0;
        int encrypy =  ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getData(),index,1));
        index += 1;
        byte[] dateTime =  ByteArrayUtil.subBytes(msg.getData(),index,7);
        index += 7;
        double lon =  PacketUtil.parseLonOrLat(ByteArrayUtil.subBytes(msg.getData(),index,4));
        index += 4;
        double lat =  PacketUtil.parseLonOrLat(ByteArrayUtil.subBytes(msg.getData(),index,4));
        index += 4;
        int vec1 =  ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getData(),index,2));
        index += 2;
        int vec2 =  ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getData(),index,2));
        index += 2;
        int vec3 =  ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getData(),index,4));
        index += 4;
        int direction =  ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getData(),index,2));
        index += 2;
        int altitude =  ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(msg.getData(),index,2));
        index += 2;
        String state =  ByteArrayUtil.bytes2bitStr(ByteArrayUtil.subBytes(msg.getData(),index,4));
        index += 4;
        String alarm =  ByteArrayUtil.bytes2bitStr(ByteArrayUtil.subBytes(msg.getData(),index,4));
        index += 4;

        UpExgMsg.UpExgMsgRealLocation location = new UpExgMsg.UpExgMsgRealLocation();
        location.setEncrypy(encrypy);
        location.setDateTime(parseDateTime(dateTime));
        location.setLon(lon);
        location.setLat(lat);
        location.setVec1(vec1);
        location.setVec2(vec2);
        location.setVec3(vec3);
        location.setDirection(direction);
        location.setAltitude(altitude);

        UpExgMsg.UpExgMsgRealLocation.State upExgState = new UpExgMsg.UpExgMsgRealLocation.State();
        int stateIdx = 0;
        upExgState.setAcc(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setLocation(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setLon(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setLat(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setOperation(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setLatLonEncryption(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setForwardCollisionWarning(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setLaneDepartureWarning(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setLoadRating(Integer.parseInt(state.substring(stateIdx,++stateIdx))*2+Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setOilPath(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setElectricCircuit(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        upExgState.setDoor(Integer.parseInt(state.substring(stateIdx,++stateIdx)));
        location.setState(upExgState);

        UpExgMsg.UpExgMsgRealLocation.Alarm upExgAlarm = new UpExgMsg.UpExgMsgRealLocation.Alarm();
        int alarmIdx = 0;
        upExgAlarm.setEmergencyAlarm(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setOverSpeed(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setFatigueDriving(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setEarlyWarning(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setGnssModuleError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setGnssAntennaDisconnect(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setGnssAntennaShortCircuit(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setTerminalMainPowerUnderVoltage(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setTerminalMainPowerFailure(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setTerminalLcdError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setTtsModuleError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setCameraError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setIcModuleError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setOverspeedWarning(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setDriverFatigueMonitor(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setBanOnDrivingWarning(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setCumulativeDrivingTimeout(Integer.parseInt(alarm.substring(alarmIdx += 2,++alarmIdx)));
        upExgAlarm.setStopTimeout(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setInOutArea(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setInOutRoute(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setRoadDrivingTimeout(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setLaneDepartureError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setVssError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setOilError(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setStolen(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setIllegalIgnition(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setIllegalMove(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setCollisionRollover(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        upExgAlarm.setRolloverWarning(Integer.parseInt(alarm.substring(alarmIdx,++alarmIdx)));
        location.setAlarm(upExgAlarm);

        msg.setUpExgMsgRealLocation(location);
        log.info("车辆动态信息交换-车辆实时定位信息：" + JSONObject.toJSONString(msg));
    }

    private String parseDateTime(byte[] dateTime){
        int year = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(dateTime,2,2));
        int month = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(dateTime,1,1));
        int day = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(dateTime,0,1));
        int hour = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(dateTime,4,1));
        int minute = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(dateTime,5,1));
        int second = ByteArrayUtil.bytes2int(ByteArrayUtil.subBytes(dateTime,6,1));
        return "" + year +
                (month < 10 ? "0" + month : month) +
                (day < 10 ? "0" + day : day) +
                (hour < 10 ? "0" + hour : hour) +
                (minute < 10 ? "0" + minute : minute) +
                (second < 10 ? "0" + second : second);
    }

    /**
     * 打印车辆实时定位信息统计
     * @param currentCount 当前计数
     */
    private void printLocationStatistics(long currentCount) {
        long currentTime = System.currentTimeMillis();

        // 检查是否到了打印统计信息的时间
        if (currentTime - lastPrintTime >= PRINT_INTERVAL) {
            // 使用synchronized确保线程安全
            synchronized (this) {
                // 双重检查，避免多线程重复打印
                if (currentTime - lastPrintTime >= PRINT_INTERVAL) {
                    String currentDateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);

                    // 计算时间间隔（秒）
                    double intervalSeconds = (currentTime - lastPrintTime) / 1000.0;

                    // 计算平均每秒接收的报文数量（TPS）
                    double tps = intervalSeconds > 0 ? currentCount / (currentTime / 1000.0) : 0;

                    log.info("=== 车辆实时定位信息统计 ===");
                    log.info("统计时间: {}", currentDateTime);
                    log.info("累计接收实时定位报文数量: {} 条", currentCount);
                    log.info("平均每秒接收报文数量(TPS): {} 条/秒", tps);
                    log.info("最近 {} 秒内处理的报文", intervalSeconds);
                    log.info("================================");

                    // 更新上次打印时间
                    lastPrintTime = currentTime;
                }
            }
        }
    }

    /**
     * 获取当前实时定位信息统计数据
     * @return 统计数据
     */
    public long getRealLocationCount() {
        return realLocationCounter.get();
    }

    /**
     * 重置统计计数器
     */
    public void resetStatistics() {
        realLocationCounter.set(0);
        lastPrintTime = System.currentTimeMillis();
        log.info("车辆实时定位信息统计计数器已重置");
    }

}
