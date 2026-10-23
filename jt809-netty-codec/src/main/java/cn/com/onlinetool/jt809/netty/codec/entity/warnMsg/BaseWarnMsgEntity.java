package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 主链路车辆报警信息业务 链路类型:主链路 消息方向:下级平台往上级平台 业务数据类型标识:UP_WARN_MSG
 * 描述:下级平台向上级平台发送车辆报警信息业务
 * <p>
 * <p>
 * 从链路车辆报警信息业务 链路类型:从链路 消息方向:上级平台往下级平台 业务数据类型标识:DOWN_WARN_MSG
 * 描述:上级平台向下级平台发送报瞥信息业务
 */
@Setter
@Getter
@NoArgsConstructor
public class BaseWarnMsgEntity implements Codec<BaseWarnMsgEntity> {

	private BaseEntity base;

    @Field(length = 21, desc = "车牌号",version = {2011})
	private String vehicleNo; // 车牌号 byte[21]
    @Field(length = 1, desc = "车辆颜色",version = {2011})
	private byte vehicleColor; // 车辆颜色 byte[1]
	private int dataType; // 子业务类型标识 byte[2]
	private long dataLength; // 后续数据长度 byte[4]

    @Field(length = 4, desc = "报警督办请求消息报文序列号",version = {2019})
    private int msgSn; // 报警督办请求消息报文序列号 byte[4]
    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 3, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

    @JsonIgnore
	private ByteBuf data; // 数据部分 byte[dataLength]

	@Override
	public BaseWarnMsgEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();

        if (base.getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            return decode2019(buf);
        }

		this.vehicleNo = new String(BufUtil.bufToBytes(msgbody.slice(0, 21)), Charset.forName("GBK"));
		this.vehicleColor = msgbody.getByte(21);
		this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(22, 2));
		this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(24, 4));
		this.data = msgbody.slice(28, msgbody.readableBytes() - 28);

		return this;
	}

	/**
	 * 解析包含车牌号与颜色的报警信息消息数据体结构 (DB32/T 3610.3—2025 表46 / JT/T 809-2011)
	 */
	public BaseWarnMsgEntity decodeWithVehicle(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
		this.vehicleNo = new String(BufUtil.bufToBytes(msgbody.slice(0, 21)), Charset.forName("GBK"));
		this.vehicleColor = msgbody.getByte(21);
		this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(22, 2));
		this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(24, 4));
		this.data = msgbody.slice(28, msgbody.readableBytes() - 28);

		return this;
	}

	public static BaseWarnMsgEntity create(int primaryDataType,String vehicleNo, byte vehicleColor, int dataType, byte[] data) {

		BaseWarnMsgEntity baseMsg = new BaseWarnMsgEntity();

		baseMsg.vehicleNo = vehicleNo;
		baseMsg.vehicleColor = vehicleColor;
		baseMsg.dataType = dataType;
		baseMsg.dataLength = data.length;

		byte[] msgbody = new byte[21 + 1 + 2 + 4 + (int) baseMsg.dataLength];
		byte[] vehicleBytes = vehicleNo.getBytes(Charset.forName("GBK"));
		System.arraycopy(vehicleBytes, 0, msgbody, 0, vehicleBytes.length > 21 ? 21 : vehicleBytes.length);
		msgbody[21] = vehicleColor;
		System.arraycopy(ByteUtil.short2Bytes((short) baseMsg.dataType), 0, msgbody, 22, 2);
		System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, 24, 4);
		System.arraycopy(data, 0, msgbody, 28, (int) baseMsg.dataLength);

		baseMsg.base = BaseEntity.create(primaryDataType, msgbody);
		return baseMsg;
	}

	// 解析表62报警督办应答消息数据体结构的方法
	public BaseWarnMsgEntity decode2019(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
		this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(0, 2));
		this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(2, 4));

		// 剩余数据存储在data中
		this.data = msgbody.slice(6, msgbody.readableBytes() - 6);

		return this;
	}

	// 创建表62报警督办应答消息数据体结构的方法
	public static BaseWarnMsgEntity create(int primaryDataType,int dataType, byte[] data) {
		BaseWarnMsgEntity baseMsg = new BaseWarnMsgEntity();
		baseMsg.dataType = dataType;
		baseMsg.dataLength = data != null ? data.length : 0;

		// 组装消息体：DATA_TYPE(2) + DATA_LENGTH(4) + DATA
		byte[] msgbody = new byte[2 + 4 + (int) baseMsg.dataLength];
		int offset = 0;

		// DATA_TYPE (2字节)
		System.arraycopy(ByteUtil.short2Bytes((short) baseMsg.dataType), 0, msgbody, offset, 2);
		offset += 2;

		// DATA_LENGTH (4字节)
		System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, offset, 4);
		offset += 4;

		// DATA (数据部分)
		if (data != null && data.length > 0) {
			System.arraycopy(data, 0, msgbody, offset, data.length);
		}

		baseMsg.base = BaseEntity.create(primaryDataType, msgbody);
		return baseMsg;
	}

	// 解析报警督办信息消息数据体结构的方法
	public BaseWarnMsgEntity decodeWarnSuperviseInfo(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
        this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(0, 2));
        this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(2, 4));

        if (this.base.getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            this.msgSn = (int) BufUtil.buf2UnsignedLong(msgbody.slice(6, 4));
            // SOURCE_DATA_TYPE、SOURCE_MSG_SN、RESULT字段存储在data中
            this.data = msgbody.slice(10, msgbody.readableBytes() - 10);

        }else {
            // 剩余数据存储在data中
            this.data = msgbody.slice(6, msgbody.readableBytes() - 6);
        }

		return this;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}

}
