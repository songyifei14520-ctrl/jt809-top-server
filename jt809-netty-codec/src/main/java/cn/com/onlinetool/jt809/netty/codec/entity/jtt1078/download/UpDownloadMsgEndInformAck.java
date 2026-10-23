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

/**
 * 远程录像下载完成通知应答消息
 * <p>
 * 链路类型：从链路
 * 消息方向：政府视频监管平台向企业视频监控平台
 * 子业务类型标识：UP DOWNLOAD MSG END INFORM ACK。
 * 描述：政府视频监管平台应答企业视频监控平台下载完成通知。 数据体见表 58
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpDownloadMsgEndInformAck extends Base1078Entity {
    /**
     * 应答结果：
     * 0x00：成功；
     * 0x01：失败；
     * 0x02：不支持；
     * 0x03：会话结束
     */
    private byte result;
    /**
     * 对应平台文件上传消息的流水号，RESULT 为 0 时有效
     */
    private int sessionId;

    @Override
    public UpDownloadMsgEndInformAck decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.result = realData.readByte();
        this.sessionId = BufUtil.buf2UnsignedInt(realData.readBytes(2));
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 2];
        data[0] = result;
        System.arraycopy(ByteUtil.short2Bytes((short) this.sessionId), 0, data, 1, 2);
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_DOWNLOAD_MSG);
    }

    public static void main(String[] args) {
        UpDownloadMsgEndInformAck upDownloadMsgEndInformAck = new UpDownloadMsgEndInformAck();
        upDownloadMsgEndInformAck.setVehicleNo("粤A12340");
        upDownloadMsgEndInformAck.setVehicleColor((byte) 0x01);
        upDownloadMsgEndInformAck.setDataType(MsgConstant.SubServiceType.UP_DOWNLOAD_MSG_END_INFORM_ACK);
        upDownloadMsgEndInformAck.setDataLength(1 + 2);
        upDownloadMsgEndInformAck.setResult((byte) 0x00);
        upDownloadMsgEndInformAck.setSessionId(1234);

        byte[] encode = upDownloadMsgEndInformAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        UpDownloadMsgEndInformAck decode = new UpDownloadMsgEndInformAck().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
