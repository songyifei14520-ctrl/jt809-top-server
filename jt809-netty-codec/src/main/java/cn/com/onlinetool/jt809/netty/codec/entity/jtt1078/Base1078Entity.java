package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

import java.nio.charset.Charset;

/**
 * TODO 概要描述
 * <p>
 * TODO 详细描述
 *
 * @author syz
 * @since 2025/10/6
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Base1078Entity extends BaseEntity {

    protected String vehicleNo; // 车牌号 byte[21]
    protected byte vehicleColor; // 车辆颜色 byte[1]
    protected int dataType; // 子业务类型标识 byte[2]
    protected long dataLength; // 后续数据长度 byte[4]

    @JsonIgnore
    @ToString.Exclude
    protected ByteBuf data; // 后续数据部分 byte[dataLength]

    @Override
    public Base1078Entity decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf msgBody = this.getMessageBody();
        this.vehicleNo = new String(BufUtil.bufToBytes(msgBody.slice(0, 21)), Charset.forName("GBK")).replaceAll("\0", "");
        this.vehicleColor = msgBody.getByte(21);
        this.dataType = BufUtil.buf2UnsignedInt(msgBody.slice(22, 2));
        this.dataLength = BufUtil.buf2UnsignedLong(msgBody.slice(24, 4));
        this.data = msgBody.slice(28, msgBody.readableBytes() - 28);
        return this;
    }

    public byte[] encode(int msgId) {
        byte[] msgBody = new byte[21 + 1 + 2 + 4 + (int) this.dataLength];
        byte[] vehicleBytes = vehicleNo.getBytes(Charset.forName("GBK"));
        System.arraycopy(vehicleBytes, 0, msgBody, 0, Math.min(vehicleBytes.length, 21));
        msgBody[21] = vehicleColor;
        System.arraycopy(ByteUtil.short2Bytes((short) this.dataType), 0, msgBody, 22, 2);
        System.arraycopy(ByteUtil.int2Byte((int) this.dataLength), 0, msgBody, 24, 4);
        System.arraycopy(BufUtil.bufToBytes(data), 0, msgBody, 28, (int) this.dataLength);

        BaseEntity baseEntity = BaseEntity.create(msgId, msgBody);
        baseEntity.setMessageBody(BufUtil.bytesToBuf(msgBody));
        return baseEntity.encode();
    }

}
