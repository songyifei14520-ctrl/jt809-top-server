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
 * 链路类型: 从链路
 * 消息方向: 上级平台向下级平台
 * 业务类型标识: DOWN_PLATFORM_MSG (0x9300)
 * 子业务类型标识: DOWN_PLATFORM_MSG_POST_QUERY_REQ (0x9301)
 * 描述: 上级平台不定期向下级平台发送平台查岗信息, 其数据体定义见表 56。
 * 依据: DB32/T 3610.3—2025 6.2.4.1 表 56 平台查岗请求消息数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class DownPlatformMsgPostQueryReqEntity implements Codec<DownPlatformMsgPostQueryReqEntity> {

    // ========== 查岗对象的类型常量 (DB32/T 3610.3—2025 表56) ==========
    /** 0x01: 当前连接的下级平台(由平台行政区划代码和平台唯一编码组成) */
    public static final byte OBJECT_TYPE_PLATFORM = 0x01;
    /** 0x02: 下级平台下属的业户(为业户经营许可证号) */
    public static final byte OBJECT_TYPE_ENTERPRISE = 0x02;
    /** 0x03: 全部下级平台及所属业户(全部填0x00) */
    public static final byte OBJECT_TYPE_ALL = 0x03;

    private BasePlatFormMsgEntity baseMsg;

    // ========== 表 56 数据部分字段 ==========
    @Field(length = 1, desc = "查岗对象的类型: 0x01下级平台 0x02业户 0x03全部", version = {2025})
    private byte objectType; // 查岗对象的类型 BYTE 1字节

    @Field(length = 20, desc = "查岗对象的ID, 长度不足时后补0x00", version = {2025})
    private String objectId; // 查岗对象的ID Octet String 20字节

    @Field(length = 4, desc = "信息ID", version = {2025})
    private long infoId; // 信息ID uint32_t 4字节

    @Field(length = 4, desc = "信息长度", version = {2025})
    private long infoLength; // 信息长度 uint32_t 4字节

    @Field(desc = "信息内容", version = {2025})
    private String infoContent; // 信息内容 Octet String

    @Override
    public DownPlatformMsgPostQueryReqEntity decode(ByteBuf buf) {
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
     * DB32/T 3610.3—2025 表56 平台查岗请求消息构造方法
     *
     * @param objectType  查岗对象的类型 (0x01:平台, 0x02:业户, 0x03:全部)
     * @param objectId    查岗对象的ID (20字节, 长度不足后补0x00)
     * @param infoId      信息ID (uint32_t 4字节)
     * @param infoContent 信息内容 (Octet String)
     * @return DownPlatformMsgPostQueryReqEntity 实例
     */
    public static DownPlatformMsgPostQueryReqEntity create(byte objectType, String objectId, long infoId, String infoContent) {
        DownPlatformMsgPostQueryReqEntity entity = new DownPlatformMsgPostQueryReqEntity();
        entity.objectType = objectType;
        entity.objectId = objectId != null ? objectId : "";
        entity.infoId = infoId;
        entity.infoContent = infoContent != null ? infoContent : "";

        byte[] contentBytes = entity.infoContent.getBytes(Charset.forName("GBK"));
        entity.infoLength = contentBytes.length;

        byte[] objectIdBytes = new byte[20];
        if (objectId != null && objectType != OBJECT_TYPE_ALL) {
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
                MsgConstant.PrimaryServiceType.DOWN_PLATFORM_MSG,
                MsgConstant.SubServiceType.DOWN_PLATFORM_MSG_POST_QUERY_REQ,
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
            case OBJECT_TYPE_ALL:
                return "全部下级平台及所属业户";
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
