package cn.com.onlinetool.jt809.netty.codec.entity.linkManager;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;

/**
 * 从链路连接保持请求消息
 * <p>
 * 业务数据类型标识:DOWN_LINKTEST_REQ
 * <p>
 * 从链路建立成功后，上级平台向下级平台发送从链路连接保持请求消息，以保持从链路的连接状态
 */
@Setter
@Getter
public class SubLinkTestReqEntity implements Codec<SubLinkTestReqEntity> {

	private BaseEntity base;

    public SubLinkTestReqEntity() {

    }

	/**
	 * 数据体为空
	 */

	@Override
	public SubLinkTestReqEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		return this;
	}

	public static SubLinkTestReqEntity create() {
		SubLinkTestReqEntity baseMsg = new SubLinkTestReqEntity();

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.DOWN_LINKTEST_REQ, new byte[0]);
		return baseMsg;
	}

	public byte[] encode() {
		return this.base.encode();
	}
}
