package cn.com.onlinetool.jt809.netty.codec.entity.platformMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:从链路 下发平台间报文请求消息 子业务类型标识:DOWN_PLATFORM_MSG_INFO_REQ
 * 描述:上级平台不定期向下级平台下发平台间报文
 * 依据: DB32/T 3610.3—2025 表58 / JT/T 809-2019 表61
 */
@Setter
@Getter
@NoArgsConstructor
public class InfoReqEntity implements Codec<InfoReqEntity> {

	// ========== 下发报文对象类型常量 (DB32/T 3610.3—2025 表58 / 表53 / JT809-2019 表61) ==========
	public static final byte OBJECT_TYPE_PLATFORM = 0x01;
	public static final byte OBJECT_TYPE_ENTERPRISE = 0x02;
	public static final byte OBJECT_TYPE_ALL = 0x03;

	private BasePlatFormMsgEntity baseMsg;

	// JT809 2019/2025版本新增字段
	@Field(length = 1, desc = "下发报文对象类型: 0x01平台 0x02业户 0x03全部", version = {2019, 2025})
	private byte objectType; // 下发报文对象类型，定义见表58/表61 byte[1]

	@Field(length = 20, desc = "下发报文对象的ID, 长度不足时后补0x00", version = {2019, 2025})
	private String objectId; // 下发报文对象的ID，长度不足时后补0x00 byte[20]

	@Field(length = 4, desc = "信息ID", version = {2011, 2019, 2025})
	private long infoId; // 信息ID byte[4]

	@Field(length = 4, desc = "数据长度", version = {2011, 2019, 2025})
	private long infoLength; // 数据长度 byte[4]

	@Field(desc = "信息内容", version = {2011, 2019, 2025})
	private String infoContent; // 信息内容 byte[infoLength]

	@Override
	public InfoReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BasePlatFormMsgEntity().decode(buf);
		ByteBuf data = baseMsg.getData();
		if (data == null) {
			return this;
		}

		if (JT809Config.isJT809_2019()) {
			// 1. 优先按 DB32/T 3610.3—2025 表58 / JT809-2019 格式解析 (头部29字节: OBJECT_TYPE(1) + OBJECT_ID(20) + INFO_ID(4) + INFO_LENGTH(4))
			if (data.readableBytes() >= 29) {
				long tryInfoLen = data.getUnsignedInt(data.readerIndex() + 25);
				if (29 + tryInfoLen == data.readableBytes()) {
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
		}

		// 2. 2011版本协议或回退: INFO_ID(4) + INFO_LENGTH(4) + INFO_CONTENT
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

	// 2019/2025版本协议调用的方法
	public static InfoReqEntity create(long infoId, String infoContent, byte objectType, String objectId) {
		return create(objectType, objectId, infoId, infoContent);
	}

	/**
	 * DB32/T 3610.3—2025 表58 下发平台间报文请求消息构造方法
	 *
	 * @param objectType  下发报文对象类型 (0x01:平台, 0x02:业户, 0x03:全部)
	 * @param objectId    下发报文对象的ID (20字节, 长度不足后补0x00, 对象类型>0x02时全部为0x00)
	 * @param infoId      信息ID (uint32_t 4字节)
	 * @param infoContent 信息内容 (Octet String)
	 * @return InfoReqEntity 实例
	 */
	public static InfoReqEntity create(byte objectType, String objectId, long infoId, String infoContent) {
		InfoReqEntity entity = new InfoReqEntity();
		entity.objectType = objectType;
		entity.objectId = objectId != null ? objectId : "";
		entity.infoId = infoId;
		entity.infoContent = infoContent != null ? infoContent : "";

		byte[] infoContentBytes = entity.infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;

		// 构建OBJECT_ID字节数组，固定20字节，不足部分补0x00
		byte[] objectIdBytes = new byte[20];
		if (objectId != null && objectType != OBJECT_TYPE_ALL) {
			byte[] objectIdSourceBytes = objectId.getBytes(Charset.forName("GBK"));
			System.arraycopy(objectIdSourceBytes, 0, objectIdBytes, 0,
					Math.min(objectIdSourceBytes.length, 20));
		}

		// 按照协议顺序构建数据：OBJECT_TYPE(1) + OBJECT_ID(20) + INFO_ID(4) + INFO_LENGTH(4) + INFO_CONTENT
		byte[] additionalData = ByteUtil.combineByteArray(
				new byte[]{objectType}, // OBJECT_TYPE 1字节
				objectIdBytes, // OBJECT_ID 20字节
				ByteUtil.int2Byte((int) entity.infoId), // INFO_ID 4字节
				ByteUtil.int2Byte((int) entity.infoLength), // INFO_LENGTH 4字节
				infoContentBytes); // INFO_CONTENT

		entity.baseMsg = BasePlatFormMsgEntity.create(
				MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
				MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_INFO_REQ,
				additionalData);
		return entity;
	}

	public static InfoReqEntity create(long infoId, String infoContent) {
		InfoReqEntity entity = new InfoReqEntity();
		entity.infoId = infoId;
		byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;
		entity.infoContent = infoContent;

		entity.baseMsg = BasePlatFormMsgEntity.create(
				MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
				MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_INFO_REQ,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.infoId),
						ByteUtil.int2Byte((int) entity.infoLength), infoContentBytes));
		return entity;
	}

	/**
	 * 获取下发报文对象类型描述
	 */
	public static String getObjectTypeName(byte objectType) {
		switch (objectType) {
			case OBJECT_TYPE_PLATFORM:
				return "下级平台";
			case OBJECT_TYPE_ENTERPRISE:
				return "下级平台所属业户";
			case OBJECT_TYPE_ALL:
				return "全部下级平台及所属业户";
			default:
				return "未知对象类型(" + String.format("0x%02X", objectType) + ")";
		}
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
