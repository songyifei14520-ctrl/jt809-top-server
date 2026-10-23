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

import java.time.Instant;
import java.util.Date;

/**
 * 远程录像下载控制请求消息
 * <p>
 * 链路类型：从链路
 * 消息方向：政府视频监管平台向企业视频监控平台
 * 子业务类型标识：DOWN DOWNLOAD MSG CONTROL。
 * 描述：政府视频监管平台给企业视频监控平台发送下载控制消息。 数据体见表 59。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class DownDownloadMsgControl extends Base1078Entity {
    /**
     * 对应平台文件上传消息的流水号
     */
    private int sessionId;
    /**
     * 0x00：暂停；
     * 0x01：继续；
     * 0x02：取消
     */
    private byte type;

    @Override
    public DownDownloadMsgControl decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.sessionId = BufUtil.buf2UnsignedInt(realData.readBytes(2));
        this.type = realData.readByte();
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[2 + 1];
        System.arraycopy(ByteUtil.short2Bytes((short) this.sessionId), 0, data, 0, 2);
        data[2] = type;
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_DOWNLOAD_MSG);
    }

    public static void main(String[] args) {
        DownDownloadMsgControl downDownloadMsgControl = new DownDownloadMsgControl();
        downDownloadMsgControl.setVehicleNo("粤A12340");
        downDownloadMsgControl.setVehicleColor((byte) 0x01);
        downDownloadMsgControl.setDataType(MsgConstant.SubServiceType.DOWN_DOWNLOAD_MSG_CONTROL);
        downDownloadMsgControl.setDataLength(2 + 1);
        downDownloadMsgControl.setSessionId(1234);
        downDownloadMsgControl.setType((byte) 0x00);

        byte[] encode = downDownloadMsgControl.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownDownloadMsgControl decode = new DownDownloadMsgControl().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
