package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.playback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

/**
 * 远程录像回放控制应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：企业视频监控平台往政府视频监管平台
 * 子业务类型标识：UP PLAYBACK MSG CONTROL ACK。
 * 描述：企业视频监控平台应答政府视频监管平台下发的回放控制消息。 数据体见表 54。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpPlaybackMsgControlAck extends Base1078Entity {
    /**
     * 应答结果，定义如下：
     * 0x00：成功；
     * 0x01：失败；
     * 0x02：不支持；
     * 0x03：会话结束
     */
    private byte result;

    @Override
    public byte[] encode() {
        byte[] data = new byte[] {result};
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_PLAYBACK_MSG);
    }

    public static void main(String[] args) {
        UpPlaybackMsgControlAck upPlaybackMsgControlAck = new UpPlaybackMsgControlAck();
        upPlaybackMsgControlAck.setVehicleNo("粤A12340");
        upPlaybackMsgControlAck.setVehicleColor((byte) 0x01);
        upPlaybackMsgControlAck.setDataType(MsgConstant.SubServiceType.UP_PLAYBACK_MSG_CONTROL_ACK);
        upPlaybackMsgControlAck.setDataLength(1);
        upPlaybackMsgControlAck.setResult((byte) 0x00);

        byte[] encode = upPlaybackMsgControlAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
