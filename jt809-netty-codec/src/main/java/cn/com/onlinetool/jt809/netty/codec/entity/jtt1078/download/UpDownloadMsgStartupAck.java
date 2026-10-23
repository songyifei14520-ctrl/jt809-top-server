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
 * 远程录像下载请求应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：企业视频监控平台向政府视频监管平台
 * 子业务类型标识：UP DOWNLOAD MSG STARTUP ACK。
 * 描述：企业视频监控平台对政府视频监管平台发送的下载车辆音视频请求的应答消息。 数据体见表 56。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpDownloadMsgStartupAck extends Base1078Entity {
    /**
     * 应答结果，定义如下：
     * 0x00：成功；
     * 0x01：失败；
     * 0x02：不支持；
     * 0x03：会话结束；
     * 0x04：时效口令错误；
     * 0x05：不满足跨域条件
     */
    private byte result;
    /**
     * 对应平台文件上传消息的流水号，RESULT 为 0 时有效
     */
    private int sessionId;

    @Override
    public byte[] encode() {
        byte[] data = null;
        if(this.result == 0) {
            data = new byte[1 + 2];
            data[0] = result;
            System.arraycopy(ByteUtil.short2Bytes((short) this.sessionId), 0, data, 1, 2);
        } else {
            data = new byte[1];
            data[0] = result;
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_DOWNLOAD_MSG);
    }

    public static void main(String[] args) {
        UpDownloadMsgStartupAck upDownloadMsgStartupAck = new UpDownloadMsgStartupAck();
        upDownloadMsgStartupAck.setVehicleNo("粤A12340");
        upDownloadMsgStartupAck.setVehicleColor((byte) 0x01);
        upDownloadMsgStartupAck.setDataType(MsgConstant.SubServiceType.UP_DOWNLOAD_MSG_STARTUP_ACK);
        upDownloadMsgStartupAck.setDataLength(1 + 2);
        upDownloadMsgStartupAck.setResult((byte) 0x00);
        upDownloadMsgStartupAck.setSessionId(1234);

        byte[] encode = upDownloadMsgStartupAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
