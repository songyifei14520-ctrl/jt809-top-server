package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 链路类型:从链路 车辆定位信息交换补发消息 子业务类型标识:DOWN_EXG_MSG_HISTORY_ARCOSSAREA 描述:本业务在
 * DOWN_EXG_MSG_APPLY_HISGNSSDATA_ACK应答成功后，立即开始交换。如果申请失败，则不进行数据转发，本条消息下级平台无需应答
 */
@Setter
@Getter
@NoArgsConstructor
public class HistoryArcossareaEntity implements Codec<HistoryArcossareaEntity> {
	private BaseExgMsgEntity baseMsg;

	private byte gnssCnt; // 该数据包里包含的卫星定位数据个数1<=gnssCnt<=5 byte[1]
	private List<byte[]> gnssData; // 卫星定位数据 byte[36]

	@Override
	public HistoryArcossareaEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);
		this.gnssCnt = baseMsg.getData().getByte(0);
		this.gnssData = new ArrayList<>();
		for (int i = 0; i < this.gnssCnt; i++) {
			this.gnssData.add(BufUtil.bufToBytes(baseMsg.getData().slice(i * 36 + 1, 36)));
		}
		return this;
	}

	public static HistoryArcossareaEntity create(String vehicleNo, byte vehicleColor, List<byte[]> gnssDataList) {
		HistoryArcossareaEntity entity = new HistoryArcossareaEntity();
		entity.gnssCnt = (byte) gnssDataList.size();

		List<byte[]> gnssDataBytesList = new ArrayList<>();
		for (byte[] gnssData : gnssDataList) {
			byte[] gnssDataBytes = new byte[36];
			System.arraycopy(gnssData, 0, gnssDataBytes, 0, gnssData.length > 36 ? 36 : gnssData.length);
			gnssDataBytesList.add(gnssDataBytes);
		}
		entity.gnssData = gnssDataBytesList;

		byte[] result = new byte[entity.gnssCnt * 36];
		for (int i = 0; i < gnssDataBytesList.size(); i++) {
			System.arraycopy(gnssDataBytesList.get(i), 0, result, i * 36, 36);
		}
		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_EXG_MSG_HISTORY_ARCOSSAREA,
				ByteUtil.combineByteArray(new byte[] { entity.gnssCnt }, result));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
