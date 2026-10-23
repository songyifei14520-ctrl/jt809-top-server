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
 * 链路类型:从链路 平台查岗请求消息 子业务类型标识:DOWN_PLATFORM_MSG_POST_QUERY_REQ
 * 描述:上级平台不定期向下级平台发送平台查岗信息
 */
@Setter
@Getter
@NoArgsConstructor
public class PostQueryReqEntity implements Codec<PostQueryReqEntity> {
	private BasePlatFormMsgEntity baseMsg;

	// JT809 2019版本新增字段
	private byte objectType; // 查岗对象的类型，定义见表61 byte[1]
	private String objectId; // 查岗对象的ID，长度不足时后补0x00 byte[20]
	private byte answerTime; // 查岗应答时限，以本条消息TIME时间开始计算 byte[1]

	private long infoId; // 信息ID，本ID跟下发的ID相同 byte[4]
	private long infoLength; // 数据长度 byte[4]
	private String infoContent; // 应答内容 byte[infoLength]

	@Override
	public PostQueryReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BasePlatFormMsgEntity().decode(buf);
		ByteBuf data = baseMsg.getData();
		if (data == null) {
			return this;
		}

		if (JT809Config.isJT809_2019()) {
			// 判断是 DB32/T 3610.3—2025 表56格式 (无 ANSWER_TIME, 29字节头) 还是 JT/T 809-2019 (含 ANSWER_TIME, 30字节头)
			if (data.readableBytes() >= 29) {
				this.objectType = data.readByte(); // OBJECT_TYPE 1字节

				byte[] objectIdBytes = new byte[20];
				data.readBytes(objectIdBytes);
				int validLength = 0;
				for (int i = 0; i < objectIdBytes.length; i++) {
					if (objectIdBytes[i] == 0) {
						break;
					}
					validLength++;
				}
				this.objectId = new String(objectIdBytes, 0, validLength, Charset.forName("GBK"));

				// 试探判断是否有 1 字节的 answerTime:
				// 如果按 2025 格式 (无 answerTime): 紧接是 infoId(4) + infoLength(4)
				// 此时读取后剩余字节数若等于 infoLength，则为 2025 格式
				boolean is2025Format = false;
				if (data.readableBytes() >= 8) {
					int markReaderIndex = data.readerIndex();
					long tryInfoId2025 = data.readUnsignedInt();
					long tryInfoLen2025 = data.readUnsignedInt();
					if (data.readableBytes() == tryInfoLen2025) {
						is2025Format = true;
						this.answerTime = 0;
						this.infoId = tryInfoId2025;
						this.infoLength = tryInfoLen2025;
					} else {
						data.readerIndex(markReaderIndex);
					}
				}

				if (!is2025Format && data.readableBytes() >= 9) {
					// 2019 格式: ANSWER_TIME(1) + INFO_ID(4) + INFO_LENGTH(4)
					this.answerTime = data.readByte();
					this.infoId = data.readUnsignedInt();
					this.infoLength = data.readUnsignedInt();
				}

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

		// 2011版本协议: INFO_ID(4) + INFO_LENGTH(4) + INFO_CONTENT
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
	public static PostQueryReqEntity create(long infoId, String infoContent, byte objectType, String objectId, byte answerTime) {
		PostQueryReqEntity entity = new PostQueryReqEntity();

		// 设置实体字段
		entity.objectType = objectType;
		entity.objectId = objectId;
		entity.answerTime = answerTime;
		entity.infoId = infoId;
		entity.infoContent = infoContent;

		byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;

		// 构建OBJECT_ID字节数组，固定20字节，不足部分补0x00
		byte[] objectIdBytes = new byte[20];
		byte[] objectIdSourceBytes = objectId.getBytes(Charset.forName("GBK"));
		System.arraycopy(objectIdSourceBytes, 0, objectIdBytes, 0,
				Math.min(objectIdSourceBytes.length, 20));

		// 按照协议顺序构建数据：OBJECT_TYPE(1) + OBJECT_ID(20) + ANSWER_TIME(1) + INFO_ID(4) + INFO_LENGTH(4) + INFO_CONTENT
		byte[] additionalData = ByteUtil.combineByteArray(
				new byte[]{objectType}, // OBJECT_TYPE 1字节
				objectIdBytes, // OBJECT_ID 20字节
				new byte[]{answerTime}, // ANSWER_TIME 1字节
				ByteUtil.int2Byte((int) entity.infoId), // INFO_ID 4字节
				ByteUtil.int2Byte((int) entity.infoLength), // INFO_LENGTH 4字节
				infoContentBytes); // INFO_CONTENT

		entity.baseMsg = BasePlatFormMsgEntity.create(
				MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
				MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_POST_QUERY_REQ,
				additionalData);
		return entity;
	}

    public static PostQueryReqEntity create(long infoId, String infoContent) {
        PostQueryReqEntity entity = new PostQueryReqEntity();
        entity.infoId = infoId;
        byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
        entity.infoLength = infoContentBytes.length;
        entity.infoContent = infoContent;

        entity.baseMsg = BasePlatFormMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
            MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_POST_QUERY_REQ,
            ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.infoId),
                ByteUtil.int2Byte((int) entity.infoLength), infoContentBytes));
        return entity;
    }

	/**
	 * DB32/T 3610.3—2025 表56 平台查岗请求消息构造方法 (无 ANSWER_TIME)
	 */
	public static PostQueryReqEntity create(byte objectType, String objectId, long infoId, String infoContent) {
		PostQueryReqEntity entity = new PostQueryReqEntity();
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
				MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
				MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_POST_QUERY_REQ,
				additionalData);
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
