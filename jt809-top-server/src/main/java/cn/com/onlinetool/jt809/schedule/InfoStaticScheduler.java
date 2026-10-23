package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.dto.SeriesInfo;
import cn.com.onlinetool.jt809.netty.codec.entity.infoStatic.DownMsgSnInformEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.infoStatic.MsgReqEntity;
import cn.com.onlinetool.jt809.netty.codec.enums.MsgTypeEnum;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息统计消息定时调度器
 * 已禁用 - 通过配置文件控制启用状态
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("infostatic")
public class InfoStaticScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    /**
     * 每5分钟模拟下发一次DOWN_MANAGE_MSG_REQ指令（平台链路连接情况与车辆定位消息传输情况上报请求消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendManageMsgReqInstruction() {
        // 使用MsgReqEntity.create构造平台链路连接情况与车辆定位消息传输情况上报请求消息体
        String platformId = "JC2025PT047"; // 平台ID

        // 修复后（正确）
        long startTime = (System.currentTimeMillis() - 3600000) / 1000; // 转换为秒级
        long endTime = System.currentTimeMillis() / 1000; // 转换为秒级
        MsgReqEntity entity = MsgReqEntity.create(platformId, startTime, endTime);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_MANAGE_MSG_REQ报文:{}", hex);
        log.info("DOWN_MANAGE_MSG_REQ报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_MANAGE_MSG_REQ指令（平台链路连接情况与车辆定位消息传输情况上报请求消息），平台ID: {}, 开始时间: {}, 结束时间: {}", platformId, startTime, endTime);
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

    /**
     * 每6分钟模拟下发一次DOWN_MANAGE_MSG_SN_INFORM指令（下发平台间消息序列号通知消息）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendManageMsgSnInformInstruction() {
        // 使用MsgSnInformEntity.create构造下发平台间消息序列号通知消息体
        List<SeriesInfo> seriesList = new ArrayList<>();
        // 添加序列号信息
        seriesList.add(new SeriesInfo(
            MsgTypeEnum.UP_WARN_MSG_URGE_TODO_ACK.getValue(),
                12345L,
                System.currentTimeMillis()/1000L
        ));

        seriesList.add(new SeriesInfo(
            MsgTypeEnum.UP_EXG_MSG_REPORT_DRIVER_INFO.getValue(),
            12345L,
            System.currentTimeMillis()/1000L
        ));

        DownMsgSnInformEntity entity = DownMsgSnInformEntity.create(seriesList);

        ByteBuf copiedBuf = Unpooled.copiedBuffer(entity.encode());
        String hex = BufUtil.buf2HexStr(copiedBuf);
        log.info("DOWN_MANAGE_MSG_SN_INFORM报文:{}", hex);
        log.info("DOWN_MANAGE_MSG_SN_INFORM报文:{}", hex);

        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(entity.encode());
            log.info("定时任务下发DOWN_MANAGE_MSG_SN_INFORM指令（下发平台间消息序列号通知消息），序列号信息数量: {}", seriesList.size());
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }
}
