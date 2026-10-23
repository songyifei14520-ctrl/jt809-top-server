package cn.com.onlinetool.jt809.netty.codec.entity.platformMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 版本协议 2019
 * 链路类型:主链路 上传平台间消息补传请求消息
 * 子业务类型标识:UP_PLATFORM_MSG_RETRAN_REQ
 * 描述:下级平台向上级平台请求补传平台间消息
 */
@Setter
@Getter
@NoArgsConstructor
public class UpRetranReqEntity implements Codec<UpRetranReqEntity> {
	private BasePlatFormMsgEntity baseMsg;


	private int retranDataType;  // 对应需要重传消息的子业务类型标识 uint16_t 2
	private byte serialTotal;    // 重传消息总数 BYTE 1
	private byte[] serialList;   // 需要重传消息的数据包序列号列表 BYTES 8
	private long time;           // 重传起始系统UTC时间，长度为8个字节 time_t 8

	@Override
	public UpRetranReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BasePlatFormMsgEntity().decode(buf);
		ByteBuf data = baseMsg.getData();

		this.retranDataType = BufUtil.buf2UnsignedShort(data.slice(0, 2));
		this.serialTotal = data.getByte(2);

		// 读取序列号列表
		this.serialList = new byte[8]; // 固定8字节
		data.slice(3, 8).readBytes(this.serialList);

		this.time = BufUtil.buf2SignedLong(data.slice(11, 8));
		return this;
	}

	public static UpRetranReqEntity create(int retranDataType, byte serialTotal, byte[] serialList, long time) {
		UpRetranReqEntity entity = new UpRetranReqEntity();
		entity.retranDataType = retranDataType;
		entity.serialTotal = serialTotal;
		entity.serialList = serialList != null ? serialList : new byte[8];
		entity.time = time;

		// 计算后续数据长度：retranDataType(2) + serialTotal(1) + serialList(8) + time(8) = 19字节

		// 构建数据部分
		byte[] data = ByteUtil.combineByteArray(

				ByteUtil.short2Bytes((short) entity.retranDataType),
				new byte[]{entity.serialTotal},
				entity.serialList,
				ByteUtil.long2Bytes(entity.time)
		);

		entity.baseMsg = BasePlatFormMsgEntity.create(MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
				MsgConstant.SubServiceType.UP_PLATFORM_MSG_RETRAN_REQ,
				data
		);
		return entity;
	}


	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
