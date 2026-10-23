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
 * 子业务类型标识:DOWN_PLATFORM_MSG_RETRAN_REQ
 * 描述:下级平台向上级平台请求补传平台间消息
 */
@Setter
@Getter
@NoArgsConstructor
public class DownRetranReqEntity implements Codec<DownRetranReqEntity> {
	private BasePlatFormMsgEntity baseMsg;


	private int retranDataType;  // 对应需要重传消息的子业务类型标识 uint16_t 2
	private byte serialTotal;    // 重传消息总数 BYTE 1
	private long[] serialList;   // 需要重传消息的数据包序列号列表 BYTES 8
	private long time;           // 重传起始系统UTC时间，长度为8个字节 time_t 8

	@Override
	public DownRetranReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BasePlatFormMsgEntity().decode(buf);
		ByteBuf data = baseMsg.getData();

		this.retranDataType = BufUtil.buf2UnsignedShort(data.slice(0, 2));
		this.serialTotal = data.getByte(2);

		// 读取序列号列表 固定8字节
		this.serialList = new long[2];
		this.serialList[0] = BufUtil.buf2UnsignedLong(data.slice(3, 4));
        this.serialList[1] = BufUtil.buf2UnsignedLong(data.slice(7, 4));

		this.time = BufUtil.buf2SignedLong(data.slice(11, 8));
		return this;
	}

	public static DownRetranReqEntity create(int retranDataType, byte serialTotal, long[] serialList, long time) {
		DownRetranReqEntity entity = new DownRetranReqEntity();
		entity.retranDataType = retranDataType;
		entity.serialTotal = serialTotal;
		entity.serialList = serialList != null ? serialList : new long[2];
		entity.time = time;

		// 计算后续数据长度：retranDataType(2) + serialTotal(1) + serialList(8) + time(8) = 19字节
        long startIndex = serialList[0];
        long endIndex = serialList[1];

		// 构建数据部分
		byte[] data = ByteUtil.combineByteArray(

				ByteUtil.short2Bytes((short) entity.retranDataType),
				new byte[]{entity.serialTotal},
				ByteUtil.int2Byte((int) startIndex),
                ByteUtil.int2Byte((int) endIndex),
				ByteUtil.long2Bytes(entity.time)
		);

		entity.baseMsg = BasePlatFormMsgEntity.create(MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
				MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_RETRAN_REQ,
				data
		);
		return entity;
	}


	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
