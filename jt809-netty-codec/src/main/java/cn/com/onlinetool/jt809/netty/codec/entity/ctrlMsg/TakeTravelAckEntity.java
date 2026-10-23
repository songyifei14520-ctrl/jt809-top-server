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

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 上报车辆行驶记录应答消息 子业务类型标识:UP_CTRL_MSG_TAKE_TRAVEL_ACK
 * 描述:下级平台应答上级平台下发的上报车辆行驶记录请求消息，将车辆行驶记录数据上传至上级平台
 */
@Setter
@Getter
@NoArgsConstructor
public class TakeTravelAckEntity implements Codec<TakeTravelAckEntity> {
	private BaseCtrlMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]
    @Field(length = 1, desc = "命令字",version = {2019})
    private byte commandType;// 命令字

	private long travelDataLength; // 车辆行驶记录数据体长度 byte[4]

	private byte[] travelDataInfo; // 车辆行驶记录信息 byte[travelDataLength]

	@Override
	public TakeTravelAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseCtrlMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0,2));
            this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2,4));
            this.commandType = data.getByte(6);
            this.travelDataLength = BufUtil.buf2UnsignedLong(data.slice(7, 4));
            this.travelDataInfo = BufUtil.bufToBytes(data.slice(11, data.readableBytes() - 11));
            return this;
        }

		this.travelDataLength = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(0, 4));
		this.travelDataInfo = BufUtil.bufToBytes(baseMsg.getData().slice(4, baseMsg.getData().readableBytes() - 4));
		return this;
	}

	public static TakeTravelAckEntity create(String vehicleNo, byte vehicleColor, int sourceDataType, long sourceMsgSn, byte commandType, byte[] travelDataInfo) {
		TakeTravelAckEntity entity = new TakeTravelAckEntity();
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
        entity.commandType = commandType;
		entity.travelDataLength = travelDataInfo.length;
		entity.travelDataInfo = travelDataInfo;

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_TRAVEL_ACK,
				ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn),
                    new byte[]{entity.commandType}, ByteUtil.int2Byte((int) entity.travelDataLength), travelDataInfo));
		return entity;
	}

    public static TakeTravelAckEntity create(String vehicleNo, byte vehicleColor, byte[] travelDataInfo) {
		TakeTravelAckEntity entity = new TakeTravelAckEntity();

		entity.travelDataLength = travelDataInfo.length;
		entity.travelDataInfo = travelDataInfo;

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_TRAVEL_ACK,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.travelDataLength), travelDataInfo));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
