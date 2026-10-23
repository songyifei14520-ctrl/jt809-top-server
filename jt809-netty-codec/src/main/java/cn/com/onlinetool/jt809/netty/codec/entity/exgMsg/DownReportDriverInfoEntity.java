package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;

/**
 * 链路类型: 从链路
 * 消息方向: 上级平台向下级平台
 * 子业务类型标识: DOWN_EXG_MSG_REPORT_DRIVER_INFO (0x920A)
 * 描述: 上级平台向下级平台下发上报车辆驾驶员身份信息的请求消息
 * (兼容 JT/T 809-2011、JT/T 809-2019 以及 DB32/T 3610.3—2025 6.2.1.4 表45)
 */
@Setter
@Getter
@NoArgsConstructor
public class DownReportDriverInfoEntity implements Codec<DownReportDriverInfoEntity> {

    // ========== 上传标志常量 (DB32/T 3610.3—2025 表45 / JT/T 809-2019) ==========
    /** 0x00: 最近收到的消息 */
    public static final byte FLAG_RECENT = 0x00;
    /** 0x01: 从终端获取 */
    public static final byte FLAG_FROM_TERMINAL = 0x01;

    private BaseExgMsgEntity baseMsg;

    /**
     * 上传标志，定义如下：
     * 0x00：最近收到的消息
     * 0x01：从终端获取
     */
    @Field(length = 1, desc = "上传标志", version = {2019, 2025})
    private byte flag; // 上传标志 byte[1] - 适用于 JT/T 809-2019 及 DB32/T 3610.3—2025

    @Override
    public DownReportDriverInfoEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);
        ByteBuf data = baseMsg.getData();

        // 2019/2025版本协议后续数据长度为1（包含flag字段），2011版本后续数据长度为0
        if (data != null && data.readableBytes() >= 1) {
            this.flag = data.getByte(0);
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 (表45) 及 JT/T 809-2019 构造方法（包含flag上传标志）
     *
     * @param vehicleNo    车牌号
     * @param vehicleColor 车牌颜色，按照 JT/T 697.7—2022 中的规定
     * @param flag         上传标志：0x00 最近收到的消息，0x01 从终端获取
     * @return DownReportDriverInfoEntity
     */
    public static DownReportDriverInfoEntity create(String vehicleNo, byte vehicleColor, byte flag) {
        DownReportDriverInfoEntity entity = new DownReportDriverInfoEntity();
        entity.flag = flag;

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.DOWN_EXG_MSG_REPORT_DRIVER_INFO,
            new byte[]{flag});
        return entity;
    }

    /**
     * JT/T 809-2011 版本协议构造方法（不包含flag字段，后续数据长度为0）
     *
     * @param vehicleNo    车牌号
     * @param vehicleColor 车牌颜色
     * @return DownReportDriverInfoEntity
     */
    public static DownReportDriverInfoEntity create(String vehicleNo, byte vehicleColor) {
        DownReportDriverInfoEntity entity = new DownReportDriverInfoEntity();

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.DOWN_EXG_MSG_REPORT_DRIVER_INFO,
            new byte[0]);
        return entity;
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}

