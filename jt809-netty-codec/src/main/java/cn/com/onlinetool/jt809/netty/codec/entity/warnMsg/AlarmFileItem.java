package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * 依据: DB32/T 3610.3—2025 表 50 及 表 50（续） 报警附件列表数据体
 * 用于智能监控报警附件目录请求应答 (0x1404) 和 报警附件主动上报 (0x1407)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmFileItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // ========== 报警附件文件类型常量 (DB32/T 3610.3—2025 表50续) ==========
    /** 0x00: 图片 */
    public static final byte FILE_TYPE_PICTURE = 0x00;
    /** 0x01: 音频 */
    public static final byte FILE_TYPE_AUDIO = 0x01;
    /** 0x02: 视频 */
    public static final byte FILE_TYPE_VIDEO = 0x02;
    /** 0x03: 记录文件 */
    public static final byte FILE_TYPE_RECORD = 0x03;
    /** 0x04: 其他 */
    public static final byte FILE_TYPE_OTHER = 0x04;

    /** 文件名称长度 BYTE (1字节) */
    @Field(length = 1, desc = "文件名称长度", version = {2025})
    private short fileNameLength;

    /** 文件名称 Octet String */
    @Field(desc = "文件名称", version = {2025})
    private String fileName;

    /**
     * 文件类型 BYTE (1字节)
     * 0x00: 图片
     * 0x01: 音频
     * 0x02: 视频
     * 0x03: 记录文件
     * 0x04: 其他
     */
    @Field(length = 1, desc = "文件类型: 0x00.图片 0x01.音频 0x02.视频 0x03.记录文件 0x04.其他", version = {2025})
    private byte fileType;

    /** 文件大小 uint32_t (4字节) */
    @Field(length = 4, desc = "文件大小", version = {2025})
    private long fileSize;

    /** 文件URL长度 BYTE (1字节) */
    @Field(length = 1, desc = "文件URL长度", version = {2025})
    private short fileUrlLength;

    /** 当前报警附件的完整URL地址 Octet String */
    @Field(desc = "文件URL", version = {2025})
    private String fileUrl;

    public AlarmFileItem(String fileName, byte fileType, long fileSize, String fileUrl) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileNameLength = (short) (fileName != null ? fileName.getBytes(Charset.forName("GBK")).length : 0);
        this.fileUrlLength = (short) (fileUrl != null ? fileUrl.getBytes(Charset.forName("GBK")).length : 0);
    }

    public byte[] encode() {
        byte[] nameBytes = fileName != null ? fileName.getBytes(Charset.forName("GBK")) : new byte[0];
        byte[] urlBytes = fileUrl != null ? fileUrl.getBytes(Charset.forName("GBK")) : new byte[0];

        int totalLen = 1 + nameBytes.length + 1 + 4 + 1 + urlBytes.length;
        byte[] bytes = new byte[totalLen];

        int offset = 0;
        // 1. 文件名称长度 BYTE
        bytes[offset++] = (byte) nameBytes.length;

        // 2. 文件名称 Octet String
        if (nameBytes.length > 0) {
            System.arraycopy(nameBytes, 0, bytes, offset, nameBytes.length);
            offset += nameBytes.length;
        }

        // 3. 文件类型 BYTE
        bytes[offset++] = fileType;

        // 4. 文件大小 uint32_t (4字节大端)
        System.arraycopy(ByteUtil.int2Byte((int) fileSize), 0, bytes, offset, 4);
        offset += 4;

        // 5. 文件URL长度 BYTE
        bytes[offset++] = (byte) urlBytes.length;

        // 6. 文件URL Octet String
        if (urlBytes.length > 0) {
            System.arraycopy(urlBytes, 0, bytes, offset, urlBytes.length);
        }

        return bytes;
    }

    public static AlarmFileItem decode(ByteBuf data) {
        if (data == null || data.readableBytes() < 1) {
            return null;
        }
        AlarmFileItem item = new AlarmFileItem();

        // 1. 文件名称长度
        int nameLen = data.readUnsignedByte();
        item.fileNameLength = (short) nameLen;

        // 2. 文件名称
        if (nameLen > 0 && data.readableBytes() >= nameLen) {
            byte[] nameBytes = new byte[nameLen];
            data.readBytes(nameBytes);
            item.fileName = cleanString(new String(nameBytes, Charset.forName("GBK")));
        } else {
            item.fileName = "";
        }

        // 3. 文件类型
        if (data.readableBytes() >= 1) {
            item.fileType = data.readByte();
        }

        // 4. 文件大小 (4字节 uint32)
        if (data.readableBytes() >= 4) {
            item.fileSize = data.readUnsignedInt();
        }

        // 5. 文件URL长度
        if (data.readableBytes() >= 1) {
            int urlLen = data.readUnsignedByte();
            item.fileUrlLength = (short) urlLen;

            // 6. 文件URL
            if (urlLen > 0 && data.readableBytes() >= urlLen) {
                byte[] urlBytes = new byte[urlLen];
                data.readBytes(urlBytes);
                item.fileUrl = cleanString(new String(urlBytes, Charset.forName("GBK")));
            } else {
                item.fileUrl = "";
            }
        } else {
            item.fileUrl = "";
        }

        return item;
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
}
