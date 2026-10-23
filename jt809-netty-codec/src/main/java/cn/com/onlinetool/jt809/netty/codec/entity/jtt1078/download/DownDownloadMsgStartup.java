package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

import java.time.Instant;
import java.util.Date;

/**
 * 远程录像下载请求消息
 * <p>
 * 链路类型：从链路
 * 消息方向：政府视频监管平台向企业视频监控平台
 * 子业务类型标识：DOWN DOWNLOAD MSG STARTUP。
 * 描述：政府视频监管平台下发该命令给企业视频监控平台下载车辆的录像音视频。 数据体见表 55。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class DownDownloadMsgStartup extends Base1078Entity {
    private byte channelId;
    /**
     * UTC 时间
     */
    private long startTime;
    private Date startTimeStr;
    /**
     * UTC 时间
     */
    private long endTime;
    private Date endTimeStr;
    /**
     * 报警类型，
     * bit0-31 见 JT/ T 808—2011 中表 18 报警 标志位定义；
     */
    private long alarmType1;
    /**
     * bit32-63 见表 9；
     */
    private long alarmType2;
    /**
     * 音视频类型，定义如下：
     * 0x00：音视频；0x01：音频；0x02：视频
     */
    private byte avitemType;
    /**
     * 码流类型，定义如下：
     * 0x01：主码流；0x02：子码流
     */
    private byte streamType;
    /**
     * 存储器类型，定义如下：
     * 0x01：主存储器；0x02：灾备存储器
     */
    private byte memType;
    /**
     * 文件大小，单位字节(BYTE)
     */
    private long fileSize;
    /**
     * 时效口令
     */
    private String authorizeCode;
    /**
     * 车辆进入跨域地区后 5min 之内的任一位置，仅跨域访问请求时使用此字段，按照 JT/ T 809—2011 中协议 4. 5. 8. 1
     */
    private String gnssData;

    @Override
    public DownDownloadMsgStartup decode(ByteBuf buf) {
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
        this.fileSize = BufUtil.buf2UnsignedLong(realData.readBytes(4));
        this.authorizeCode = new String(BufUtil.bufToBytes(realData.readBytes(64)));
        if(realData.readableBytes() > 0) {
            this.gnssData = new String(BufUtil.bufToBytes(realData.readBytes(36)));
        }
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 8 + 8 + (4 + 4) + 1 + 1 + 1 + 4 + 64 + 36];
        data[0] = channelId;
        System.arraycopy(ByteUtil.long2Bytes(startTime), 0, data, 1, 8);
        System.arraycopy(ByteUtil.long2Bytes(endTime), 0, data, 9, 8);
        System.arraycopy(ByteUtil.int2Byte((int)alarmType1), 0, data, 17, 4);
        System.arraycopy(ByteUtil.int2Byte((int)alarmType2), 0, data, 21, 4);
        data[25] = avitemType;
        data[26] = streamType;
        data[27] = memType;
        System.arraycopy(ByteUtil.int2Byte((int)fileSize), 0, data, 28, 4);
        byte[] authorizeCodeBytes = authorizeCode.getBytes();
        System.arraycopy(authorizeCodeBytes, 0, data, 32, 64);
        if(gnssData != null && !gnssData.isEmpty()) {
            byte[] gnssDataBytes = gnssData.getBytes();
            System.arraycopy(gnssDataBytes, 0, data, 96, 36);
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_DOWNLOAD_MSG);
    }

    public static void main(String[] args) {
        DownDownloadMsgStartup downDownloadMsgStartup = new DownDownloadMsgStartup();
        downDownloadMsgStartup.setVehicleNo("粤A12340");
        downDownloadMsgStartup.setVehicleColor((byte) 0x01);
        downDownloadMsgStartup.setDataType(MsgConstant.SubServiceType.DOWN_DOWNLOAD_MSG_STARTUP);
        downDownloadMsgStartup.setDataLength(1 + 8 + 8 + (4 + 4) + 1 + 1 + 1 + 4 + 64 + 36);
        downDownloadMsgStartup.setChannelId((byte) 0x01);
        downDownloadMsgStartup.setStartTime(1693708800L);
        downDownloadMsgStartup.setEndTime(1693708800L);
        downDownloadMsgStartup.setAlarmType1(0x00);
        downDownloadMsgStartup.setAlarmType2(0x00);
        downDownloadMsgStartup.setAvitemType((byte) 0x00);
        downDownloadMsgStartup.setStreamType((byte) 0x01);
        downDownloadMsgStartup.setMemType((byte) 0x01);
        downDownloadMsgStartup.setFileSize(1024);
        downDownloadMsgStartup.setAuthorizeCode("5r99JeevbiNDc2eSyUXeA5DGGrKyj7lCo4UmGKNwUtWVKWX2t7h2vUI9DlMfjjpW");
        downDownloadMsgStartup.setGnssData("20251008-141930-N39.046565-E117.794291");

        byte[] encode = downDownloadMsgStartup.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownDownloadMsgStartup decode = new DownDownloadMsgStartup().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
