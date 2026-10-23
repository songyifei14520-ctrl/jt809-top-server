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
 * 链路类型:主链路 实时上传车辆定位信息 子业务类型标识:UP_EXG_MSG_REAL_LOCATION
 */
@Setter
@Getter
@NoArgsConstructor
public class RealLocationEntity implements Codec<RealLocationEntity> {
	private BaseExgMsgEntity baseMsg;

	private byte[] gnssData; // 车辆定位信息 byte[36]

	@Override
	public RealLocationEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);
		this.gnssData = BufUtil.bufToBytes(baseMsg.getData());
		return this;
	}

	public static RealLocationEntity create(String vehicleNo, byte vehicleColor, byte[] gnssData) {
		RealLocationEntity entity = new RealLocationEntity();
		entity.gnssData = gnssData;
        byte[] gnssDataBytes;
        if(JT809Config.isJT809_2019()){
            gnssDataBytes = new byte[gnssData.length];
            System.arraycopy(gnssData, 0, gnssDataBytes, 0,  gnssData.length);
        }else {
            gnssDataBytes = new byte[36];
            System.arraycopy(gnssData, 0, gnssDataBytes, 0, gnssData.length > 36 ? 36 : gnssData.length);
        }

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_REAL_LOCATION, gnssDataBytes);
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
