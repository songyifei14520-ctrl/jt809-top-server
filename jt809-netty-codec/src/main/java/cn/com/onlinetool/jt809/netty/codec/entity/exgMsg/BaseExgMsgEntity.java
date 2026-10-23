package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 主链路车辆动态信息交换业务 链路类型:主链路 业务类型标识:UP_EXG_MSG 描述:下级平台向上级平台发送车辆动态信息交换业务数据包
 * <p>
 * 链路类型:从链路 消息方向:上级平台往下级平台 业务数据类型标识:DOWN_EXG_MSG
 * 描述:上级平台作为客户端向下级平台服务端发送车辆动态信息交换业务
 */
@Setter
@Getter
@NoArgsConstructor
public class BaseExgMsgEntity implements Codec<BaseExgMsgEntity> {

	private BaseEntity base;

	private String vehicleNo; // 车牌号 byte[21]
	private byte vehicleColor; // 车辆颜色 byte[1]
	private int dataType; // 子业务类型标识 byte[2]
	private long dataLength; // 后续数据长度 byte[4]

    private int sourceDataType;
    private long sourceMsgSn;

    @JsonIgnore
	private ByteBuf data; // 数据部分 byte[dataLength]

	@Override
	public BaseExgMsgEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();

		this.vehicleNo = new String(BufUtil.bufToBytes(msgbody.slice(0, 21)), Charset.forName("GBK"));
		this.vehicleColor = msgbody.getByte(21);
		this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(22, 2));
		this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(24, 4));
		this.data = msgbody.slice(28, msgbody.readableBytes() - 28);

		return this;
	}

    public BaseExgMsgEntity withSorceSnDecode(ByteBuf buf) {
        this.base = new BaseEntity().decode(buf);

        ByteBuf msgbody = this.base.getMessageBody();

        this.vehicleNo = new String(BufUtil.bufToBytes(msgbody.slice(0, 21)), Charset.forName("GBK"));
        this.vehicleColor = msgbody.getByte(21);
        this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(22, 2));
        this.sourceDataType = BufUtil.buf2UnsignedInt(msgbody.slice(24, 2));
        this.sourceMsgSn = BufUtil.buf2UnsignedLong(msgbody.slice(26, 4));
        this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(24, 4));

        return this;
    }

    public BaseExgMsgEntity noPlateNoDecode(ByteBuf buf) {
        this.base = new BaseEntity().decode(buf);

        ByteBuf msgbody = this.base.getMessageBody();

        this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(0, 2));
        this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(2, 4));
        this.data = msgbody.slice(6, msgbody.readableBytes() - 6);

        return this;
    }

	public static BaseExgMsgEntity create(int primaryDataType,String vehicleNo, byte vehicleColor, int dataType, byte[] data) {
		BaseExgMsgEntity baseMsg = new BaseExgMsgEntity();
		baseMsg.vehicleNo = vehicleNo;
		baseMsg.vehicleColor = vehicleColor;
		baseMsg.dataType = dataType;
		baseMsg.dataLength = data.length;

		byte[] msgbody = new byte[21 + 1 + 2 + 4 + (int) baseMsg.dataLength];
		byte[] vehicleBytes = vehicleNo.getBytes(Charset.forName("GBK"));
		System.arraycopy(vehicleBytes, 0, msgbody, 0, vehicleBytes.length > 21 ? 21 : vehicleBytes.length);
		msgbody[21] = vehicleColor;
		System.arraycopy(ByteUtil.short2Bytes((short) dataType), 0, msgbody, 22, 2);
		System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, 24, 4);
		System.arraycopy(data, 0, msgbody, 28, (int) baseMsg.dataLength);

		baseMsg.base = BaseEntity.create(primaryDataType, msgbody);
		return baseMsg;
	}

    public static BaseExgMsgEntity create(int primaryDataType,String vehicleNo, byte vehicleColor, int dataType, int sourceDataType, long sourceMsgSn) {
        BaseExgMsgEntity baseMsg = new BaseExgMsgEntity();
        baseMsg.vehicleNo = vehicleNo;
        baseMsg.vehicleColor = vehicleColor;
        baseMsg.dataType = dataType;
        baseMsg.sourceDataType = sourceDataType;
        baseMsg.sourceMsgSn = sourceMsgSn;
        baseMsg.dataLength = 0;

        byte[] msgbody = new byte[21 + 1 + 2 + 2 + 4  + 4];
        byte[] vehicleBytes = vehicleNo.getBytes(Charset.forName("GBK"));
        System.arraycopy(vehicleBytes, 0, msgbody, 0, vehicleBytes.length > 21 ? 21 : vehicleBytes.length);
        msgbody[21] = vehicleColor;
        System.arraycopy(ByteUtil.short2Bytes((short) dataType), 0, msgbody, 22, 2);
        System.arraycopy(ByteUtil.short2Bytes((short) sourceDataType), 0, msgbody, 24, 2);
        System.arraycopy(ByteUtil.int2Byte((int) sourceMsgSn), 0, msgbody, 26, 4);
        System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, 30, 4);

        baseMsg.base = BaseEntity.create(primaryDataType, msgbody);
        return baseMsg;
    }

    public static BaseExgMsgEntity create(int primaryDataType,int dataType, byte[] data) {
        BaseExgMsgEntity baseMsg = new BaseExgMsgEntity();
        baseMsg.dataType = dataType;
        baseMsg.dataLength = data.length;

        byte[] msgbody = new byte[ 2 + 4 + (int) baseMsg.dataLength];

        System.arraycopy(ByteUtil.short2Bytes((short) dataType), 0, msgbody, 0, 2);
        System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, 2, 4);
        System.arraycopy(data, 0, msgbody, 6, (int) baseMsg.dataLength);

        baseMsg.base = BaseEntity.create(primaryDataType, msgbody);
        return baseMsg;
    }

	@Override
	public byte[] encode() {
		return base.encode();
	}
}
