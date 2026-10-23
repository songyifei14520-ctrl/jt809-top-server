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

/**
 * 链路类型:从链路 补发车辆定位信息应答消息 子业务类型标识:DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK
 * 描述:本条消息是上级平台应答下级平台发送的补发车辆定位信息请求消息,即UP_EXG_MSG_APPLY_HISGNSSDATA_REQ
 */
@Setter
@Getter
@NoArgsConstructor
public class ApplyHisgnssDataAckEntity implements Codec<ApplyHisgnssDataAckEntity> {
	private BaseExgMsgEntity baseMsg;

    @Field(length = 2, desc = "源数据类型",version = {2019})
    private int sourceDateType;// 源数据类型 byte[2]
    @Field(length = 4, desc = "源消息流水号",version = {2019})
    private long sourceMsgSn;// 源消息流水号 byte[4]
	private byte result; // 补发车辆定位信息交换的原因 byte[1]

	@Override
	public ApplyHisgnssDataAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);

		//2019版本协议
		if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
			ByteBuf data = baseMsg.getData();
			if (data.readableBytes() >= 7) { // 2+4+1 = 7字节
				this.sourceDateType = BufUtil.buf2UnsignedInt(data.slice(0, 2));
				this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2, 4));
				this.result = data.getByte(6);
			}
			return this;
		}

		//2011版本协议
		this.result = baseMsg.getData().getByte(0);
		return this;
	}

	public static ApplyHisgnssDataAckEntity create(String vehicleNo, byte vehicleColor, byte result) {
		ApplyHisgnssDataAckEntity entity = new ApplyHisgnssDataAckEntity();
		entity.result = result;

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK, new byte[] { entity.result });
		return entity;
	}

	// 2019版本协议的create方法
	public static ApplyHisgnssDataAckEntity create(String vehicleNo, byte vehicleColor, int sourceDateType, long sourceMsgSn, byte result) {
		ApplyHisgnssDataAckEntity entity = new ApplyHisgnssDataAckEntity();
		entity.sourceDateType = sourceDateType;
		entity.sourceMsgSn = sourceMsgSn;
		entity.result = result;

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK,
				ByteUtil.combineByteArray(
						ByteUtil.short2Bytes((short)sourceDateType),
						ByteUtil.int2Byte((int)sourceMsgSn),
						new byte[] { result }));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
