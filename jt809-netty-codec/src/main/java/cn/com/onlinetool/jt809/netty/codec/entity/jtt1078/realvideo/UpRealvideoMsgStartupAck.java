package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.realvideo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

/**
 * 实时音视频请求应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：接收方平台向发起方平台
 * 子业务类型标识：UP REALVIDEO MSG STARTUP ACK。
 * 描述：企业视频监控平台应答政府视频监管平台发送的车辆实时音视频请求消息。 数据体见表 43。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpRealvideoMsgStartupAck extends Base1078Entity {
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
     * 企业视频服务器 IP 地址
     */
    private String serverIp;
    /**
     * 企业视频服务器端口号
     */
    private int serverPort;

    @Override
    public byte[] encode() {
        byte[] data = null;
        if(this.result == 0) {
            data = new byte[1 + 32 + 2];
            data[0] = result;
            byte[] serverIpBytes = serverIp.getBytes();
            System.arraycopy(serverIpBytes, 0, data, 1, Math.min(serverIpBytes.length, 32));
            System.arraycopy(ByteUtil.short2Bytes((short) this.serverPort), 0, data, 33, 2);
            this.setData(BufUtil.bytesToBuf(data));
        } else {
            data = new byte[1];
            data[0] = result;
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_REALVIDEO_MSG);
    }

    public static void main(String[] args) {
        UpRealvideoMsgStartupAck upRealvideoMsgStartupAck = new UpRealvideoMsgStartupAck();
        upRealvideoMsgStartupAck.setVehicleNo("粤A88888");
        upRealvideoMsgStartupAck.setVehicleColor((byte) 0x01);
        upRealvideoMsgStartupAck.setDataType(MsgConstant.SubServiceType.UP_REALVIDEO_MSG_STARTUP_ACK);
        upRealvideoMsgStartupAck.setDataLength(1 + 32 + 2);
        upRealvideoMsgStartupAck.setResult((byte) 0x00);
        upRealvideoMsgStartupAck.setServerIp("127.0.0.1");
        upRealvideoMsgStartupAck.setServerPort(1078);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String msgBody = objectMapper.writeValueAsString(upRealvideoMsgStartupAck);
            System.out.println(msgBody);
            UpRealvideoMsgStartupAck test = objectMapper.readValue(msgBody, UpRealvideoMsgStartupAck.class);
            System.out.println(test);
            byte[] testBytes = test.encode();
            System.out.println(ByteUtil.bytes2Hex(testBytes).toUpperCase());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
