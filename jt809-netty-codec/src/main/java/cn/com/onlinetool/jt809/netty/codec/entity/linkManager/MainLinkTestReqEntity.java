package cn.com.onlinetool.jt809.netty.codec.entity.linkManager;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;

/**
 * 主链路连接保持请求消息
 * <p>
 * 业务数据类型标识:UP_LINKTEST_REQ
 * <p>
 * 下级平台向上级平台发送主链路连接保持清求消息，以保持主链路的连接
 */
@Setter
@Getter
public class MainLinkTestReqEntity implements Codec<MainLinkTestReqEntity> {

	private BaseEntity base;

	/**
	 * 数据体为空
	 */

	@Override
	public MainLinkTestReqEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		return this;
	}

	public static MainLinkTestReqEntity create() {
		MainLinkTestReqEntity baseMsg = new MainLinkTestReqEntity();

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_LINKTEST_REQ, new byte[0]);
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}
}
