package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * 协议版本2019
 * 链路类型:主链路 上报报警督办请求消息 子业务类型标识:UP_WARN_MSG_URGE_TODO_REQ_INFO
 * 描述:下级平台告知上级平台已对报警情况进行催办。
 * 表72上报报警督办请求消息数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class UrgeTodoReqInfoEntity implements Codec<UrgeTodoReqInfoEntity> {
	private BaseWarnMsgEntity baseMsg;

	/**
	 * 发起报警平台唯一编码，由平台所在地行政区划大码和平台编号组成 BYTES[11]
	 */
	private String platformId; // byte[11]

	/**
	 * 报警时间，UTC时间格式 time_t
	 */
	private long warnTime; // byte[8]

	/**
	 * 对应报警消息源子业务类型标识 uint16_t
	 */
	private int sourceDataType; // byte[2]

	/**
	 * 对应报警督办请求消息报文序号 uint32_t
	 */
	private long sourceMsgSn; // byte[4]

	/**
	 * 督办截止时间，UTC时间格式 time_t
	 */
	private long supervisionEndtime; // byte[8]

	/**
	 * 督办级别，定义如下：
	 * 0x00：紧急
	 * 0x01：一般
	 */
	private byte supervisionLevel; // byte[1]

	/**
	 * 督办人 Octet String[16]
	 */
	private String supervisor; // byte[16]

	/**
	 * 督办联系电话 Octet String[20]
	 */
	private String supervisorTel; // byte[20]

	/**
	 * 督办联系电子邮件 Octet String[32]
	 */
	private String supervisorEmail; // byte[32]

	@Override
	public UrgeTodoReqInfoEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseWarnMsgEntity().decode(buf);
		ByteBuf data = baseMsg.getData();

		// 跳过dataType(2) + dataLength(4) = 6字节
		// 读取平台ID (11字节)
		byte[] platformIdBytes = new byte[11];
		data.slice(6, 11).readBytes(platformIdBytes);
		this.platformId = new String(platformIdBytes, StandardCharsets.UTF_8).trim();

		this.warnTime = BufUtil.buf2SignedLong(data.slice(17, 8));
		this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(25, 2));
		this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(27, 4));
		this.supervisionEndtime = BufUtil.buf2SignedLong(data.slice(31, 8));
		this.supervisionLevel = data.getByte(39);

		// 读取督办人 (16字节)
		byte[] supervisorBytes = new byte[16];
		data.slice(40, 16).readBytes(supervisorBytes);
		this.supervisor = new String(supervisorBytes, StandardCharsets.UTF_8).trim();

		// 读取督办联系电话 (20字节)
		byte[] supervisorTelBytes = new byte[20];
		data.slice(56, 20).readBytes(supervisorTelBytes);
		this.supervisorTel = new String(supervisorTelBytes, StandardCharsets.UTF_8).trim();

		// 读取督办联系电子邮件 (32字节)
		byte[] supervisorEmailBytes = new byte[32];
		data.slice(76, 32).readBytes(supervisorEmailBytes);
		this.supervisorEmail = new String(supervisorEmailBytes, StandardCharsets.UTF_8).trim();

		return this;
	}

	public static UrgeTodoReqInfoEntity create(int sourceDataType, long warnTime, String platformId,
			long supervisionEndTime, byte supervisionLevel, String supervisor,
			String supervisorTel, String supervisorEmail) {
		UrgeTodoReqInfoEntity entity = new UrgeTodoReqInfoEntity();
		entity.platformId = platformId != null ? platformId : "";
		entity.warnTime = warnTime;
		entity.sourceDataType = sourceDataType; // 使用报警类型作为源数据类型
		entity.sourceMsgSn = System.currentTimeMillis(); // 使用当前时间戳作为报文序列号
		entity.supervisionEndtime = supervisionEndTime;
		entity.supervisionLevel = supervisionLevel;
		entity.supervisor = supervisor != null ? supervisor : "";
		entity.supervisorTel = supervisorTel != null ? supervisorTel : "";
		entity.supervisorEmail = supervisorEmail != null ? supervisorEmail : "";

		// 数据长度：dataType(2) + dataLength(4) + platformId(11) + warnTime(8) + sourceDataType(2) + sourceMsgSn(4) + supervisionEndtime(8) + supervisionLevel(1) + supervisor(16) + supervisorTel(20) + supervisorEmail(32) = 108字节
		int dataLength = 108;

		// 准备字符串字段的字节数组
		byte[] platformIdBytes = new byte[11];
		byte[] platformIdSrc = entity.platformId.getBytes(StandardCharsets.UTF_8);
		System.arraycopy(platformIdSrc, 0, platformIdBytes, 0, Math.min(platformIdSrc.length, 11));

		byte[] supervisorBytes = new byte[16];
		byte[] supervisorSrc = entity.supervisor.getBytes(StandardCharsets.UTF_8);
		System.arraycopy(supervisorSrc, 0, supervisorBytes, 0, Math.min(supervisorSrc.length, 16));

		byte[] supervisorTelBytes = new byte[20];
		byte[] supervisorTelSrc = entity.supervisorTel.getBytes(StandardCharsets.UTF_8);
		System.arraycopy(supervisorTelSrc, 0, supervisorTelBytes, 0, Math.min(supervisorTelSrc.length, 20));

		byte[] supervisorEmailBytes = new byte[32];
		byte[] supervisorEmailSrc = entity.supervisorEmail.getBytes(StandardCharsets.UTF_8);
		System.arraycopy(supervisorEmailSrc, 0, supervisorEmailBytes, 0, Math.min(supervisorEmailSrc.length, 32));

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.UP_WARN_MSG,MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_REQ_INFO,
				ByteUtil.combineByteArray(
						ByteUtil.short2Bytes((short) MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_REQ_INFO),
						ByteUtil.int2Byte(dataLength),
						platformIdBytes,
						ByteUtil.long2Bytes(entity.warnTime),
						ByteUtil.short2Bytes((short) entity.sourceDataType),
						ByteUtil.int2Byte((int) entity.sourceMsgSn),
						ByteUtil.long2Bytes(entity.supervisionEndtime),
						new byte[] { entity.supervisionLevel },
						supervisorBytes,
						supervisorTelBytes,
						supervisorEmailBytes));
		return entity;
	}



	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
