package cn.com.onlinetool.jt809.netty.codec.entity.platformMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 平台查岗应答消息 子业务类型标识:UP_PLATFORM_MSG_POST_QUERY_ACK
 * 描述:下级平台应答上级平台发送的不定期平台查岗消息
 */
@Setter
@Getter
@NoArgsConstructor
public class PostQueryAckEntity implements Codec<PostQueryAckEntity> {
	// ========== 查岗对象的类型常量 (DB32/T 3610.3—2025 表57 / 表53 / JT809-2019 表60) ==========
	public static final byte OBJECT_TYPE_PLATFORM = 0x01;
	public static final byte OBJECT_TYPE_ENTERPRISE = 0x02;

	private BasePlatFormMsgEntity baseMsg;

	// JT809 2019版本新增字段
	private byte objectType; // 查岗对象类型，定义见表60/表57 byte[1]
	private String responder; // 查岗应答人姓名 byte[16]
	private String responderTel; // 查岗应答人联系电话 byte[20]
	private String objectId; // 查岗对象的ID，长度不足时后补0x00 byte[20]

	@Field(length = 2, desc = "消息源子业务类型标识", version = {2019})
	private int sourceDataType;// 消息源子业务类型标识 byte[2]
	@Field(length = 4, desc = "源报文序列号", version = {2019})
	private long sourceMsgSn;// 源报文序列号 byte[4]

	@Field(length = 4, desc = "信息ID", version = {2011, 2025})
	private long infoId; // 信息ID，本ID跟下发的ID相同 byte[4]
	private long infoLength; // 数据长度 byte[4]
	private String infoContent; // 应答内容 byte[infoLength]

	@Override
	public PostQueryAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BasePlatFormMsgEntity().decode(buf);
		ByteBuf data = baseMsg.getData();
		if (data == null) {
			return this;
		}

		if (JT809Config.isJT809_2019()) {
			// 1. 尝试按 DB32/T 3610.3—2025 表57 格式解析 (头部29字节: OBJECT_TYPE(1) + OBJECT_ID(20) + INFO_ID(4) + INFO_LENGTH(4))
			if (data.readableBytes() >= 29) {
				long tryInfoLen2025 = data.getUnsignedInt(data.readerIndex() + 25);
				if (29 + tryInfoLen2025 == data.readableBytes()) {
					this.objectType = data.readByte();
					byte[] objectIdBytes = new byte[20];
					data.readBytes(objectIdBytes);
					this.objectId = cleanString(new String(objectIdBytes, Charset.forName("GBK")));
					this.infoId = data.readUnsignedInt();
					this.infoLength = data.readUnsignedInt();
					if (this.infoLength > 0 && data.readableBytes() >= this.infoLength) {
						byte[] contentBytes = new byte[(int) this.infoLength];
						data.readBytes(contentBytes);
						this.infoContent = new String(contentBytes, Charset.forName("GBK"));
					} else {
						this.infoContent = "";
					}
					return this;
				}
			}

			// 2. 尝试按 JT/T 809-2019 格式解析 (头部67字节: OBJECT_TYPE(1) + RESPONDER(16) + RESPONDER_TEL(20) + OBJECT_ID(20) + SOURCE_DATA_TYPE(2) + SOURCE_MSG_SN(4) + INFO_LENGTH(4))
			if (data.readableBytes() >= 67) {
				this.objectType = data.readByte();
				byte[] respBytes = new byte[16];
				data.readBytes(respBytes);
				this.responder = cleanString(new String(respBytes, Charset.forName("GBK")));

				byte[] telBytes = new byte[20];
				data.readBytes(telBytes);
				this.responderTel = cleanString(new String(telBytes, Charset.forName("GBK")));

				byte[] objBytes = new byte[20];
				data.readBytes(objBytes);
				this.objectId = cleanString(new String(objBytes, Charset.forName("GBK")));

				this.sourceDataType = data.readUnsignedShort();
				this.sourceMsgSn = data.readUnsignedInt();
				this.infoLength = data.readUnsignedInt();

				if (this.infoLength > 0 && data.readableBytes() >= this.infoLength) {
					byte[] contentBytes = new byte[(int) this.infoLength];
					data.readBytes(contentBytes);
					this.infoContent = new String(contentBytes, Charset.forName("GBK"));
				} else {
					this.infoContent = "";
				}
				return this;
			}
		}

		// 3. 2011版本协议: INFO_ID(4) + INFO_LENGTH(4) + INFO_CONTENT
		if (data.readableBytes() >= 8) {
			this.infoId = data.readUnsignedInt();
			this.infoLength = data.readUnsignedInt();
			if (this.infoLength > 0 && data.readableBytes() >= this.infoLength) {
				byte[] contentBytes = new byte[(int) this.infoLength];
				data.readBytes(contentBytes);
				this.infoContent = new String(contentBytes, Charset.forName("GBK"));
			} else {
				this.infoContent = "";
			}
		}
		return this;
	}

	// 2019版本协议调用的方法
	public static PostQueryAckEntity create(int sourceDataType, long sourceMsgSn, String infoContent, byte objectType, String responder, String responderTel, String objectId) {
		PostQueryAckEntity entity = new PostQueryAckEntity();

		// 设置实体字段
		entity.objectType = objectType;
		entity.responder = responder;
		entity.responderTel = responderTel;
		entity.objectId = objectId;
		entity.sourceDataType = sourceDataType;
		entity.sourceMsgSn = sourceMsgSn;
		entity.infoContent = infoContent;

		byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;

		// 构建RESPONDER字节数组，固定16字节，不足部分补0x00
		byte[] responderBytes = new byte[16];
		byte[] responderSourceBytes = responder.getBytes(Charset.forName("GBK"));
		System.arraycopy(responderSourceBytes, 0, responderBytes, 0,
				Math.min(responderSourceBytes.length, 16));

		// 构建RESPONDER_TEL字节数组，固定20字节，不足部分补0x00
		byte[] responderTelBytes = new byte[20];
		byte[] responderTelSourceBytes = responderTel.getBytes(Charset.forName("GBK"));
		System.arraycopy(responderTelSourceBytes, 0, responderTelBytes, 0,
				Math.min(responderTelSourceBytes.length, 20));

		// 构建OBJECT_ID字节数组，固定20字节，不足部分补0x00
		byte[] objectIdBytes = new byte[20];
		byte[] objectIdSourceBytes = objectId.getBytes(Charset.forName("GBK"));
		System.arraycopy(objectIdSourceBytes, 0, objectIdBytes, 0,
				Math.min(objectIdSourceBytes.length, 20));

		// 按照协议顺序构建数据：OBJECT_TYPE(1) + RESPONDER(16) + RESPONDER_TEL(20) + OBJECT_ID(20) + SOURCE_DATA_TYPE(2) + SOURCE_MSG_SN(4) + INFO_LENGTH(4) + INFO_CONTENT
		byte[] additionalData = ByteUtil.combineByteArray(
				new byte[]{objectType}, // OBJECT_TYPE 1字节
				responderBytes, // RESPONDER 16字节
				responderTelBytes, // RESPONDER_TEL 20字节
				objectIdBytes, // OBJECT_ID 20字节
				ByteUtil.short2Bytes((short) entity.sourceDataType), // SOURCE_DATA_TYPE 2字节
				ByteUtil.int2Byte((int) entity.sourceMsgSn), // SOURCE_MSG_SN 4字节
				ByteUtil.int2Byte((int) entity.infoLength), // INFO_LENGTH 4字节
				infoContentBytes); // INFO_CONTENT

		entity.baseMsg = BasePlatFormMsgEntity.create(
				MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
				MsgConstant.SubServiceType.UP_PLATFORM_MSG_POST_QUERY_ACK,
				additionalData);
		return entity;
	}

	/**
	 * DB32/T 3610.3—2025 表57 平台查岗应答消息构造方法
	 *
	 * @param objectType  查岗对象的类型 (0x01:平台, 0x02:业户)
	 * @param objectId    查岗对象的ID (20字节, 长度不足后补0x00)
	 * @param infoId      信息ID (uint32_t 4字节, 本ID跟下发的ID相同)
	 * @param infoContent 应答内容 (Octet String)
	 * @return PostQueryAckEntity 实例
	 */
	public static PostQueryAckEntity create(byte objectType, String objectId, long infoId, String infoContent) {
		PostQueryAckEntity entity = new PostQueryAckEntity();
		entity.objectType = objectType;
		entity.objectId = objectId != null ? objectId : "";
		entity.infoId = infoId;
		entity.infoContent = infoContent != null ? infoContent : "";

		byte[] infoContentBytes = entity.infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;

		byte[] objectIdBytes = new byte[20];
		if (objectId != null) {
			byte[] src = objectId.getBytes(Charset.forName("GBK"));
			System.arraycopy(src, 0, objectIdBytes, 0, Math.min(src.length, 20));
		}

		byte[] additionalData = ByteUtil.combineByteArray(
				new byte[]{objectType},
				objectIdBytes,
				ByteUtil.int2Byte((int) entity.infoId),
				ByteUtil.int2Byte((int) entity.infoLength),
				infoContentBytes);

		entity.baseMsg = BasePlatFormMsgEntity.create(
				MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
				MsgConstant.SubServiceType.UP_PLATFORM_MSG_POST_QUERY_ACK,
				additionalData);
		return entity;
	}

	public static PostQueryAckEntity create(long infoId, String infoContent) {
		PostQueryAckEntity entity = new PostQueryAckEntity();
		entity.infoId = infoId;
		byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;
		entity.infoContent = infoContent;

		entity.baseMsg = BasePlatFormMsgEntity.create(
				MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
				MsgConstant.SubServiceType.UP_PLATFORM_MSG_POST_QUERY_ACK,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.infoId),
						ByteUtil.int2Byte((int) entity.infoLength), infoContentBytes));
		return entity;
	}

	private static String cleanString(String str) {
		if (str == null) {
			return null;
		}
		int nullIdx = str.indexOf('\0');
		if (nullIdx >= 0) {
			str = str.substring(0, nullIdx);
		}
		return str.trim();
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
