package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

/**
 * 远程录像下载控制请求应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：企业视频监控平台向政府视频监管平台
 * 子业务类型标识：UP DOWNLOAD MSG CONTROL ACK。
 * 描述：企业视频监控平台对政府视频监管平台发送的下载控制请求的应答消息。 数据体见表 60。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpDownloadMsgControlAck extends Base1078Entity {
    /**
     * 应答结果：
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
        return super.encode(MsgConstant.PrimaryServiceType.UP_DOWNLOAD_MSG);
    }

    public static void main(String[] args) {
        UpDownloadMsgControlAck upDownloadMsgControlAck = new UpDownloadMsgControlAck();
        upDownloadMsgControlAck.setVehicleNo("粤A12340");
        upDownloadMsgControlAck.setVehicleColor((byte) 0x01);
        upDownloadMsgControlAck.setDataType(MsgConstant.SubServiceType.UP_DOWNLOAD_MSG_CONTROL_ACK);
        upDownloadMsgControlAck.setDataLength(1);
        upDownloadMsgControlAck.setResult((byte) 0x00);

        byte[] encode = upDownloadMsgControlAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
