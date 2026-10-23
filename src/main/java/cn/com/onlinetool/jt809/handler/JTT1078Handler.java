package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.MsgConstant;
import org.dromara.common.jt809.netty.codec.entity.BaseEntity;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.authorize.UpAuthorizeMsgStartup;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.download.UpDownloadMsgEndInform;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.download.UpDownloadMsgEndInformAck;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.filelist.DownFilelistMsgAck;
import org.dromara.common.jt809.netty.codec.entity.jtt1078.filelist.UpFilelistMsg;
import org.dromara.common.jt809.netty.codec.util.BufUtil;
import org.dromara.common.jt809.netty.codec.util.ByteUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * TODO 概要描述
 * <p>
 * TODO 详细描述
 *
 * @author syz
 * @since 2025/10/10
 */
@Slf4j
@Component
public class JTT1078Handler implements CommonHandler {
    @Resource
    private TcpChannelMsgManage channelMsgManage;

    private static final String CLIENT_KEY = "clientChannel";

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到主动上传音视频资源目录信息消息", new Date());
        log.info("主动上传音视频资源目录信息消息处理完成");
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {
        BaseEntity baseEntity = new BaseEntity().decode(msg);
        log.info("SubServiceType:{}", baseEntity.getSubDataType());
        if(baseEntity.getSubDataType() == MsgConstant.SubServiceType.UP_FILELIST_MSG) {
            UpFilelistMsg decode = new UpFilelistMsg().decode(msg);
            log.info("主动上传音视频资源目录信息消息解码完成");
            log.info(decode.toString());

            DownFilelistMsgAck downFilelistMsgAck = new DownFilelistMsgAck();
            downFilelistMsgAck.setVehicleNo(decode.getVehicleNo());
            downFilelistMsgAck.setVehicleColor(decode.getVehicleColor());
            downFilelistMsgAck.setDataType(MsgConstant.SubServiceType.DOWN_FILELIST_MSG_ACK);
            downFilelistMsgAck.setDataLength(1 + 1);
            downFilelistMsgAck.setResult((byte) 0x00);
            downFilelistMsgAck.setItemNumber((byte) decode.getItemNum());
            byte[] encode = downFilelistMsgAck.encode();
            sendMsg(encode, downFilelistMsgAck, "主动上传音视频资源目录应答消息");
        } else if(baseEntity.getSubDataType() == MsgConstant.SubServiceType.UP_DOWNLOAD_MSG_END_INFORM) {
            UpDownloadMsgEndInform decode = new UpDownloadMsgEndInform().decode(msg);
            log.info("远程录像下载完成通知消息解码完成");
            log.info(decode.toString());

            UpDownloadMsgEndInformAck upDownloadMsgEndInformAck = new UpDownloadMsgEndInformAck();
            upDownloadMsgEndInformAck.setVehicleNo(decode.getVehicleNo());
            upDownloadMsgEndInformAck.setVehicleColor(decode.getVehicleColor());
            upDownloadMsgEndInformAck.setDataType(MsgConstant.SubServiceType.UP_DOWNLOAD_MSG_END_INFORM_ACK);
            upDownloadMsgEndInformAck.setDataLength(1 + 2);
            upDownloadMsgEndInformAck.setResult((byte) 0x00);
            upDownloadMsgEndInformAck.setSessionId(decode.getSessionId());
            byte[] encode = upDownloadMsgEndInformAck.encode();
            sendMsg(encode, upDownloadMsgEndInformAck, "远程录像下载完成通知应答消息");
        } else if(baseEntity.getSubDataType() == MsgConstant.SubServiceType.UP_AUTHORIZE_MSG_STARTUP) {
            UpAuthorizeMsgStartup decode = new UpAuthorizeMsgStartup().decode(msg);
            log.info("时效口令上报消息解码完成");
            log.info(decode.toString());
        } else {
            log.info("未处理消息:{}", ByteUtil.bytes2Hex(BufUtil.bufToBytes(msg)));
        }

    }

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
}
