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
 * 从链路连接请求消息
 * <p>
 * 业务数据类型标识: DOWN_CONNECT_REQ
 * <p>
 * 主链路建立连接后，上级平台向下级平台发送从链路连接清求消息，以建立从链路连接
 */
@Setter
@Getter
@NoArgsConstructor
public class SubConnectReqEntity implements Codec<SubConnectReqEntity> {

	private BaseEntity base;

	private long verifyCode; // 校验码 byte[4]

	@Override
	public SubConnectReqEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
		this.verifyCode = BufUtil.buf2UnsignedLong(msgbody);

		return this;
	}

	public static SubConnectReqEntity create(long verifyCode) {
		SubConnectReqEntity baseMsg = new SubConnectReqEntity();

		baseMsg.verifyCode = verifyCode;

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.DOWN_CONNECT_REQ,
				ByteUtil.int2Byte((int)baseMsg.verifyCode));
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}

}
