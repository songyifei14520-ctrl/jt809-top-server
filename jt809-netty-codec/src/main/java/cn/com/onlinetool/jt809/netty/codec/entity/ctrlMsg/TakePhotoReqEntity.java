package cn.com.onlinetool.jt809.netty.codec.entity.ctrlMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;

/**
 * 链路类型:从链路 车辆拍照请求消息 子业务类型标识:DOWN_CTRL_MSG_TAKE_PHOTO_REQ
 * 描述:上级平台向下级平台下发对某指定车辆的拍照请求消息
 */
@Setter
@Getter
@NoArgsConstructor
public class TakePhotoReqEntity implements Codec<TakePhotoReqEntity> {
	private BaseCtrlMsgEntity baseMsg;

	private byte lensId; // 镜头ID byte[1]
	private byte size; // 照片大小 byte[1]

	@Override
	public TakePhotoReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseCtrlMsgEntity().decode(buf);
		this.lensId = baseMsg.getData().getByte(0);
		this.size = baseMsg.getData().getByte(1);
		return this;
	}

	public static TakePhotoReqEntity create(String vehicleNo, byte vehicleColor, byte lensId, byte size) {
		TakePhotoReqEntity entity = new TakePhotoReqEntity();
		entity.lensId = lensId;
		entity.size = size;

		entity.baseMsg = BaseCtrlMsgEntity.create(
				MsgConstant.PrimaryServiceType.DOWN_CTRL_MSG,
				vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_CTRL_MSG_TAKE_PHOTO_REQ, new byte[] { entity.lensId, entity.size });
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
