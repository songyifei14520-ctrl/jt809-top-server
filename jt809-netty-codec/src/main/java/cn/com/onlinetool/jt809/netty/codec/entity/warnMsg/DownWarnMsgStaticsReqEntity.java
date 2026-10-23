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
 * 子业务类型标识: DOWN_WARN_MSG_STATICS_REQ (0x9406)
 * 描述: 上级平台向下级平台发送智能监控报警统计核查请求业务, 其数据体定义见表 54。
 * 依据: DB32/T 3610.3—2025 6.2.3.3 表 54 报警统计核查请求消息数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class DownWarnMsgStaticsReqEntity implements Codec<DownWarnMsgStaticsReqEntity> {

    // ========== 车牌颜色常量 (DB32/T 3610.3—2025 表54) ==========
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

    private BaseWarnMsgEntity baseMsg;

    // ========== 表 54 字段 ==========
    @Field(length = 21, desc = "车牌号码", version = {2025})
    private String vehicleNo; // 车牌号码 byte[21]

    @Field(length = 1, desc = "车牌颜色", version = {2025})
    private byte vehicleColor; // 车牌颜色 byte[1]

    // ========== 表 54 数据部分字段 (固定 16 字节) ==========
    @Field(length = 8, desc = "统计开始时间,UTC时间格式", version = {2025})
    private long startTime; // 统计开始时间, UTC时间格式 time_t 8字节

    @Field(length = 8, desc = "统计结束时间,UTC时间格式", version = {2025})
    private long endTime; // 统计结束时间, UTC时间格式 time_t 8字节

    @Override
    public DownWarnMsgStaticsReqEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseWarnMsgEntity().decodeWithVehicle(buf);
        this.vehicleNo = cleanString(this.baseMsg.getVehicleNo());
        this.vehicleColor = this.baseMsg.getVehicleColor();

        ByteBuf data = this.baseMsg.getData();
        if (data != null && data.readableBytes() >= 16) {
            this.startTime = data.readLong();
            this.endTime = data.readLong();
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表54 报警统计核查请求消息构造方法
     *
     * @param vehicleNo    车牌号码 (21字节)
     * @param vehicleColor 车牌颜色 (0x01:蓝色, 0x02:黄色, 0x03:黑色, 0x04:白色, 0x05:绿色, 0x06:黄绿色, 0x07:渐变绿, 0x09:其他)
     * @param startTime    统计开始时间, UTC时间格式 (time_t 8字节)
     * @param endTime      统计结束时间, UTC时间格式 (time_t 8字节)
     * @return DownWarnMsgStaticsReqEntity 实例
     */
    public static DownWarnMsgStaticsReqEntity create(String vehicleNo, byte vehicleColor, long startTime, long endTime) {
        DownWarnMsgStaticsReqEntity entity = new DownWarnMsgStaticsReqEntity();
        entity.vehicleNo = vehicleNo;
        entity.vehicleColor = vehicleColor;
        entity.startTime = startTime;
        entity.endTime = endTime;

        byte[] data = ByteUtil.combineByteArray(
                ByteUtil.long2Bytes(startTime),
                ByteUtil.long2Bytes(endTime)
        );

        entity.baseMsg = BaseWarnMsgEntity.create(
                MsgConstant.PrimaryServiceType.DOWN_WARN_MSG,
                vehicleNo,
                vehicleColor,
                MsgConstant.SubServiceType.DOWN_WARN_MSG_STATICS_REQ,
                data
        );
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
