package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.warnMsg.DownInformTipsEntity;
import org.dromara.common.jt809.netty.codec.entity.warnMsg.ExgInformEntity;
import org.dromara.common.jt809.netty.codec.entity.warnMsg.UrgeTodoReqEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 报警消息定时调度器
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("warnmsg")
public class WarnMsgScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每19分钟模拟下发一次DOWN_WARN_MSG_URGE_TODO_REQ指令（报警督办请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendUrgeTodoReqInstruction() {
        // 使用UrgeTodoReqEntity.create构造报警督办请求消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        String platformId = "JC2025PT047"; // 报警平台唯一编码
        int dataType = 0x1202; // 报警消息源子业务类型标识
        long msgSn = System.currentTimeMillis() / 1000; // 报警信息消息序号
        byte warnSrc = 1; // 报警信息来源
        int warnType = 1; // 报警类型
        long warnTime = System.currentTimeMillis() /1000; // 报警时间
        long supervisionId = System.currentTimeMillis() / 1000; // 报警督办ID
        long supervisionEndTime = (System.currentTimeMillis() + 24 * 60 * 60 * 1000)/1000; // 督办截止时间（24小时后）
        byte supervisionLevel = 1; // 督办级别
        String supervisor = "监管员001"; // 督办人
        String supervisorTel = "13800138000"; // 督办人联系电话
        String supervisorEmail = "supervisor@example.com"; // 督办人邮箱
        UrgeTodoReqEntity entity = UrgeTodoReqEntity.create( platformId, dataType, msgSn,
                warnTime, supervisionEndTime,
                supervisionLevel, supervisor, supervisorTel, supervisorEmail);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_WARN_MSG_URGE_TODO_REQ报文:{}", hex);
        log.info("DOWN_WARN_MSG_URGE_TODO_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_WARN_MSG_URGE_TODO_REQ指令（报警督办请求消息），车牌号: {}, 督办级别: {}", vehicleNo, supervisionLevel);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每20分钟模拟下发一次DOWN_WARN_MSG_INFORM_TIPS指令（报警预警消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendInformTipsInstruction() {
        // 使用InformTipsEntity.create构造报警预警消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        String platformId = "12345678901234567890"; // 报警平台唯一编码
        int warnType = 2; // 报警类型
        long startTime = System.currentTimeMillis() - 60 * 1000; // 事件开始时间（1分钟前）
        long endTime = System.currentTimeMillis(); // 事件结束时间（当前时间）
        long warnTime = System.currentTimeMillis();
        String vehicleNoParam = "京A12345"; // 车牌号码
        byte vehicleColorParam = 1; // 车牌颜色
        String srcPlatformId = "09876543210987654321"; // 被报警平台唯一编码
        long drvLineId = 123456L; // 线路ID
        String warnContent = "车辆超速预警，当前速度120km/h，限速80km/h"; // 报警内容
        DownInformTipsEntity entity = DownInformTipsEntity.create(platformId, warnType,
                startTime, endTime, vehicleNoParam, vehicleColorParam,
                srcPlatformId, drvLineId, warnContent, warnTime);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_WARN_MSG_INFORM_TIPS报文:{}", hex);
        log.info("DOWN_WARN_MSG_INFORM_TIPS报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_WARN_MSG_INFORM_TIPS指令（报警预警消息），车牌号: {}, 报警类型: {}", vehicleNo, warnType);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每21分钟模拟下发一次DOWN_WARN_MSG_EXG_INFORM指令（实时交换报警信息消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendExgInformInstruction() {
        // 使用ExgInformEntity.create构造实时交换报警信息消息体
        String vehicleNo = "测B01844";
        byte vehicleColor = 2; // 蓝色
        String platformId = "JC2025PT047"; // 报警平台唯一编码
        int warnType = 3; // 报警类型
        long warnTime = System.currentTimeMillis()/1000;
        long startTime = System.currentTimeMillis() - 3600000; // 事件开始时间（1小时前）
        long endTime = System.currentTimeMillis(); // 事件结束时间（当前时间）
        String vehicleNoParam = "京A12345"; // 车牌号码
        byte vehicleColorParam = 1; // 车牌颜色
        byte[] srcPlatformId = "35010000".getBytes(); // 发起报警平台唯一编码
        long drvLineId = 1001L; // 线路ID
        String warnContent = "车辆超速报警，当前速度超过限制值"; // 报警描述

        ExgInformEntity entity = ExgInformEntity.create(platformId, warnType,
                startTime, endTime, vehicleNoParam, vehicleColorParam,
                srcPlatformId, drvLineId, warnContent, warnTime);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_WARN_MSG_EXG_INFORM报文:{}", hex);
        log.info("DOWN_WARN_MSG_EXG_INFORM报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_WARN_MSG_EXG_INFORM指令（实时交换报警信息消息），车牌号: {}, 报警类型: {}", vehicleNo, warnType);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
