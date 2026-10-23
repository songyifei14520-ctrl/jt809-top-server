package cn.com.onlinetool.jt809.netty.codec.entity.linkManager;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 从链路注销请求消息
 * <p>
 * 业务数据类型标识：DOWN_DISCONNECT_REQ
 * <p>
 * 从链路建立后，上级平台在取消该链路时，应向下级平台发送从链路注销请求消息
 */
@Setter
@Getter
@NoArgsConstructor
public class SubDisConnectReqEntity implements Codec<SubDisConnectReqEntity> {

	private BaseEntity base;

	private long verifyCode; // 校验码 byte[4]

	@Override
	public SubDisConnectReqEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
		this.verifyCode = BufUtil.buf2UnsignedLong(msgbody);

		return this;
	}

	public static SubDisConnectReqEntity create(long verifyCode) {
		SubDisConnectReqEntity baseMsg = new SubDisConnectReqEntity();

		baseMsg.verifyCode = verifyCode;

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.DOWN_DISCONNECT_REQ,
				ByteUtil.long2Bytes((baseMsg.verifyCode)));
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}
}
