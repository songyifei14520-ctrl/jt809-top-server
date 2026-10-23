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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 远程录像下载完成通知消息
 * <p>
 * 链路类型：主链路
 * 消息方向：企业视频监控平台向政府视频监管平台
 * 子业务类型标识：UP DOWNLOAD MSG END INFORM。
 * 描述：企业视频监控平台向政府视频监管平台发送，通知政府视频监管平台录像文件已从终端下载完成。 数据体见表 57。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpDownloadMsgEndInform extends Base1078Entity {
    /**
     * 0x00：成功，
     * 0x01：失败
     */
    private byte result;
    /**
     * 对应平台文件上传消息的流水号，RESULT 为 0 时有效
     */
    private int sessionId;
    /**
     * FTP 服务器 ip 地址，RESULT 为 0 时有效
     */
    private String serverIp;
    /**
     * FTP 端口，RESULT 为 0 时有效
     */
    private int tcpPort;
    /**
     * FTP 用户名，RESULT 为 0 时有效
     */
    private String userName;
    /**
     * FTP 密码，RESULT 为 0 时有效
     */
    private String password;
    /**
     * 文件存储路径，RESULT 为 0 时有效
     */
    private String filePath;

    @Override
    public UpDownloadMsgEndInform decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.result = realData.readByte();
        this.sessionId = BufUtil.buf2UnsignedInt(realData.readBytes(2));
        this.serverIp = new String(BufUtil.bufToBytes(realData.readBytes(32))).replaceAll("\0", "");
        this.tcpPort = BufUtil.buf2UnsignedInt(realData.readBytes(2));
        this.userName = new String(BufUtil.bufToBytes(realData.readBytes(49))).replaceAll("\0", "");
        this.password = new String(BufUtil.bufToBytes(realData.readBytes(22))).replaceAll("\0", "");
        this.filePath = new String(BufUtil.bufToBytes(realData.readBytes(200))).replaceAll("\0", "");
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 2 + 32 + 2 + 49 + 22 + 200];
        data[0] = result;
        System.arraycopy(ByteUtil.short2Bytes((short) this.sessionId), 0, data, 1, 2);
        byte[] serverIpBytes = serverIp.getBytes();
        System.arraycopy(serverIpBytes, 0, data, 3, Math.min(serverIpBytes.length, 32));
        System.arraycopy(ByteUtil.short2Bytes((short) this.tcpPort), 0, data, 35, 2);
        byte[] userNameBytes = userName.getBytes();
        System.arraycopy(userNameBytes, 0, data, 37, Math.min(userNameBytes.length, 49));
        byte[] passwordBytes = password.getBytes();
        System.arraycopy(passwordBytes, 0, data, 86, Math.min(passwordBytes.length, 22));
        byte[] filePathBytes = filePath.getBytes(Charset.forName("GBK"));
        System.arraycopy(filePathBytes, 0, data, 108, Math.min(filePathBytes.length, 200));

        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_DOWNLOAD_MSG);
    }

    public static void main(String[] args) {
        UpDownloadMsgEndInform upDownloadMsgEndInform = new UpDownloadMsgEndInform();
        upDownloadMsgEndInform.setVehicleNo("粤A12340");
        upDownloadMsgEndInform.setVehicleColor((byte) 0x01);
        upDownloadMsgEndInform.setDataType(MsgConstant.SubServiceType.UP_DOWNLOAD_MSG_END_INFORM);
        upDownloadMsgEndInform.setDataLength(1 + 2 + 32 + 2 + 49 + 22 + 200);
        upDownloadMsgEndInform.setResult((byte) 0x00);
        upDownloadMsgEndInform.setSessionId(1234);
        upDownloadMsgEndInform.setServerIp("127.0.0.1");
        upDownloadMsgEndInform.setTcpPort(21);
        upDownloadMsgEndInform.setUserName("admin");
        upDownloadMsgEndInform.setPassword("123456");
        upDownloadMsgEndInform.setFilePath("/home/admin");

        byte[] encode = upDownloadMsgEndInform.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        UpDownloadMsgEndInform decode = new UpDownloadMsgEndInform().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
