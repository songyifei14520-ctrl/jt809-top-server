package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.realvideo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

/**
 * 主动请求停止实时音视频传输应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：企业视频监控平台向政府视频监管平台
 * 子业务类型标识：UP REALVIDEO MSG END ACK。
 * 描述：企业视频监控平台应答政府视频监管平台发送的主动请求停止实时音视频传输消息，数据体见表 45。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpRealvideoMsgEndAck extends Base1078Entity {
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
        return super.encode(MsgConstant.PrimaryServiceType.UP_REALVIDEO_MSG);
    }

    public static void main(String[] args) {
        UpRealvideoMsgEndAck upRealvideoMsgEndAck = new UpRealvideoMsgEndAck();
        upRealvideoMsgEndAck.setVehicleNo("粤A12340");
        upRealvideoMsgEndAck.setVehicleColor((byte) 0x01);
        upRealvideoMsgEndAck.setDataType(MsgConstant.SubServiceType.UP_REALVIDEO_MSG_END_ACK);
        upRealvideoMsgEndAck.setDataLength(1);
        upRealvideoMsgEndAck.setResult((byte) 0x00);

        byte[] encode = upRealvideoMsgEndAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
