package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.realvideo;

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

/**
 * 主动请求停止实时音视频传输消息
 * <p>
 * 链路类型：从链路
 * 消息方向：政府视频监管平台向企业视频监控平台
 * 子业务类型标识：DOWN REALVIDEO MSG END。
 * 描述：政府视频监管平台下发该命令给企业视频监控平台，主动请求停止车辆的实时音视频传输。
 * 数据体见表 44。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class DownRealvideoMsgEnd extends Base1078Entity {
    private byte channelId;
    /**
     * 音视频类型，定义如下：
     * 0x00：音视频；0x01：音频；0x02：视频
     */
    private byte avitemType;

    @Override
    public DownRealvideoMsgEnd decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.channelId = realData.readByte();
        this.avitemType = realData.readByte();
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 1];
        data[0] = channelId;
        data[1] = avitemType;
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_REALVIDEO_MSG);
    }

    public static void main(String[] args) {
        DownRealvideoMsgEnd downRealvideoMsgEnd = new DownRealvideoMsgEnd();
        downRealvideoMsgEnd.setVehicleNo("粤A12340");
        downRealvideoMsgEnd.setVehicleColor((byte) 0x01);
        downRealvideoMsgEnd.setDataType(MsgConstant.SubServiceType.DOWN_REALVIDEO_MSG_END);
        downRealvideoMsgEnd.setDataLength(1 + 1);
        downRealvideoMsgEnd.setChannelId((byte) 0x01);
        downRealvideoMsgEnd.setAvitemType((byte) 0x00);

        byte[] encode = downRealvideoMsgEnd.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownRealvideoMsgEnd decode = new DownRealvideoMsgEnd().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
