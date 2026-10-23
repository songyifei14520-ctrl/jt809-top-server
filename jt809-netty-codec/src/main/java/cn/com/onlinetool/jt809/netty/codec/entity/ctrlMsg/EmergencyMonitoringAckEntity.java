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
 * 车辆应急接入监管平台应答消息
 * 子业务类型标识:UP_CTRL_MSG_EMERGENCY_MONITORING_ACK
 * 描述:下级平台应答上级平台下发的车辆应急接入监管平台请求消息应答
 */
@Setter
@Getter
@NoArgsConstructor
public class EmergencyMonitoringAckEntity implements Codec<EmergencyMonitoringAckEntity> {
	private BaseCtrlMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

    /**
     * 车辆应急接入监管结果
     * 0x00：车载终端成功收到该命令；
     * 0x01：无该车辆
     * 0x02：其他原因失败
     */
    private byte result; //车辆行驶记录数据体长度  byte[1]


	@Override
    public EmergencyMonitoringAckEntity decode(ByteBuf buf) {
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

	public static EmergencyMonitoringAckEntity create(String vehicleNo, byte vehicleColor, int sourceDataType, long sourceMsgSn, byte result) {
        EmergencyMonitoringAckEntity entity = new EmergencyMonitoringAckEntity();
        entity.result = result;
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
        entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
                MsgConstant.SubServiceType.UP_CTRL_MSG_EMERGENCY_MONITORING_ACK,
                ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn),
                    new byte[] {entity.result})
        );
        return entity;
    }

    public static EmergencyMonitoringAckEntity create(String vehicleNo, byte vehicleColor, byte result) {
        EmergencyMonitoringAckEntity entity = new EmergencyMonitoringAckEntity();
        entity.result = result;
        entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
                MsgConstant.SubServiceType.UP_CTRL_MSG_EMERGENCY_MONITORING_ACK,
                new byte[] {entity.result});
        return entity;
    }

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}



}
