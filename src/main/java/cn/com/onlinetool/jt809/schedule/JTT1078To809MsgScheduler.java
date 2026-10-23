package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.hutool.core.date.DateUtil;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.jt809.netty.codec.JT809Config;
import org.dromara.common.jt809.netty.codec.MsgConstant;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.download.DownDownloadMsgControl;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.download.DownDownloadMsgStartup;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.filelist.DownRealvideoFilelistReq;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.playback.DownPlaybackMsgControl;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.playback.DownPlaybackMsgStartup;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.realvideo.DownRealvideoMsgEnd;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.realvideo.DownRealvideoMsgStartup;
import org.dromara.common.jt809.netty.codec.util.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 测试定时生成1078对应809消息
 * <p>
 * TODO 详细描述
 *
 * @author syz
 * @since 2025/10/9
 */
@Slf4j
@Component
//@ConditionalOnSchedulerEnabled("jtt1078to809msg")
public class JTT1078To809MsgScheduler {

    @Resource
    private TcpChannelMsgManage channelMsgManage;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    private static final String CLIENT_KEY = "clientChannel";


    private void sendMsg(byte[] encode, Object object, String msgName) {
        Channel channel = channelMsgManage.getChannel(CLIENT_KEY);
        if (channel == null) {
            log.warn("定时任务未找到可用通道，key={}", CLIENT_KEY);
            return;
        }
        if (channel.isActive()) {
            channel.writeAndFlush(encode);
            log.info("定时任务下发【{}}】指令:", msgName);
            log.info(ByteUtil.bytes2Hex(encode).toUpperCase());
            log.info(object.toString());
        } else {
            log.warn("定时任务通道不活跃，key={}", CLIENT_KEY);
        }
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void downRealvideoMsgStartup() {
        DownRealvideoMsgStartup downRealvideoMsgStartup = new DownRealvideoMsgStartup();
        downRealvideoMsgStartup.setVehicleNo("粤A18888");
        downRealvideoMsgStartup.setVehicleColor((byte) 0x02);
        downRealvideoMsgStartup.setDataType(MsgConstant.SubServiceType.DOWN_REALVIDEO_MSG_STARTUP);
        downRealvideoMsgStartup.setDataLength(1 + 1 + 64 + 36);
        downRealvideoMsgStartup.setChannelId((byte) 0x01);
        downRealvideoMsgStartup.setAvitemType((byte) 0x00);
        String localAuthorize = redisTemplate.opsForValue().get("JTT1078:AuthorizeCode:Local");
        downRealvideoMsgStartup.setAuthorizeCode(localAuthorize);
        downRealvideoMsgStartup.setGnssData("20251008-141930-N39.046565-E117.794291");
        byte[] encode = downRealvideoMsgStartup.encode();
        sendMsg(encode, downRealvideoMsgStartup, "实时音视频请求消息");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void downRealvideoMsgEnd() {
        DownRealvideoMsgEnd downRealvideoMsgEnd = new DownRealvideoMsgEnd();
        downRealvideoMsgEnd.setVehicleNo("粤A18888");
        downRealvideoMsgEnd.setVehicleColor((byte) 0x02);
        downRealvideoMsgEnd.setDataType(MsgConstant.SubServiceType.DOWN_REALVIDEO_MSG_END);
        downRealvideoMsgEnd.setDataLength(1 + 1);
        downRealvideoMsgEnd.setChannelId((byte) 0x01);
        downRealvideoMsgEnd.setAvitemType((byte) 0x00);
        byte[] encode = downRealvideoMsgEnd.encode();
        sendMsg(encode, downRealvideoMsgEnd, "主动请求停止实时音视频传输消息");
    }

    @Scheduled(cron = "0 */2 * * * ?")
    public void downRealvideoFilelistReq() {
        DownRealvideoFilelistReq downRealvideoFilelistReq = new DownRealvideoFilelistReq();
        downRealvideoFilelistReq.setVehicleNo("粤A18888");
        downRealvideoFilelistReq.setVehicleColor((byte) 0x02);
        downRealvideoFilelistReq.setDataType(MsgConstant.SubServiceType.DOWN_REALVIDEO_FILELIST_REQ);
        downRealvideoFilelistReq.setDataLength(1 + 8 + 8 + (4 + 4) + 1 + 1 + 1 + 64 + 36);
        downRealvideoFilelistReq.setChannelId((byte) 0x01);
        Date startTime = DateUtil.parse("2025-10-15 00:00:00", "yyyy-MM-dd HH:mm:ss");
        Date endTime = DateUtil.parse("2025-10-16 23:59:59", "yyyy-MM-dd HH:mm:ss");
        downRealvideoFilelistReq.setStartTime(startTime.toInstant().getEpochSecond());
        downRealvideoFilelistReq.setEndTime(endTime.toInstant().getEpochSecond());
        downRealvideoFilelistReq.setAlarmType1(0x00);
        downRealvideoFilelistReq.setAlarmType2(0x00);
        downRealvideoFilelistReq.setAvitemType((byte) 0x00);
        downRealvideoFilelistReq.setStreamType((byte) 0x00);
        downRealvideoFilelistReq.setMemType((byte) 0x00);
        String localAuthorize = redisTemplate.opsForValue().get("JTT1078:AuthorizeCode:Local");
        downRealvideoFilelistReq.setAuthorizeCode(localAuthorize);
        downRealvideoFilelistReq.setGnssData("20251008-141930-N39.046565-E117.794291");
        System.out.println("M1:" + JT809Config.M1);
        System.out.println("IA1:" + JT809Config.IA1);
        System.out.println("IC1:" + JT809Config.IC1);
        byte[] encode = downRealvideoFilelistReq.encode();
        System.out.println("Header:" + downRealvideoFilelistReq.getHeader());
        System.out.println(downRealvideoFilelistReq);
        System.out.println("查询音视频资源目录请求消息:" + ByteUtil.bytes2Hex(encode));
        sendMsg(encode, downRealvideoFilelistReq, "查询音视频资源目录请求消息");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void downPlaybackMsgStartup() {
        DownPlaybackMsgStartup downPlaybackMsgStartup = new DownPlaybackMsgStartup();
        downPlaybackMsgStartup.setVehicleNo("粤A18888");
        downPlaybackMsgStartup.setVehicleColor((byte) 0x02);
        downPlaybackMsgStartup.setDataType(MsgConstant.SubServiceType.DOWN_PLAYBACK_MSG_STARTUP);
        downPlaybackMsgStartup.setDataLength(1 + 1 + 1 + 1 + 8 + 8 + 64 + 36);
        downPlaybackMsgStartup.setChannelId((byte) 0x01);
        downPlaybackMsgStartup.setAvitemType((byte) 0x00);
        downPlaybackMsgStartup.setStreamType((byte) 0x01);
        downPlaybackMsgStartup.setMemType((byte) 0x01);
        Date startTime = DateUtil.parse("2025-10-15 00:00:00", "yyyy-MM-dd HH:mm:ss");
        Date endTime = DateUtil.parse("2025-10-16 23:59:59", "yyyy-MM-dd HH:mm:ss");
        downPlaybackMsgStartup.setPlaybackStartTime(startTime.toInstant().getEpochSecond());
        downPlaybackMsgStartup.setPlaybackEndTime(endTime.toInstant().getEpochSecond());
        String localAuthorize = redisTemplate.opsForValue().get("JTT1078:AuthorizeCode:Local");
        downPlaybackMsgStartup.setAuthorizeCode(localAuthorize);
        downPlaybackMsgStartup.setGnssData("20251008-141930-N39.046565-E117.794291");

        byte[] encode = downPlaybackMsgStartup.encode();
        sendMsg(encode, downPlaybackMsgStartup, "远程录像回放请求消息");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void downPlaybackMsgControl() {
        DownPlaybackMsgControl downPlaybackMsgControl = new DownPlaybackMsgControl();
        downPlaybackMsgControl.setVehicleNo("粤A18888");
        downPlaybackMsgControl.setVehicleColor((byte) 0x02);
        downPlaybackMsgControl.setDataType(MsgConstant.SubServiceType.DOWN_PLAYBACK_MSG_CONTROL);
        downPlaybackMsgControl.setDataLength(1 + 1 + 8);
        downPlaybackMsgControl.setControlType((byte) 0x01);
        downPlaybackMsgControl.setFastTime((byte) 0x00);
        downPlaybackMsgControl.setDateTime(1693708800L);

        byte[] encode = downPlaybackMsgControl.encode();
        sendMsg(encode, downPlaybackMsgControl, "远程录像回放控制消息");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void downDownloadMsgStartup() {
        DownDownloadMsgStartup downDownloadMsgStartup = new DownDownloadMsgStartup();
        downDownloadMsgStartup.setVehicleNo("粤A18888");
        downDownloadMsgStartup.setVehicleColor((byte) 0x02);
        downDownloadMsgStartup.setDataType(MsgConstant.SubServiceType.DOWN_DOWNLOAD_MSG_STARTUP);
        downDownloadMsgStartup.setDataLength(1 + 8 + 8 + (4 + 4) + 1 + 1 + 1 + 4 + 64 + 36);
        downDownloadMsgStartup.setChannelId((byte) 0x01);
        Date startTime = DateUtil.parse("2025-10-15 00:00:00", "yyyy-MM-dd HH:mm:ss");
        Date endTime = DateUtil.parse("2025-10-16 23:59:59", "yyyy-MM-dd HH:mm:ss");
        downDownloadMsgStartup.setStartTime(startTime.toInstant().getEpochSecond());
        downDownloadMsgStartup.setEndTime(endTime.toInstant().getEpochSecond());
        downDownloadMsgStartup.setAlarmType1(0x00);
        downDownloadMsgStartup.setAlarmType2(0x00);
        downDownloadMsgStartup.setAvitemType((byte) 0x00);
        downDownloadMsgStartup.setStreamType((byte) 0x02);
        downDownloadMsgStartup.setMemType((byte) 0x01);
        downDownloadMsgStartup.setFileSize(87460355);
        String localAuthorize = redisTemplate.opsForValue().get("JTT1078:AuthorizeCode:Local");
        downDownloadMsgStartup.setAuthorizeCode(localAuthorize);
        downDownloadMsgStartup.setGnssData("20251008-141930-N39.046565-E117.794291");

        byte[] encode = downDownloadMsgStartup.encode();
        sendMsg(encode, downDownloadMsgStartup, "远程录像下载请求消息");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void downDownloadMsgControl() {
        String sessionId = redisTemplate.opsForValue().get("JTT1078:JTT809:download:sessionId:13100014994");
        if(StringUtils.isEmpty(sessionId)) {
            log.warn("sessionId is empty");
            return;
        }
        DownDownloadMsgControl downDownloadMsgControl = new DownDownloadMsgControl();
        downDownloadMsgControl.setVehicleNo("粤A18888");
        downDownloadMsgControl.setVehicleColor((byte) 0x02);
        downDownloadMsgControl.setDataType(MsgConstant.SubServiceType.DOWN_DOWNLOAD_MSG_CONTROL);
        downDownloadMsgControl.setDataLength(2 + 1);
        downDownloadMsgControl.setSessionId(Integer.parseInt(sessionId));
        downDownloadMsgControl.setType((byte) 0x00);

        byte[] encode = downDownloadMsgControl.encode();
        sendMsg(encode, downDownloadMsgControl, "远程录像下载控制请求消息");
    }
}
