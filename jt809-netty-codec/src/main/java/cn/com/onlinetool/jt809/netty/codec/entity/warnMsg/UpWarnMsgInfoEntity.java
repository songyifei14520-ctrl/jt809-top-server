package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型: 主链路
 * 消息方向: 下级平台往上级平台
 * 业务类型标识: UP_WARN_MSG (0x1400)
 * 子业务类型标识: UP_WARN_MSG_INFO (0x1402)
 * 描述: 下级平台主动向上级平台上报某车辆上传的报警信息, 其数据体定义见表 46 及表 46（续）。本条消息上级平台无需应答。本条消息服务端无需应答。
 * 依据: DB32/T 3610.3—2025 6.2.2.1 表 46 与 表 46（续） 主动上报报警信息消息数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class UpWarnMsgInfoEntity implements Codec<UpWarnMsgInfoEntity> {

    // ========== 车牌颜色常量 (DB32/T 3610.3—2025 表46) ==========
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

    // ========== 报警信息来源常量 (DB32/T 3610.3—2025 表46) ==========
    /** 0x01: 车载终端 */
    public static final byte WARN_SRC_TERMINAL = 0x01;
    /** 0x02: 企业监控平台 */
    public static final byte WARN_SRC_ENTERPRISE_PLATFORM = 0x02;
    /** 0x03: 监控平台 */
    public static final byte WARN_SRC_MONITOR_PLATFORM = 0x03;
    /** 0x09: 其他 */
    public static final byte WARN_SRC_OTHER = 0x09;

    // ========== 报警状态常量 (DB32/T 3610.3—2025 表46续) ==========
    /** 0x01: 报警开始 */
    public static final byte STATUS_START = 0x01;
    /** 0x02: 报警结束 */
    public static final byte STATUS_END = 0x02;

    private BaseWarnMsgEntity baseMsg;

    // ========== 表 46 字段 ==========
    @Field(length = 21, desc = "车牌号码", version = {2025})
    private String vehicleNo; // 车牌号码 byte[21]

    @Field(length = 1, desc = "车牌颜色", version = {2025})
    private byte vehicleColor; // 车牌颜色 byte[1]

    @Field(length = 1, desc = "报警信息来源", version = {2025})
    private byte warnSrc; // 报警信息来源 byte[1]

    @Field(length = 2, desc = "报警类型", version = {2025})
    private int warnType; // 报警类型 uint16_t 2字节

    @Field(length = 8, desc = "报警时间(UTC)", version = {2025})
    private long warnTime; // 报警时间，UTC时间格式 time_t 8字节

    // ========== 表 46（续）字段 ==========
    @Field(length = 32, desc = "报警信息ID", version = {2025})
    private String infoId; // 报警信息ID Octet String 32字节

    @Field(length = 1, desc = "驾驶员姓名长度", version = {2025})
    private short driverLength; // 驾驶员姓名长度 BYTE 1字节

    @Field(desc = "驾驶员姓名", version = {2025})
    private String driver; // 驾驶员姓名 Octet String

    @Field(length = 1, desc = "驾驶员驾照号码长度", version = {2025})
    private short driverNoLength; // 驾驶员驾照号码长度 BYTE 1字节

    @Field(desc = "驾驶员驾照号码", version = {2025})
    private String driverNo; // 驾驶员驾照号码 Octet String

    @Field(length = 1, desc = "报警级别", version = {2025})
    private byte level; // 报警级别 BYTE 1字节

    @Field(length = 4, desc = "经度,单位为度(°)", version = {2025})
    private long lon; // 经度,单位为度(°),乘以10^6 uint32_t 4字节

    @Field(length = 4, desc = "纬度,单位为度(°)", version = {2025})
    private long lat; // 纬度,单位为度(°),乘以10^6 uint32_t 4字节

    @Field(length = 2, desc = "单位m,海拔高度", version = {2025})
    private int altitude; // 单位m,海拔高度 uint16_t 2字节

    @Field(length = 2, desc = "单位km/h,行车速度", version = {2025})
    private int vec1; // 单位km/h,行车速度 uint16_t 2字节

    @Field(length = 2, desc = "单位km/h,行驶记录速度", version = {2025})
    private int vec2; // 单位km/h,行驶记录速度 uint16_t 2字节

    @Field(length = 1, desc = "报警状态,1:报警开始;2:报警结束", version = {2025})
    private byte status; // 报警状态 BYTE 1字节

    @Field(length = 2, desc = "方向,0°~359°,正北为0,顺时针", version = {2025})
    private int direction; // 方向 uint16_t 2字节

    @Field(length = 2, desc = "报警数据长度,最长2048字节", version = {2025})
    private int infoLength; // 报警数据长度 uint16_t 2字节

    @Field(desc = "上报报警信息内容", version = {2025})
    private String infoContent; // 上报报警信息内容 Octet String

    @Override
    public UpWarnMsgInfoEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseWarnMsgEntity().decodeWithVehicle(buf);
        this.vehicleNo = cleanString(this.baseMsg.getVehicleNo());
        this.vehicleColor = this.baseMsg.getVehicleColor();

        ByteBuf data = this.baseMsg.getData();
        if (data != null && data.readableBytes() >= 11) {
            // 表 46 前半部分
            this.warnSrc = data.readByte();
            this.warnType = data.readUnsignedShort();
            this.warnTime = data.readLong();

            // 表 46（续）
            if (data.readableBytes() >= 32) {
                byte[] infoIdBytes = new byte[32];
                data.readBytes(infoIdBytes);
                this.infoId = cleanString(new String(infoIdBytes, Charset.forName("GBK")));
            }

            if (data.readableBytes() >= 1) {
                int dLen = data.readUnsignedByte();
                this.driverLength = (short) dLen;
                if (dLen > 0 && data.readableBytes() >= dLen) {
                    byte[] dBytes = new byte[dLen];
                    data.readBytes(dBytes);
                    this.driver = cleanString(new String(dBytes, Charset.forName("GBK")));
                } else {
                    this.driver = "";
                }
            }

            if (data.readableBytes() >= 1) {
                int dnLen = data.readUnsignedByte();
                this.driverNoLength = (short) dnLen;
                if (dnLen > 0 && data.readableBytes() >= dnLen) {
                    byte[] dnBytes = new byte[dnLen];
                    data.readBytes(dnBytes);
                    this.driverNo = cleanString(new String(dnBytes, Charset.forName("GBK")));
                } else {
                    this.driverNo = "";
                }
            }

            if (data.readableBytes() >= 1) {
                this.level = data.readByte();
            }

            if (data.readableBytes() >= 4) {
                this.lon = data.readUnsignedInt();
            }

            if (data.readableBytes() >= 4) {
                this.lat = data.readUnsignedInt();
            }

            if (data.readableBytes() >= 2) {
                this.altitude = data.readUnsignedShort();
            }

            if (data.readableBytes() >= 2) {
                this.vec1 = data.readUnsignedShort();
            }

            if (data.readableBytes() >= 2) {
                this.vec2 = data.readUnsignedShort();
            }

            if (data.readableBytes() >= 1) {
                this.status = data.readByte();
            }

            if (data.readableBytes() >= 2) {
                this.direction = data.readUnsignedShort();
            }

            if (data.readableBytes() >= 2) {
                int iLen = data.readUnsignedShort();
                this.infoLength = iLen;
                if (iLen > 0 && data.readableBytes() >= iLen) {
                    byte[] cBytes = new byte[iLen];
                    data.readBytes(cBytes);
                    this.infoContent = new String(cBytes, Charset.forName("GBK"));
                } else {
                    this.infoContent = "";
                }
            }
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表46 及 表46（续）完整主动上报报警信息消息构造方法
     *
     * @param vehicleNo    车牌号码 (21字节)
     * @param vehicleColor 车牌颜色 (0x01:蓝色, 0x02:黄色, 0x03:黑色, 0x04:白色, 0x05:绿色, 0x06:黄绿色, 0x07:渐变绿, 0x09:其他)
     * @param warnSrc      报警信息来源 (0x01:车载终端, 0x02:企业监控平台, 0x03:监控平台, 0x09:其他)
     * @param warnType     报警类型
     * @param warnTime     报警时间，UTC时间格式 (time_t 8字节)
     * @param infoId       报警信息ID (32字节)
     * @param driver       驾驶员姓名 (变长)
     * @param driverNo     驾驶员驾照号码 (变长)
     * @param level        报警级别
     * @param lon          经度，单位为度(°)，乘以10^6 (uint32_t)
     * @param lat          纬度，单位为度(°)，乘以10^6 (uint32_t)
     * @param altitude     海拔高度，单位m (uint16_t)
     * @param vec1         行车速度，单位km/h (uint16_t)
     * @param vec2         行驶记录速度，单位km/h (uint16_t)
     * @param status       报警状态，1:报警开始; 2:报警结束
     * @param direction    方向，0°~359°，正北为0，顺时针 (uint16_t)
     * @param infoContent  上报报警信息内容 (变长，最长2048字节)
     * @return UpWarnMsgInfoEntity 实例
     */
    public static UpWarnMsgInfoEntity create(
            String vehicleNo, byte vehicleColor, byte warnSrc, int warnType, long warnTime,
            String infoId, String driver, String driverNo, byte level,
            long lon, long lat, int altitude, int vec1, int vec2,
            byte status, int direction, String infoContent) {

        UpWarnMsgInfoEntity entity = new UpWarnMsgInfoEntity();
        entity.vehicleNo = vehicleNo;
        entity.vehicleColor = vehicleColor;
        entity.warnSrc = warnSrc;
        entity.warnType = warnType;
        entity.warnTime = warnTime;
        entity.infoId = infoId;
        entity.driver = driver;
        entity.driverNo = driverNo;
        entity.level = level;
        entity.lon = lon;
        entity.lat = lat;
        entity.altitude = altitude;
        entity.vec1 = vec1;
        entity.vec2 = vec2;
        entity.status = status;
        entity.direction = direction;
        entity.infoContent = infoContent;

        byte[] infoIdBytes = copyBytes(infoId, 32);

        byte[] driverBytes = (driver != null) ? driver.getBytes(Charset.forName("GBK")) : new byte[0];
        entity.driverLength = (short) driverBytes.length;

        byte[] driverNoBytes = (driverNo != null) ? driverNo.getBytes(Charset.forName("GBK")) : new byte[0];
        entity.driverNoLength = (short) driverNoBytes.length;

        byte[] contentBytes = (infoContent != null) ? infoContent.getBytes(Charset.forName("GBK")) : new byte[0];
        entity.infoLength = contentBytes.length;

        byte[] data = ByteUtil.combineByteArray(
            new byte[]{warnSrc},
            ByteUtil.short2Bytes((short) warnType),
            ByteUtil.long2Bytes(warnTime),
            infoIdBytes,
            new byte[]{(byte) entity.driverLength},
            driverBytes,
            new byte[]{(byte) entity.driverNoLength},
            driverNoBytes,
            new byte[]{level},
            ByteUtil.int2Byte((int) lon),
            ByteUtil.int2Byte((int) lat),
            ByteUtil.short2Bytes((short) altitude),
            ByteUtil.short2Bytes((short) vec1),
            ByteUtil.short2Bytes((short) vec2),
            new byte[]{status},
            ByteUtil.short2Bytes((short) direction),
            ByteUtil.short2Bytes((short) entity.infoLength),
            contentBytes
        );

        entity.baseMsg = BaseWarnMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_WARN_MSG,
            vehicleNo,
            vehicleColor,
            MsgConstant.SubServiceType.UP_WARN_MSG_INFO,
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
