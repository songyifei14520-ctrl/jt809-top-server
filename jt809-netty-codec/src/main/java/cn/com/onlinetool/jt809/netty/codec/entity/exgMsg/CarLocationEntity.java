package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;

/**
 * 链路类型:从链路 交换车辆定位信息消息 子业务类型标识:DOWN_EXG_MSG_CAR_LOCATION
 * 描述:上级平台通过该消息不间断地向车辆驶入区域所属的下级平台发送车辆定位信息,直到该车驶离该区域
 */
@Setter
@Getter
@NoArgsConstructor
public class CarLocationEntity implements Codec<CarLocationEntity> {
	private BaseExgMsgEntity baseMsg;

	private byte[] gnssData; // 车辆定位信息 byte[36]

	@Override
	public CarLocationEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);
		this.gnssData = BufUtil.bufToBytes(baseMsg.getData());
		return this;
	}

	public static CarLocationEntity create(String vehicleNo, byte vehicleColor, byte[] gnssData) {
		CarLocationEntity entity = new CarLocationEntity();
		entity.gnssData = gnssData;

        byte[] gnssDataBytes;
        if(JT809Config.isJT809_2019()){
            gnssDataBytes = gnssData;
        }else {
            gnssDataBytes = new byte[36];
            System.arraycopy(gnssData, 0, gnssDataBytes, 0, gnssData.length > 36 ? 36 : gnssData.length);
        }

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_EXG_MSG_CAR_LOCATION, gnssDataBytes);
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
