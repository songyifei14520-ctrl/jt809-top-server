package cn.com.onlinetool.jt809.netty.codec.entity.baseMsg;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 补报车辆静态信息应答消息 子业务类型标识:UP_BASE_MSG_VEHICLE_ADDED_ACK
 * 描述:上级平台应答下级平台发送的补报车辆静态信息清求消息
 */
@Data
@NoArgsConstructor
public class VehicleAddedAckEntity implements Codec<VehicleAddedAckEntity> {
	private BaseMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

	private String carInfo; // byte[dataLength]

	@Override
	public VehicleAddedAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0,2));
            this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2,4));
            this.carInfo = new String(BufUtil.bufToBytes(data.slice(6,data.readableBytes()-6)), Charset.forName("GBK"));
            return this;
        }

		this.carInfo = new String(BufUtil.bufToBytes(baseMsg.getData()), Charset.forName("GBK"));
		return this;
	}

	public static VehicleAddedAckEntity create(String vehicleNo, byte vehicleColor,int sourceDataType,long sourceMsgSn, String carInfo) {
		VehicleAddedAckEntity entity = new VehicleAddedAckEntity();
		entity.carInfo = carInfo;
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
		entity.baseMsg = BaseMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_BASE_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_BASE_MSG_VEHICLE_ADDED_ACK,
                ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType),ByteUtil.int2Byte((int)sourceMsgSn),
                    entity.carInfo.getBytes(Charset.forName("GBK")))
        );
		return entity;
	}

    public static VehicleAddedAckEntity create(String vehicleNo, byte vehicleColor, String carInfo) {
        VehicleAddedAckEntity entity = new VehicleAddedAckEntity();
        entity.carInfo = carInfo;
        entity.baseMsg = BaseMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_BASE_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.UP_BASE_MSG_VEHICLE_ADDED_ACK,
            entity.carInfo.getBytes(Charset.forName("GBK"))
        );
        return entity;
    }

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
