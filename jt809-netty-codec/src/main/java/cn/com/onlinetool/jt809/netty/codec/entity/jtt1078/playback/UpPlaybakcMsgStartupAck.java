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
 * 远程录像回放请求应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：接收方平台向发起方平台
 * 子业务类型标识：UP PLAYBACK MSG STARTUP ACK。
 * 描述：企业视频监控平台应答政府视频监管平台、下级政府平台应答上级政府平台或归属地区政府
 * 平台应答跨域地区政府平台发送的录像回放请求消息。 数据体见表 52。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpPlaybakcMsgStartupAck extends Base1078Entity {
    /**
     * 企业视频服务器 IP 地址
     */
    private String serverIp;
    /**
     * 企业视频服务器端口号
     */
    private int serverPort;
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

    @Override
    public byte[] encode() {
        byte[] data = null;
        if(this.result == 0) {
            data = new byte[32 + 2 + 1];
            byte[] serverIpBytes = serverIp.getBytes();
            System.arraycopy(serverIpBytes, 0, data, 0, Math.min(serverIpBytes.length, 32));
            System.arraycopy(ByteUtil.short2Bytes((short) this.serverPort), 0, data, 32, 2);
            data[34] = result;
        } else {
            data = new byte[1];
            data[0] = result;
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_PLAYBACK_MSG);
    }

    public static void main(String[] args) {
        UpPlaybakcMsgStartupAck upPlaybakcMsgStartupAck = new UpPlaybakcMsgStartupAck();
        upPlaybakcMsgStartupAck.setVehicleNo("粤A12340");
        upPlaybakcMsgStartupAck.setVehicleColor((byte) 0x01);
        upPlaybakcMsgStartupAck.setDataType(MsgConstant.SubServiceType.UP_PLAYBACK_MSG_STARTUP_ACK);
        upPlaybakcMsgStartupAck.setDataLength(1 + 32 + 2);
        upPlaybakcMsgStartupAck.setServerIp("127.0.0.1");
        upPlaybakcMsgStartupAck.setServerPort(1078);
        upPlaybakcMsgStartupAck.setResult((byte) 0x00);

        byte[] encode = upPlaybakcMsgStartupAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
