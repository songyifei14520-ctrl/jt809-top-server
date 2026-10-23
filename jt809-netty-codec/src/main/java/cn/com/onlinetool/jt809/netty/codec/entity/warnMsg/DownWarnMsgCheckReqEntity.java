package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 链路类型: 从链路
 * 消息方向: 上级平台向下级平台
 * 业务类型标识: DOWN_WARN_MSG (0x9400)
 * 子业务类型标识: DOWN_WARN_MSG_CHECK_REQ (0x9405)
 * 描述: 上级平台向下级平台发送智能监控报警核查请求业务, 其数据体定义见表 52。
 * 依据: DB32/T 3610.3—2025 6.2.2.6 表 52 报警信息核查请求消息数据体 及 表 60 车辆报警类型编码表
 */
@Setter
@Getter
@NoArgsConstructor
public class DownWarnMsgCheckReqEntity implements Codec<DownWarnMsgCheckReqEntity> {

    // ========== 车牌颜色常量 (DB32/T 3610.3—2025 表52) ==========
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

    // ========== 表 60 车辆报警类型编码 ==========
    /** 0x0001: 超速报警 */
    public static final int WARN_TYPE_OVERSPEED = 0x0001;
    /** 0x0002: 连续驾驶超时疲劳报警 */
    public static final int WARN_TYPE_FATIGUE = 0x0002;
    /** 0x0003: 紧急报警 */
    public static final int WARN_TYPE_EMERGENCY = 0x0003;
    /** 0x0004: 进入指定区域报警 */
    public static final int WARN_TYPE_IN_AREA = 0x0004;
    /** 0x0005: 离开指定区域报警 */
    public static final int WARN_TYPE_OUT_AREA = 0x0005;
    /** 0x0006: 路段堵塞报警 */
    public static final int WARN_TYPE_ROAD_JAM = 0x0006;
    /** 0x0007: 危险路段报警 */
    public static final int WARN_TYPE_DANGEROUS_ROAD = 0x0007;
    /** 0x0008: 越界报警 */
    public static final int WARN_TYPE_ACROSS_BORDER = 0x0008;
    /** 0x0009: 盗警 */
    public static final int WARN_TYPE_THEFT = 0x0009;

    /** 0x0218: 驾驶员身份异常报警 */
    public static final int WARN_TYPE_DRIVER_IDENTITY_ABNORMAL = 0x0218;
    /** 0x0219: 驾驶员变更报警 */
    public static final int WARN_TYPE_DRIVER_CHANGE = 0x0219;
    /** 0x0221: 胎压过高报警 */
    public static final int WARN_TYPE_TIRE_PRESSURE_HIGH = 0x0221;
    /** 0x0222: 胎压过低报警 */
    public static final int WARN_TYPE_TIRE_PRESSURE_LOW = 0x0222;
    /** 0x0223: 轮胎温度过高报警 */
    public static final int WARN_TYPE_TIRE_TEMP_HIGH = 0x0223;
    /** 0x0224: 传感器异常报警 */
    public static final int WARN_TYPE_SENSOR_ABNORMAL = 0x0224;
    /** 0x0225: 胎压不平衡报警 */
    public static final int WARN_TYPE_TIRE_PRESSURE_UNBALANCE = 0x0225;
    /** 0x0226: 慢漏气报警 */
    public static final int WARN_TYPE_SLOW_LEAKAGE = 0x0226;
    /** 0x0227: 电池电压低报警 */
    public static final int WARN_TYPE_BATTERY_LOW = 0x0227;

    private BaseWarnMsgEntity baseMsg;

    // ========== 表 52 字段 ==========
    @Field(length = 21, desc = "车牌号码", version = {2025})
    private String vehicleNo; // 车牌号码 byte[21]

    @Field(length = 1, desc = "车牌颜色", version = {2025})
    private byte vehicleColor; // 车牌颜色 byte[1]

    // ========== 表 52 数据部分字段 (固定 10 字节) ==========
    @Field(length = 2, desc = "报警类型(表60)", version = {2025})
    private int warnType; // 报警类型 uint16_t 2字节

    @Field(length = 8, desc = "报警时间(UTC时间格式)", version = {2025})
    private long warnTime; // 报警时间, UTC时间格式 time_t 8字节

    @Override
    public DownWarnMsgCheckReqEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseWarnMsgEntity().decodeWithVehicle(buf);
        this.vehicleNo = cleanString(this.baseMsg.getVehicleNo());
        this.vehicleColor = this.baseMsg.getVehicleColor();

        ByteBuf data = this.baseMsg.getData();
        if (data != null && data.readableBytes() >= 10) {
            this.warnType = data.readUnsignedShort();
            this.warnTime = data.readLong();
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表52 报警信息核查请求消息构造方法
     *
     * @param vehicleNo    车牌号码 (21字节)
     * @param vehicleColor 车牌颜色 (0x01:蓝色, 0x02:黄色, 0x03:黑色, 0x04:白色, 0x05:绿色, 0x06:黄绿色, 0x07:渐变绿, 0x09:其他)
     * @param warnType     报警类型，见表 60 (uint16_t 2字节)
     * @param warnTime     报警时间，UTC时间格式 (time_t 8字节)
     * @return DownWarnMsgCheckReqEntity 实例
     */
    public static DownWarnMsgCheckReqEntity create(String vehicleNo, byte vehicleColor, int warnType, long warnTime) {
        DownWarnMsgCheckReqEntity entity = new DownWarnMsgCheckReqEntity();
        entity.vehicleNo = vehicleNo;
        entity.vehicleColor = vehicleColor;
        entity.warnType = warnType;
        entity.warnTime = warnTime;

        byte[] data = ByteUtil.combineByteArray(
                ByteUtil.short2Bytes((short) warnType),
                ByteUtil.long2Bytes(warnTime)
        );

        entity.baseMsg = BaseWarnMsgEntity.create(
                MsgConstant.PrimaryServiceType.DOWN_WARN_MSG,
                vehicleNo,
                vehicleColor,
                MsgConstant.SubServiceType.DOWN_WARN_MSG_CHECK_REQ,
                data
        );
        return entity;
    }

    /**
     * 获取报警类型描述名称
     *
     * @param warnType 报警类型编码
     * @return 报警名称
     */
    public static String getWarnTypeName(int warnType) {
        switch (warnType) {
            case WARN_TYPE_OVERSPEED:
                return "超速报警";
            case WARN_TYPE_FATIGUE:
                return "连续驾驶超时疲劳报警";
            case WARN_TYPE_EMERGENCY:
                return "紧急报警";
            case WARN_TYPE_IN_AREA:
                return "进入指定区域报警";
            case WARN_TYPE_OUT_AREA:
                return "离开指定区域报警";
            case WARN_TYPE_ROAD_JAM:
                return "路段堵塞报警";
            case WARN_TYPE_DANGEROUS_ROAD:
                return "危险路段报警";
            case WARN_TYPE_ACROSS_BORDER:
                return "越界报警";
            case WARN_TYPE_THEFT:
                return "盗警";
            case WARN_TYPE_DRIVER_IDENTITY_ABNORMAL:
                return "驾驶员身份异常报警";
            case WARN_TYPE_DRIVER_CHANGE:
                return "驾驶员变更报警";
            case WARN_TYPE_TIRE_PRESSURE_HIGH:
                return "胎压过高报警";
            case WARN_TYPE_TIRE_PRESSURE_LOW:
                return "胎压过低报警";
            case WARN_TYPE_TIRE_TEMP_HIGH:
                return "轮胎温度过高报警";
            case WARN_TYPE_SENSOR_ABNORMAL:
                return "传感器异常报警";
            case WARN_TYPE_TIRE_PRESSURE_UNBALANCE:
                return "胎压不平衡报警";
            case WARN_TYPE_SLOW_LEAKAGE:
                return "慢漏气报警";
            case WARN_TYPE_BATTERY_LOW:
                return "电池电压低报警";
            default:
                return "未知报警类型(" + String.format("0x%04X", warnType) + ")";
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
