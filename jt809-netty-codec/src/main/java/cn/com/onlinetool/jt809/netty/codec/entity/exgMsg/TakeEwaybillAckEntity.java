package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 上报车辆电子运单应答消息 子业务类型标识:UP_EXG_MSG_TAKE_EWAYBILL_ACK
 * 描述:下级平台应答上级平台发送的上报车辆电子运单请求消息，向上级平台上传车辆当前电子运单
 */
@Setter
@Getter
@NoArgsConstructor
public class TakeEwaybillAckEntity implements Codec<TakeEwaybillAckEntity> {
	private BaseExgMsgEntity baseMsg;

    @Field(length = 2, desc = "数据来源类型",version = {2019})
    private int sourceDateType;// 数据来源类型
    @Field(length = 3, desc = "数据来源消息流水号",version = {2019})
    private long sourceMsgSn;// 数据来源消息流水号
	private long ewaybillLength; // 电子运单数据体长度 byte[4]
	private String ewaybillInfo; // 电子运单数据内容 byte[ewaybillLength]

	@Override
	public TakeEwaybillAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);

		//2019版本协议
		if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
			ByteBuf data = baseMsg.getData();
			if (data.readableBytes() >= 10) {
				this.sourceDateType = BufUtil.buf2UnsignedInt(data.slice(0, 2));
				this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2, 4));
				this.ewaybillLength = BufUtil.buf2UnsignedLong(data.slice(6, 4));
				if (data.readableBytes() > 10) {
					this.ewaybillInfo = new String(
							BufUtil.bufToBytes(data.slice(10, data.readableBytes() - 10)),
							Charset.forName("GBK"));
				}
			}
			return this;
		}

		//2011版本协议
		ByteBuf data = baseMsg.getData();
		if (data.readableBytes() >= 4) {
			this.ewaybillLength = BufUtil.buf2UnsignedLong(data.slice(0, 4));
			if (data.readableBytes() > 4) {
				this.ewaybillInfo = new String(
						BufUtil.bufToBytes(data.slice(4, data.readableBytes() - 4)),
						Charset.forName("GBK"));
			}
		}
		return this;
	}

	public static TakeEwaybillAckEntity create(String vehicleNo, byte vehicleColor, String ewaybillInfo) {
		TakeEwaybillAckEntity entity = new TakeEwaybillAckEntity();
		entity.ewaybillInfo = ewaybillInfo;

		byte[] ewaybillInfoBytes = ewaybillInfo.getBytes(Charset.forName("GBK"));

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_TAKE_EWAYBILL_ACK,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) ewaybillInfoBytes.length), ewaybillInfoBytes));
		return entity;
	}

	// 2019版本协议的create方法
	public static TakeEwaybillAckEntity create(String vehicleNo, byte vehicleColor, int sourceDateType, long sourceMsgSn, String ewaybillInfo) {
		TakeEwaybillAckEntity entity = new TakeEwaybillAckEntity();
		entity.sourceDateType = sourceDateType;
		entity.sourceMsgSn = sourceMsgSn;
		entity.ewaybillInfo = ewaybillInfo;

		byte[] ewaybillInfoBytes = ewaybillInfo.getBytes(Charset.forName("GBK"));
		entity.ewaybillLength = ewaybillInfoBytes.length;

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_TAKE_EWAYBILL_ACK,
				ByteUtil.combineByteArray(
						ByteUtil.short2Bytes((short)sourceDateType),
						ByteUtil.int2Byte((int)sourceMsgSn),
						ByteUtil.int2Byte((int)entity.ewaybillLength),
						ewaybillInfoBytes));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
