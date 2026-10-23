package cn.com.onlinetool.jt809.netty.codec.entity.ctrlMsg;

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
 * 链路类型:从链路 上报车辆行驶记录请求消息 子业务类型标识:DOWN_CTRL_MSG_TAKE_TRAVEL_REQ
 * 描述:上级平台向下级平台下发上报车辆行驶记录请求消息
 */
@Setter
@Getter
@NoArgsConstructor
public class TakeTravelReqEntity implements Codec<TakeTravelReqEntity> {
	private BaseCtrlMsgEntity baseMsg;

	private long startTime; // 开始时间 byte[8]
	private long endTime; // 结束时间 byte[8]

    @Field(length = 1, desc = "命令字",version = {2019})
    private byte commandType;// 命令字

	@Override
	public TakeTravelReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseCtrlMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.startTime = BufUtil.buf2SignedLong(data.slice(0, 8));
            this.endTime = BufUtil.buf2SignedLong(data.slice(8, 8));
            this.commandType = data.getByte(16);
            return this;
        }

        //2011版本协议
		this.startTime = BufUtil.buf2SignedLong(baseMsg.getData().slice(0, 8));
        this.endTime = BufUtil.buf2SignedLong(baseMsg.getData().slice(8, 8));
		return this;
	}

	public static TakeTravelReqEntity create(String vehicleNo, byte vehicleColor, long startTime, long endTime, byte commandType) {
		TakeTravelReqEntity entity = new TakeTravelReqEntity();
		entity.startTime = startTime;
		entity.endTime = endTime;
        entity.commandType = commandType;

		entity.baseMsg = BaseCtrlMsgEntity.create(
				MsgConstant.PrimaryServiceType.DOWN_CTRL_MSG,
				vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_CTRL_MSG_TAKE_TRAVEL_REQ,
				ByteUtil.combineByteArray(ByteUtil.long2Bytes(entity.startTime), ByteUtil.long2Bytes(entity.endTime), new byte[]{entity.commandType}));
		return entity;
	}

    public static TakeTravelReqEntity create(String vehicleNo, byte vehicleColor, long startTime, long endTime) {
		TakeTravelReqEntity entity = new TakeTravelReqEntity();
		entity.startTime = startTime;
		entity.endTime = endTime;

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_CTRL_MSG_TAKE_TRAVEL_REQ,
				ByteUtil.combineByteArray(ByteUtil.long2Bytes(entity.startTime), ByteUtil.long2Bytes(entity.endTime)));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
