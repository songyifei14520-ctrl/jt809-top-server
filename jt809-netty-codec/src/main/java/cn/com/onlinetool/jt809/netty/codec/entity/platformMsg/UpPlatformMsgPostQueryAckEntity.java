package cn.com.onlinetool.jt809.netty.codec.entity.platformMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型: 主链路
 * 消息方向: 下级平台向上级平台
 * 业务类型标识: UP_PLATFORM_MSG (0x1300)
 * 子业务类型标识: UP_PLATFORM_MSG_POST_QUERY_ACK (0x1301)
 * 描述: 下级平台根据查岗对象的类型将上级平台发送的不定期平台查岗消息发送到不同的查岗对象, 并将不同查岗对象的应答分别转发给上级平台, 其数据体定义见表 57。
 * 依据: DB32/T 3610.3—2025 6.2.4.2 表 57 平台查岗应答消息数据体及续表
 */
@Setter
@Getter
@NoArgsConstructor
public class UpPlatformMsgPostQueryAckEntity implements Codec<UpPlatformMsgPostQueryAckEntity> {

    // ========== 查岗对象的类型常量 (DB32/T 3610.3—2025 表57 / 表53) ==========
    /** 0x01: 平台 (由平台行政区划代码和平台唯一编码组成) */
    public static final byte OBJECT_TYPE_PLATFORM = 0x01;
    /** 0x02: 业户 (为业户经营许可证号) */
    public static final byte OBJECT_TYPE_ENTERPRISE = 0x02;

    private BasePlatFormMsgEntity baseMsg;

    // ========== 表 57 数据部分字段 ==========
    @Field(length = 1, desc = "查岗对象的类型: 0x01平台 0x02业户", version = {2025})
    private byte objectType; // 查岗对象的类型 BYTE 1字节

    @Field(length = 20, desc = "查岗对象的ID, 长度不足时后补0x00", version = {2025})
    private String objectId; // 查岗对象的ID Octet String 20字节

    @Field(length = 4, desc = "信息ID, 本ID跟下发的ID相同", version = {2025})
    private long infoId; // 信息ID uint32_t 4字节

    @Field(length = 4, desc = "数据长度", version = {2025})
    private long infoLength; // 数据长度 uint32_t 4字节

    @Field(desc = "应答内容", version = {2025})
    private String infoContent; // 应答内容 Octet String

    @Override
    public UpPlatformMsgPostQueryAckEntity decode(ByteBuf buf) {
        this.baseMsg = new BasePlatFormMsgEntity().decode(buf);
        ByteBuf data = this.baseMsg.getData();

        if (data != null && data.readableBytes() >= 29) { // 1 + 20 + 4 + 4 = 29字节
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
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表57 平台查岗应答消息构造方法
     *
     * @param objectType  查岗对象的类型 (0x01:平台, 0x02:业户)
     * @param objectId    查岗对象的ID (20字节, 长度不足后补0x00)
     * @param infoId      信息ID (uint32_t 4字节, 本ID跟下发的ID相同)
     * @param infoContent 应答内容 (Octet String)
     * @return UpPlatformMsgPostQueryAckEntity 实例
     */
    public static UpPlatformMsgPostQueryAckEntity create(byte objectType, String objectId, long infoId, String infoContent) {
        UpPlatformMsgPostQueryAckEntity entity = new UpPlatformMsgPostQueryAckEntity();
        entity.objectType = objectType;
        entity.objectId = objectId != null ? objectId : "";
        entity.infoId = infoId;
        entity.infoContent = infoContent != null ? infoContent : "";

        byte[] contentBytes = entity.infoContent.getBytes(Charset.forName("GBK"));
        entity.infoLength = contentBytes.length;

        byte[] objectIdBytes = new byte[20];
        if (objectId != null) {
            byte[] src = objectId.getBytes(Charset.forName("GBK"));
            System.arraycopy(src, 0, objectIdBytes, 0, Math.min(src.length, 20));
        }

        byte[] data = ByteUtil.combineByteArray(
                new byte[]{objectType},
                objectIdBytes,
                ByteUtil.int2Byte((int) entity.infoId),
                ByteUtil.int2Byte((int) entity.infoLength),
                contentBytes
        );

        entity.baseMsg = BasePlatFormMsgEntity.create(
                MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
                MsgConstant.SubServiceType.UP_PLATFORM_MSG_POST_QUERY_ACK,
                data
        );
        return entity;
    }

    /**
     * 获取查岗对象类型描述
     */
    public static String getObjectTypeName(byte objectType) {
        switch (objectType) {
            case OBJECT_TYPE_PLATFORM:
                return "下级平台";
            case OBJECT_TYPE_ENTERPRISE:
                return "下级平台所属业户";
            default:
                return "未知查岗对象类型(" + String.format("0x%02X", objectType) + ")";
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
