package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 链路类型: 主链路
 * 消息方向: 下级平台向上级平台
 * 业务类型标识: UP_WARN_MSG (0x1400)
 * 子业务类型标识: UP_WARN_MSG_FILELIST_ACK (0x1404)
 * 描述: 下级平台向上级平台发送智能监控报警附件目录请求应答业务, 上级平台可通过报警附件文件 URL 以 FTP 协议自行下载报警附件文件, 其数据体定义见表 49。
 * 依据: DB32/T 3610.3—2025 6.2.2.4 表 49 智能监控报警附件目录请求应答数据体 及 表 50 报警附件列表数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class UpWarnMsgFilelistAckEntity implements Codec<UpWarnMsgFilelistAckEntity> {

    // ========== 车牌颜色常量 (DB32/T 3610.3—2025 表49) ==========
    /** 0x01: 蓝色 */
    public static final byte COLOR_BLUE = 0x01;
    /** 0x02: 黄色 */
    public static final byte COLOR_YELLOW = 0x02;
    /** 0x03: 黑色 */
    public static final byte COLOR_BLACK = 0x03;
    /** 0x04: 白色 */
    public static final byte COLOR_WHITE = 0x04;
    /** 0x05: 绿色 */
    public static final byte COLOR_GREEN = 0x05;
    /** 0x06: 黄绿色 */
    public static final byte COLOR_YELLOW_GREEN = 0x06;
    /** 0x07: 渐变绿 */
    public static final byte COLOR_GRADIENT_GREEN = 0x07;
    /** 0x09: 其他 */
    public static final byte COLOR_OTHER = 0x09;

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

    private BaseWarnMsgEntity baseMsg;

    // ========== 表 49 字段 ==========
    @Field(length = 21, desc = "车牌号码", version = {2025})
    private String vehicleNo; // 车牌号码 byte[21]

    @Field(length = 1, desc = "车牌颜色", version = {2025})
    private byte vehicleColor; // 车牌颜色 byte[1]

    // ========== 表 49 数据部分字段 ==========
    @Field(length = 32, desc = "报警信息ID", version = {2025})
    private String infoId; // 报警信息ID Octet String 32字节

    @Field(length = 1, desc = "附件服务器地址长度", version = {2025})
    private short serverLength; // 附件服务器地址长度 BYTE 1字节

    @Field(desc = "附件服务器IP或域名", version = {2025})
    private String server; // 附件服务器IP或域名 Octet String

    @Field(length = 2, desc = "附件服务器FTP协议端口号", version = {2025})
    private int port; // 附件服务器FTP协议端口号 uint16_t 2字节

    @Field(length = 1, desc = "用户名长度", version = {2025})
    private short userNameLength; // 用户名长度 BYTE 1字节

    @Field(desc = "附件服务器用户名", version = {2025})
    private String userName; // 附件服务器用户名 Octet String

    @Field(length = 1, desc = "密码长度", version = {2025})
    private short passwordLength; // 密码长度 BYTE 1字节

    @Field(desc = "附件服务器密码", version = {2025})
    private String password; // 附件服务器密码 Octet String

    @Field(length = 1, desc = "附件数量", version = {2025})
    private short fileCount; // 附件数量 BYTE 1字节

    @Field(desc = "报警附件列表", version = {2025})
    private List<AlarmFileItem> fileList = new ArrayList<>(); // 报警附件列表见表 50


    @Override
    public UpWarnMsgFilelistAckEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseWarnMsgEntity().decodeWithVehicle(buf);
        this.vehicleNo = cleanString(this.baseMsg.getVehicleNo());
        this.vehicleColor = this.baseMsg.getVehicleColor();

        ByteBuf data = this.baseMsg.getData();
        this.fileList = new ArrayList<>();
        if (data != null && data.readableBytes() >= 32) {
            // 1. INFO_ID 32字节
            byte[] infoIdBytes = new byte[32];
            data.readBytes(infoIdBytes);
            this.infoId = cleanString(new String(infoIdBytes, Charset.forName("GBK")));

            // 2. SERVER_LENGTH + SERVER
            if (data.readableBytes() >= 1) {
                int sLen = data.readUnsignedByte();
                this.serverLength = (short) sLen;
                if (sLen > 0 && data.readableBytes() >= sLen) {
                    byte[] serverBytes = new byte[sLen];
                    data.readBytes(serverBytes);
                    this.server = cleanString(new String(serverBytes, Charset.forName("GBK")));
                } else {
                    this.server = "";
                }
            }

            // 3. PORT uint16_t (2字节)
            if (data.readableBytes() >= 2) {
                this.port = data.readUnsignedShort();
            }

            // 4. USERNAME_LENGTH + USERNAME
            if (data.readableBytes() >= 1) {
                int uLen = data.readUnsignedByte();
                this.userNameLength = (short) uLen;
                if (uLen > 0 && data.readableBytes() >= uLen) {
                    byte[] userBytes = new byte[uLen];
                    data.readBytes(userBytes);
                    this.userName = cleanString(new String(userBytes, Charset.forName("GBK")));
                } else {
                    this.userName = "";
                }
            }

            // 5. PASSWORD_LENGTH + PASSWORD
            if (data.readableBytes() >= 1) {
                int pLen = data.readUnsignedByte();
                this.passwordLength = (short) pLen;
                if (pLen > 0 && data.readableBytes() >= pLen) {
                    byte[] pwdBytes = new byte[pLen];
                    data.readBytes(pwdBytes);
                    this.password = cleanString(new String(pwdBytes, Charset.forName("GBK")));
                } else {
                    this.password = "";
                }
            }

            // 6. FILE_COUNT + FILE_LIST
            if (data.readableBytes() >= 1) {
                int count = data.readUnsignedByte();
                this.fileCount = (short) count;
                for (int i = 0; i < count && data.isReadable(); i++) {
                    AlarmFileItem item = AlarmFileItem.decode(data);
                    if (item != null) {
                        this.fileList.add(item);
                    }
                }
            }
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表49 智能监控报警附件目录请求应答消息构造方法
     *
     * @param vehicleNo    车牌号码 (21字节)
     * @param vehicleColor 车牌颜色 (0x01:蓝色, 0x02:黄色, 0x03:黑色, 0x04:白色, 0x05:绿色, 0x06:黄绿色, 0x07:渐变绿, 0x09:其他)
     * @param infoId       报警信息ID (32字节)
     * @param server       附件服务器IP或域名
     * @param port         附件服务器FTP协议端口号 (uint16_t)
     * @param userName     附件服务器用户名
     * @param password     附件服务器密码
     * @param fileList     报警附件列表
     * @return UpWarnMsgFilelistAckEntity 实例
     */
    public static UpWarnMsgFilelistAckEntity create(
            String vehicleNo, byte vehicleColor, String infoId,
            String server, int port, String userName, String password,
            List<AlarmFileItem> fileList) {

        UpWarnMsgFilelistAckEntity entity = new UpWarnMsgFilelistAckEntity();
        entity.vehicleNo = vehicleNo;
        entity.vehicleColor = vehicleColor;
        entity.infoId = infoId;
        entity.server = server != null ? server : "";
        entity.port = port;
        entity.userName = userName != null ? userName : "";
        entity.password = password != null ? password : "";
        entity.fileList = fileList != null ? fileList : new ArrayList<>();
        entity.fileCount = (short) entity.fileList.size();

        byte[] infoIdBytes = copyBytes(infoId, 32);

        byte[] serverBytes = entity.server.getBytes(Charset.forName("GBK"));
        entity.serverLength = (short) serverBytes.length;

        byte[] portBytes = ByteUtil.short2Bytes((short) port);

        byte[] userBytes = entity.userName.getBytes(Charset.forName("GBK"));
        entity.userNameLength = (short) userBytes.length;

        byte[] pwdBytes = entity.password.getBytes(Charset.forName("GBK"));
        entity.passwordLength = (short) pwdBytes.length;

        // 计算所有附件列表的总长度并组装数据
        List<byte[]> itemByteList = new ArrayList<>();
        int fileListTotalLength = 0;
        for (AlarmFileItem item : entity.fileList) {
            byte[] itemBytes = item.encode();
            itemByteList.add(itemBytes);
            fileListTotalLength += itemBytes.length;
        }

        byte[] fileBytes = new byte[fileListTotalLength];
        int fileOffset = 0;
        for (byte[] b : itemByteList) {
            System.arraycopy(b, 0, fileBytes, fileOffset, b.length);
            fileOffset += b.length;
        }

        byte[] data = ByteUtil.combineByteArray(
                infoIdBytes,
                new byte[]{(byte) entity.serverLength},
                serverBytes,
                portBytes,
                new byte[]{(byte) entity.userNameLength},
                userBytes,
                new byte[]{(byte) entity.passwordLength},
                pwdBytes,
                new byte[]{(byte) entity.fileCount},
                fileBytes
        );

        entity.baseMsg = BaseWarnMsgEntity.create(
                MsgConstant.PrimaryServiceType.UP_WARN_MSG,
                vehicleNo,
                vehicleColor,
                MsgConstant.SubServiceType.UP_WARN_MSG_FILELIST_ACK,
                data
        );
        return entity;
    }

    private static byte[] copyBytes(String src, int targetLen) {
        byte[] result = new byte[targetLen];
        if (src != null) {
            byte[] srcBytes = src.getBytes(Charset.forName("GBK"));
            System.arraycopy(srcBytes, 0, result, 0, Math.min(srcBytes.length, targetLen));
        }
        return result;
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
