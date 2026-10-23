package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.playback;

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
 * 远程录像回放控制消息
 * <p>
 * 链路类型：从链路
 * 消息方向：政府视频监管平台向企业视频监控平台
 * 子业务类型标识：DOWN PLAYBACK MSG CONTROL。
 * 描述：政府视频监管平台下发该命令给企业视频监控平台对回放进行控制。 数据体见表 53。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class DownPlaybackMsgControl extends Base1078Entity {
    /**
     * 0x00：正常回放；
     * 0x01：暂停回放；
     * 0x02：结束回放；
     * 0x03：快进回放；
     * 0x04：关键帧快退回放；
     * 0x05：拖动回放；
     * 0x06：关键帧播放
     */
    private byte controlType;
    /**
     * 快进或快退倍数，回放控制为 0x03 和 0x04 时，此字段内容有效，否则置 0。
     * 0x00：无效；
     * 0x01：1 倍；
     * 0x02：2 倍；
     * 0x03：4 倍；
     * 0x04：8 倍；
     * 0x05：16 倍
     */
    private byte fastTime;
    /**
     * 拖动位置，用 UTC 时间表示，回放控制
     * 为 0x05 时，此字段内容有效
     */
    private long dateTime;
    private Date dateTimeStr;


    @Override
    public DownPlaybackMsgControl decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.controlType = realData.readByte();
        this.fastTime = realData.readByte();
        this.dateTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.dateTimeStr = Date.from(Instant.ofEpochSecond(this.dateTime));
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 1 + 8];
        data[0] = controlType;
        data[1] = fastTime;
        System.arraycopy(ByteUtil.long2Bytes(dateTime), 0, data, 2, 8);
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_PLAYBACK_MSG);
    }

    public static void main(String[] args) {
        DownPlaybackMsgControl downPlaybackMsgControl = new DownPlaybackMsgControl();
        downPlaybackMsgControl.setVehicleNo("粤A12340");
        downPlaybackMsgControl.setVehicleColor((byte) 0x01);
        downPlaybackMsgControl.setDataType(MsgConstant.SubServiceType.DOWN_PLAYBACK_MSG_CONTROL);
        downPlaybackMsgControl.setDataLength(1 + 1 + 8);
        downPlaybackMsgControl.setControlType((byte) 0x01);
        downPlaybackMsgControl.setFastTime((byte) 0x00);
        downPlaybackMsgControl.setDateTime(1693708800L);

        byte[] encode = downPlaybackMsgControl.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownPlaybackMsgControl decode = new DownPlaybackMsgControl().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
