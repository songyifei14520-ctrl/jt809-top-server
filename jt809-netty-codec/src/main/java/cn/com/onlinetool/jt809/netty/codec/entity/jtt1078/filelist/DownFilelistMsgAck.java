package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.filelist;

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
 * 主动上传音视频资源目录应答消息
 * <p>
 * 链路类型：从链路
 * 消息方向：上级平台向下级平台
 * 子业务类型标识：DOWN FILELIST MSG ACK。
 * 描述：政府视频监管平台应答企业视频监控平台发送的主动上传音视频资源目录请求消息。 数据
 * 体见表 48。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class DownFilelistMsgAck extends Base1078Entity {
    /**
     * 应答结果，定义如下：
     * 0x00：成功；
     * 0x01：失败；
     * 0x02：不支持；
     * 0x03：会话结束
     */
    private byte result;
    /**
     * 资源目录总数
     */
    private byte itemNumber;

    @Override
    public DownFilelistMsgAck decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.result = realData.readByte();
        this.itemNumber = realData.readByte();
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 1];
        data[0] = result;
        data[1] = itemNumber;
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_SEARCH_MSG);
    }

    public static void main(String[] args) {
        DownFilelistMsgAck downFilelistMsgAck = new DownFilelistMsgAck();
        downFilelistMsgAck.setVehicleNo("粤A12340");
        downFilelistMsgAck.setVehicleColor((byte) 0x01);
        downFilelistMsgAck.setDataType(MsgConstant.SubServiceType.DOWN_FILELIST_MSG_ACK);
        downFilelistMsgAck.setDataLength(1 + 1);
        downFilelistMsgAck.setResult((byte) 0x00);
        downFilelistMsgAck.setItemNumber((byte) 0x01);

        byte[] encode = downFilelistMsgAck.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownFilelistMsgAck decode = new DownFilelistMsgAck().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
