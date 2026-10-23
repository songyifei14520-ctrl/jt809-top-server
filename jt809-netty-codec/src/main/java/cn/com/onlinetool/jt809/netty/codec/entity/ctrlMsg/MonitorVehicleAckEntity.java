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
 * 链路类型:主链路
 * 车辆单向监听应答消息
 * 子业务类型标识:UP_CTRL_MSG_MONITOR_VEHICLE_ACK
 * 描述:下级平台向上级平台上传车辆单向监听请求消息的应答
 */
@Setter
@Getter
@NoArgsConstructor
public class MonitorVehicleAckEntity implements Codec<MonitorVehicleAckEntity> {
	private BaseCtrlMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

    /**
     * 车辆单向监听应答结果，定义如下：
     * 0x00：监听成功
     * 0x01：监听失败
     */
    private byte result; //  byte[1]


    @Override
	public MonitorVehicleAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseCtrlMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0,2));
            this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2,4));
            this.result = data.getByte(6);
            return this;
        }

		this.result = baseMsg.getData().readByte();
		return this;
	}

	public static MonitorVehicleAckEntity create(String vehicleNo, byte vehicleColor, int sourceDataType, long sourceMsgSn, byte result) {
		MonitorVehicleAckEntity entity = new MonitorVehicleAckEntity();
		entity.result = result;
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_MONITOR_VEHICLE_ACK,
				ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn),
                    new byte[] {entity.result}));
		return entity;
	}

    public static MonitorVehicleAckEntity create(String vehicleNo, byte vehicleColor, byte result) {
		MonitorVehicleAckEntity entity = new MonitorVehicleAckEntity();
		entity.result = result;
		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_MONITOR_VEHICLE_ACK,
				new byte[] {entity.result});
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}


}
