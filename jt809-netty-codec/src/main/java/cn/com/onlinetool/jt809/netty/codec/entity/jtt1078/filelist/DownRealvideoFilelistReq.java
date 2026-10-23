package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.filelist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;
import cn.com.onlinetool.jt809.netty.codec.util.JT809Util;

import java.time.Instant;
import java.util.Date;

/**
 * 查询音视频资源目录请求消息
 * <p>
 * 链路类型：从链路
 * 消息方向：上级平台向下级平台
 * 子业务类型标识：DOWN REALVIDEO FILELIST REQ。
 * 描述：政府视频监管平台向企业视频监控平台，或上级政府平台向下级政府平台发起查询音视频资
 * 源目录请求消息。 数据体见表 49。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class DownRealvideoFilelistReq extends Base1078Entity {
    private byte channelId;
    /**
     * 起始时间，YY-MM-DD-HH-MM-SS，全 0
     * 表示无起始时间条件
     */
    private long startTime;
    private Date startTimeStr;
    /**
     * 终止时间，YY-MM-DD-HH-MM-SS，全 0
     * 表示无终止时间条件
     */
    private long endTime;
    private Date endTimeStr;
    /**
     * 报警类型，
     * bit0-31 见 JT/ T 808—2011 中表 18 报警 标志位定义；
     * 全 0 表示无报警类型条件
     */
    private long alarmType1;
    /**
     * bit32-63 见表 9；
     * 全 0 表示无报警类型条件
     */
    private long alarmType2;
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
     * 时效口令
     */
    private String authorizeCode;
    /**
     * 车辆进入跨域地区后 5min 之内的任一位置，仅跨域访问请求时使用此字段，按照 JT/ T 809—2011 中协议 4. 5. 8. 1
     */
    private String gnssData;

    @Override
    public DownRealvideoFilelistReq decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.channelId = realData.readByte();
        this.startTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.startTimeStr = Date.from(Instant.ofEpochSecond(this.startTime));
        this.endTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.endTimeStr = Date.from(Instant.ofEpochSecond(this.endTime));
        this.alarmType1 = BufUtil.buf2UnsignedLong(realData.readBytes(4));
        this.alarmType2 = BufUtil.buf2UnsignedLong(realData.readBytes(4));
        this.avitemType = realData.readByte();
        this.streamType = realData.readByte();
        this.memType = realData.readByte();
        this.authorizeCode = new String(BufUtil.bufToBytes(realData.readBytes(64)));
        if(realData.readableBytes() > 0) {
            this.gnssData = new String(BufUtil.bufToBytes(realData.readBytes(36)));
        }
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 8 + 8 + (4 + 4) + 1 + 1 + 1 + 64 + 36];
        data[0] = channelId;
        System.arraycopy(ByteUtil.long2Bytes(startTime), 0, data, 1, 8);
        System.arraycopy(ByteUtil.long2Bytes(endTime), 0, data, 9, 8);
        System.arraycopy(ByteUtil.int2Byte((int)alarmType1), 0, data, 17, 4);
        System.arraycopy(ByteUtil.int2Byte((int)alarmType2), 0, data, 21, 4);
        data[25] = avitemType;
        data[26] = streamType;
        data[27] = memType;
        byte[] authorizeCodeBytes = authorizeCode.getBytes();
        System.arraycopy(authorizeCodeBytes, 0, data, 28, 64);
        if(gnssData != null && !gnssData.isEmpty()) {
            byte[] gnssDataBytes = gnssData.getBytes();
            System.arraycopy(gnssDataBytes, 0, data, 92, 36);
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_SEARCH_MSG);
    }

    public static void main(String[] args) {
        DownRealvideoFilelistReq downRealvideoFilelistReq = new DownRealvideoFilelistReq();
        downRealvideoFilelistReq.setVehicleNo("粤A12340");
        downRealvideoFilelistReq.setVehicleColor((byte) 0x01);
        downRealvideoFilelistReq.setDataType(MsgConstant.SubServiceType.DOWN_REALVIDEO_FILELIST_REQ);
        downRealvideoFilelistReq.setDataLength(1 + 8 + 8 + (4 + 4) + 1 + 1 + 1 + 64 + 36);
        downRealvideoFilelistReq.setChannelId((byte) 0x01);
        downRealvideoFilelistReq.setStartTime(1693708800L);
        downRealvideoFilelistReq.setEndTime(1693708800L);
        downRealvideoFilelistReq.setAlarmType1(0x00);
        downRealvideoFilelistReq.setAlarmType2(0x00);
        downRealvideoFilelistReq.setAvitemType((byte) 0x00);
        downRealvideoFilelistReq.setStreamType((byte) 0x01);
        downRealvideoFilelistReq.setMemType((byte) 0x01);
        downRealvideoFilelistReq.setAuthorizeCode("5r99JeevbiNDc2eSyUXeA5DGGrKyj7lCo4UmGKNwUtWVKWX2t7h2vUI9DlMfjjpW");
        downRealvideoFilelistReq.setGnssData("20251008-141930-N39.046565-E117.794291");

        JT809Config.encrypt = true;
        JT809Config.M1 = 56107078;
        JT809Config.IA1 = 37382110;
        JT809Config.IC1 = 50361430;

        byte[] encode = downRealvideoFilelistReq.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        byte[] decode = JT809Util.decode(encode);
        System.out.println(ByteUtil.bytes2Hex(decode).toUpperCase());
        DownRealvideoFilelistReq decodeObj = new DownRealvideoFilelistReq().decode(BufUtil.bytesToBuf(decode));
        System.out.println(decodeObj);
    }
}
