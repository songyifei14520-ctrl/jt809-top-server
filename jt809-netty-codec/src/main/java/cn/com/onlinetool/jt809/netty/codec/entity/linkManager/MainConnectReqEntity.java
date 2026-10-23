package cn.com.onlinetool.jt809.netty.codec.entity.linkManager;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 主链路登录请求消息
 * <p>
 * 业务数据类型标识: UP_CONNECT_REQ
 * <p>
 * 下级平台向上级平台发送用户名和密码等登录信息。
 */
@Setter
@Getter
@NoArgsConstructor
public class MainConnectReqEntity implements Codec<MainConnectReqEntity> {

	private BaseEntity base;

	private long userId; // 用户名 byte[4]
	private String password; // 密码 byte[8]

    @Field(length = 4, desc = "平台接人码",version = {2019})
    private long msgGnssscenterId; // 下级平台接入码，上级平台给下级平台分配唯一标识码 byte[4]
	private String downLinkIp; // 下级平台提供对应的从链路服务端 IP 地址 byte[32]
	private int downLinkPort; // 下级平台提供对应的从链路服务器端口号 byte[2]

	@Override
	public MainConnectReqEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();

		//2019版本协议
		if (this.base.getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            this.userId = BufUtil.buf2UnsignedLong(msgbody.slice(0, 4));
            this.password = new String(BufUtil.bufToBytes(msgbody.slice(4, 8)), Charset.forName("GBK"));
            this.msgGnssscenterId = BufUtil.buf2UnsignedLong(msgbody.slice(12, 4));
            this.downLinkIp = new String(BufUtil.bufToBytes(msgbody.slice(16, 32)), Charset.forName("GBK"));
            this.downLinkPort = BufUtil.buf2UnsignedInt(msgbody.slice(48, 2));
			return this;
		}

		//2011版本协议
		this.userId = BufUtil.buf2UnsignedLong(msgbody.slice(0, 4));
		this.password = new String(BufUtil.bufToBytes(msgbody.slice(4, 8)),Charset.forName("GBK"));
		this.downLinkIp = new String(BufUtil.bufToBytes(msgbody.slice(12, 32)),Charset.forName("GBK"));
		this.downLinkPort = BufUtil.buf2UnsignedInt(msgbody.slice(44, 2));

		return this;
	}

	/**
	 * 创建2019版本协议的主链路登录请求消息
	 * @param userId 用户名
	 * @param password 密码
	 * @param msgGnssscenterId 下级平台接入码
	 * @param downLinkIp 下级平台提供对应的从链路服务端IP地址
	 * @param downLinkPort 下级平台提供对应的从链路服务器端口号
	 * @return MainConnectReqEntity实例
	 */
	public static MainConnectReqEntity create(long userId, String password, long msgGnssscenterId, String downLinkIp, int downLinkPort) {
		MainConnectReqEntity baseMsg = new MainConnectReqEntity();

		baseMsg.userId = userId;
		baseMsg.password = password;
		baseMsg.msgGnssscenterId = msgGnssscenterId;
		baseMsg.downLinkIp = downLinkIp;
		baseMsg.downLinkPort = downLinkPort;

		byte[] passwordBytes = new byte[8];
		byte[] passwordTmp = password.getBytes(Charset.forName("GBK"));
		int copyLength = Math.min(passwordTmp.length, 8);
		System.arraycopy(passwordTmp, 0, passwordBytes, 0, copyLength);

		byte[] downLinkIpBytes = new byte[32];
		byte[] downLinkIpTmp = downLinkIp.getBytes(Charset.forName("GBK"));
		int copyLengths = Math.min(downLinkIpTmp.length, 32);
		System.arraycopy(downLinkIpTmp, 0, downLinkIpBytes, 0, copyLengths);

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_CONNECT_REQ,
				ByteUtil.combineByteArray(ByteUtil.int2Byte( (int)baseMsg.userId), passwordBytes,
                    ByteUtil.int2Byte( (int)msgGnssscenterId),
                    downLinkIpBytes,
                    ByteUtil.short2Bytes((short) baseMsg.downLinkPort)));
		return baseMsg;
	}

	/**
	 * 创建2011版本协议的主链路登录请求消息
	 * @param userId 用户名
	 * @param password 密码
	 * @param downLinkIp 下级平台提供对应的从链路服务端IP地址
	 * @param downLinkPort 下级平台提供对应的从链路服务器端口号
	 * @return MainConnectReqEntity实例
	 */
	public static MainConnectReqEntity create(long userId, String password, String downLinkIp, int downLinkPort) {
		MainConnectReqEntity baseMsg = new MainConnectReqEntity();

		baseMsg.userId = userId;
		baseMsg.password = password;
		baseMsg.downLinkIp = downLinkIp;
		baseMsg.downLinkPort = downLinkPort;

		byte[] passwordBytes = new byte[8];
		byte[] passwordTmp = password.getBytes(Charset.forName("GBK"));
		int copyLength = Math.min(passwordTmp.length, 8);
		System.arraycopy(passwordTmp, 0, passwordBytes, 0, copyLength);

		byte[] downLinkIpBytes = new byte[32];
		byte[] downLinkIpTmp = downLinkIp.getBytes(Charset.forName("GBK"));
		int copyLengths = Math.min(downLinkIpTmp.length, 32);
		System.arraycopy(downLinkIpTmp, 0, downLinkIpBytes, 0, copyLengths);

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_CONNECT_REQ,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) baseMsg.userId), passwordBytes, downLinkIpBytes,
						ByteUtil.short2Bytes((short) baseMsg.downLinkPort)));
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}

}
