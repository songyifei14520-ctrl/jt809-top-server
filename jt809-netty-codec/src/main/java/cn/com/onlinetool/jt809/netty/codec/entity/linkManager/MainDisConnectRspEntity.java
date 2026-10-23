package cn.com.onlinetool.jt809.netty.codec.entity.linkManager;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;

/**
 * 主链路注销请求消息
 * <p>
 * 业务数据类型标识:UP_DISCONNECT_RSP
 * <p>
 * 上级平台收到下级平台发送的主链路注销请求消息后，向下级平台返回主链路注销应答消息，并记录链路注销日志，下级平台接收到应答消息后，可中断主从链路联接
 */
@Setter
@Getter
public class MainDisConnectRspEntity implements Codec<MainDisConnectRspEntity> {

	private BaseEntity base;

	/**
	 * 数据体为空
	 */

	@Override
	public MainDisConnectRspEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		return this;
	}

	public static MainDisConnectRspEntity create(long userId, String password) {
		MainDisConnectRspEntity baseMsg = new MainDisConnectRspEntity();

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_DISCONNECT_RSP, new byte[0]);
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}

}
