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

import java.nio.charset.Charset;

/**
 * 主链路注销请求消息
 * <p>
 * 业务数据类型标识：UP_DISCONNECT_REQ
 * <p>
 * 下级平台在中断与上级平台的主链路连接时，应向上级平台发送主链路注销请求消息
 */
@Setter
@Getter
@NoArgsConstructor
public class MainDisConnectReqEntity implements Codec<MainDisConnectReqEntity> {

	private BaseEntity base;
	private long userId; // 用户名 byte[4]
	private String password; // 密码 byte[8]

	@Override
	public MainDisConnectReqEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
		this.userId = BufUtil.buf2UnsignedLong(msgbody.slice(0, 4));
		this.password = new String(BufUtil.bufToBytes(msgbody.slice(4, 8)), Charset.forName("GBK"));

		return this;
	}

	public static MainDisConnectReqEntity create(long userId, String password) {
		MainDisConnectReqEntity baseMsg = new MainDisConnectReqEntity();

		baseMsg.userId = userId;
		baseMsg.password = password;

		byte[] passwordBytes = new byte[8];
		byte[] passwordTmp = password.getBytes(Charset.forName("GBK"));
		int copyLength = Math.min(passwordTmp.length, 8); // 确保复制的长度不超过目标数组的长度
		System.arraycopy(passwordTmp, 0, passwordBytes, 0, copyLength); // 从目标数组的第 0 个位置开始复制

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_DISCONNECT_REQ,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) baseMsg.userId), passwordBytes));
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}

}
