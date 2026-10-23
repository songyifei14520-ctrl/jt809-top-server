package cn.com.onlinetool.jt809.netty.codec.entity;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.enums.EncryptFlagEnum;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.JT809Util;

import java.time.Instant;

@Data
@NoArgsConstructor
public abstract class Header implements Codec<Header> {


	private long msgLength; // 数据长度(包括头标识、数据头、数据体、校验码和尾标识) byte[4]

	private long msgSn; // 报文序列号 byte[4]

	private int msgId; // 业务数据类型 byte[2]

	private long msgGnssscenterId; // 下级平台接入码，上级平台给下级平台分配唯一标识码 byte[4]

	private byte[] versionFlag = new byte[3]; // 协议版本号标识，长度为 3 个字节来表示，0x01 0x02 0x0F标识的版本号是v1.2.15 byte[3]

	private byte encryptFlag = EncryptFlagEnum.NO.getValue(); // 报文加密标识位 0 表示报文不加密，1 表示报文加密 byte[1]

	private long encryptKey = 0L; // 数据加密的密匙，长度为 4 个字节。 byte[4]

	private long time = 0L; // 发送消息时的系统UTC时间，长度为 8 个字节。JT/T809-2019版本字段



	/**
	 * 获取协议版本对应的消息头长度
	 * @return 消息头长度
	 */
	public abstract int getHeaderLength();

	/**
	 * 设置时间戳
	 * @param time UTC时间戳（秒）
	 */
	public void setTime(long time) {
		if (isValidTimestamp(time)) {
			this.time = time;
		} else {
			this.time = getCurrentUtcTimestamp();
		}
	}

	/**
	 * 获取时间戳
	 * @return UTC时间戳（秒）
	 */
	public long getTime() {
		return this.time;
	}



	/**
	 * 获取当前UTC时间戳（秒）
	 * @return UTC时间戳
	 */
	public static long getCurrentUtcTimestamp() {
		return Instant.now().getEpochSecond();
	}

	/**
	 * 验证时间戳是否合理
	 * @param timestamp 时间戳（秒）
	 * @return true表示合理，false表示不合理
	 */
	public static boolean isValidTimestamp(long timestamp) {
		// 时间戳应该在2000年到2100年之间
		return timestamp >= 946684800L && timestamp <= 4102444800L;
	}


	@Override
	public Header decode(ByteBuf buf) {return null;}

	/**
	 * 创建消息头 (2011版本)
	 */
	public static Header create(int msgId, int msgbodyLength) {return null;}

	@Override
	public byte[] encode() {return null;}

}

