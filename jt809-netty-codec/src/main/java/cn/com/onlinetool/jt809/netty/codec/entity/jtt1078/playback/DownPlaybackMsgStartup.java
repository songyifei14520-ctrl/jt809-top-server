package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.playback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

import java.time.Instant;
import java.util.Date;

/**
 * 远程录像回放请求消息
 * <p>
 * 链路类型：从链路
 * 消息方向：发起方平台向接收方平台
 * 子业务类型标识：DOWN PLAYBACK MSG STARTUP。
 * 描述：政府视频监管平台向企业视频监控平台、上级政府平台向下级政府平台或跨域地区政府平台
 * 向归属地区政府平台下发该命令请求车辆的录像音视频。 数据体见表 51。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class DownPlaybackMsgStartup extends Base1078Entity {
    private byte channelId;
    /**
     * 音视频类型，0：音视频；1：音频；2：视频，3：视频或音视频
     */
    private byte avitemType;
    /**
     * 码流类型，0：所有码流，1：主码流，2：子码流
     */
    private byte streamType;
    /**
     * 存储器类型，0：所有存储器 1：主存储器，2：灾备存储器
     */
    private byte memType;
    /**
     * 回放起始时间，UTC 时间
     */
    private long playbackStartTime;
    private Date playbackStartTimeStr;
    /**
     * 回放结束时间，UTC 时间
     */
    private long playbackEndTime;
    private Date playbackEndTimeStr;
    /**
     * 时效口令
     */
    private String authorizeCode;
    /**
     * 车辆进入跨域地区后 5min 之内的任一位置，仅跨域访问请求时使用此字段，按照 JT/ T 809—2011 协议 4. 5. 8. 1 的规定
     */
    private String gnssData;


    @Override
    public DownPlaybackMsgStartup decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.channelId = realData.readByte();
        this.avitemType = realData.readByte();
        this.streamType = realData.readByte();
        this.memType = realData.readByte();
        this.playbackStartTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.playbackStartTimeStr = Date.from(Instant.ofEpochSecond(this.playbackStartTime));
        this.playbackEndTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.playbackEndTimeStr = Date.from(Instant.ofEpochSecond(this.playbackEndTime));
        this.authorizeCode = new String(BufUtil.bufToBytes(realData.readBytes(64)));
        if(realData.readableBytes() > 0) {
            this.gnssData = new String(BufUtil.bufToBytes(realData.readBytes(36)));
        }
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 1 + 1 + 1 + 8 + 8 + 64 + 36];
        data[0] = channelId;
        data[1] = avitemType;
        data[2] = streamType;
        data[3] = memType;
        System.arraycopy(ByteUtil.long2Bytes(playbackStartTime), 0, data, 4, 8);
        System.arraycopy(ByteUtil.long2Bytes(playbackStartTime), 0, data, 12, 8);
        byte[] authorizeCodeBytes = authorizeCode.getBytes();
        System.arraycopy(authorizeCodeBytes, 0, data, 20, 64);
        if(gnssData != null && !gnssData.isEmpty()) {
            byte[] gnssDataBytes = gnssData.getBytes();
            System.arraycopy(gnssDataBytes, 0, data, 84, 36);
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_PLAYBACK_MSG);
    }

    public static void main(String[] args) {
        DownPlaybackMsgStartup downPlaybackMsgStartup = new DownPlaybackMsgStartup();
        downPlaybackMsgStartup.setVehicleNo("粤A12340");
        downPlaybackMsgStartup.setVehicleColor((byte) 0x01);
        downPlaybackMsgStartup.setDataType(MsgConstant.SubServiceType.DOWN_PLAYBACK_MSG_STARTUP);
        downPlaybackMsgStartup.setDataLength(1 + 1 + 1 + 1 + 8 + 8 + 64 + 36);
        downPlaybackMsgStartup.setChannelId((byte) 0x01);
        downPlaybackMsgStartup.setAvitemType((byte) 0x00);
        downPlaybackMsgStartup.setStreamType((byte) 0x01);
        downPlaybackMsgStartup.setMemType((byte) 0x01);
        downPlaybackMsgStartup.setPlaybackStartTime(1693708800L);
        downPlaybackMsgStartup.setPlaybackEndTime(1693708800L);
        downPlaybackMsgStartup.setAuthorizeCode("5r99JeevbiNDc2eSyUXeA5DGGrKyj7lCo4UmGKNwUtWVKWX2t7h2vUI9DlMfjjpW");
        downPlaybackMsgStartup.setGnssData("20251008-141930-N39.046565-E117.794291");

        byte[] encode = downPlaybackMsgStartup.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownPlaybackMsgStartup decode = new DownPlaybackMsgStartup().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
