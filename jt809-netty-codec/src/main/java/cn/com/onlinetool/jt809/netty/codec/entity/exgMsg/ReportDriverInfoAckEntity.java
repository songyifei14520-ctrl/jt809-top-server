package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 上报驾驶员身份识别信息应答消息 子业务类型标识:UP_EXG_MSG_REPORT_DRIVER_INFO_ACK (0x120A)
 * 描述:下级平台应答上级平台发送的上报驾驶员身份识别信息请求消息，上传指定车辆的驾驶员身份识别信息数据。
 * (兼容 JT/T 809-2011、JT/T 809-2019 以及 DB32/T 3610.3—2025 6.2.1.2 表43)
 */
@Setter
@Getter
@NoArgsConstructor
public class ReportDriverInfoAckEntity implements Codec<ReportDriverInfoAckEntity> {

    // ========== 驾驶员人脸识别事件常量 (DB32/T 3610.3—2025 表43) ==========
    /** 0x00: 代表未进行人脸识别 */
    public static final byte DRIVER_FACE_NONE = 0x00;
    /** 0x01: 代表人脸识别通过 */
    public static final byte DRIVER_FACE_PASS = 0x01;
    /** 0x02: 代表人脸识别未通过 */
    public static final byte DRIVER_FACE_FAIL = 0x02;

    private BaseExgMsgEntity baseMsg;

    @Field(length = 2, desc = "数据来源类型", version = {2019, 2025})
    private int sourceDateType; // 数据来源类型 uint16_t 2字节
    @Field(length = 4, desc = "数据来源消息流水号", version = {2019, 2025})
    private long sourceMsgSn; // 数据来源消息流水号 uint32_t 4字节
    @Field(length = 16, desc = "驾驶员姓名")
    private String driverName; // 驾驶员姓名 byte[16]
    @Field(length = 20, desc = "驾驶证编号")
    private String driverId; // 驾驶证编号 byte[20]
    @Field(length = 40, desc = "从业资格证（备用）")
    private String licence; // 从业资格证（备用） byte[40]
    @Field(length = 200, desc = "发证机构名称（备用）")
    private String orgName; // 发证机构名称（备用） byte[200]
    @Field(length = 8, desc = "证件有效期", version = {2019, 2025})
    private long valId_date; // 证件有效期时分秒均用0表示 time_t 8字节

    @Field(length = 1, desc = "驾驶员人脸识别事件: 0x00.未进行人脸识别 0x01.人脸识别通过 0x02.人脸识别未通过", version = {2025})
    private Byte driverFace; // 驾驶员人脸识别事件 byte[1]

    @Override
    public ReportDriverInfoAckEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);

        // 2019/2025版本协议 (2019协议头长度为 32 字节)
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            if (data != null && data.readableBytes() >= 290) { // 2+4+16+20+40+200+8 = 290字节
                this.sourceDateType = BufUtil.buf2UnsignedInt(data.slice(0, 2));
                this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2, 4));
                this.driverName = cleanString(new String(BufUtil.bufToBytes(data.slice(6, 16)), Charset.forName("GBK")));
                this.driverId = cleanString(new String(BufUtil.bufToBytes(data.slice(22, 20)), Charset.forName("GBK")));
                this.licence = cleanString(new String(BufUtil.bufToBytes(data.slice(42, 40)), Charset.forName("GBK")));
                this.orgName = cleanString(new String(BufUtil.bufToBytes(data.slice(82, 200)), Charset.forName("GBK")));
                this.valId_date = BufUtil.buf2SignedLong(data.slice(282, 8));

                // 苏标2025扩展: 驾驶员人脸识别事件 1字节 (总长度为291字节)
                if (data.readableBytes() >= 291) {
                    this.driverFace = data.getByte(290);
                }
            }
            return this;
        }

        // 2011版本协议
        ByteBuf data = baseMsg.getData();
        if (data != null && data.readableBytes() >= 276) {
            this.driverName = cleanString(new String(BufUtil.bufToBytes(data.slice(0, 16)), Charset.forName("GBK")));
            this.driverId = cleanString(new String(BufUtil.bufToBytes(data.slice(16, 20)), Charset.forName("GBK")));
            this.licence = cleanString(new String(BufUtil.bufToBytes(data.slice(36, 40)), Charset.forName("GBK")));
            this.orgName = cleanString(new String(BufUtil.bufToBytes(data.slice(76, 200)), Charset.forName("GBK")));
        }
        return this;
    }

    /**
     * 苏标2025 (DB32/T 3610.3—2025 表43) 支持驾驶员人脸识别事件字段
     */
    public static ReportDriverInfoAckEntity create(String vehicleNo, byte vehicleColor, int sourceDateType, long sourceMsgSn,
            String driverName, String driverId, String licence, String orgName, long valId_date, byte driverFace) {
        ReportDriverInfoAckEntity entity = new ReportDriverInfoAckEntity();
        entity.sourceDateType = sourceDateType;
        entity.sourceMsgSn = sourceMsgSn;
        entity.driverName = driverName;
        entity.driverId = driverId;
        entity.licence = licence;
        entity.orgName = orgName;
        entity.valId_date = valId_date;
        entity.driverFace = driverFace;

        byte[] driverNameBytes = copyBytes(driverName, 16);
        byte[] driverIdBytes = copyBytes(driverId, 20);
        byte[] licenceBytes = copyBytes(licence, 40);
        byte[] orgNameBytes = copyBytes(orgName, 200);

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO_ACK,
            ByteUtil.combineByteArray(
                ByteUtil.short2Bytes((short) sourceDateType),
                ByteUtil.int2Byte((int) sourceMsgSn),
                driverNameBytes,
                driverIdBytes,
                licenceBytes,
                orgNameBytes,
                ByteUtil.long2Bytes(valId_date),
                new byte[]{driverFace}
            )
        );
        return entity;
    }

    /**
     * JT/T 809-2019 标准格式创建方法
     */
    public static ReportDriverInfoAckEntity create(String vehicleNo, byte vehicleColor, int sourceDateType, long sourceMsgSn,
            String driverName, String driverId, String licence, String orgName, long valId_date) {
        ReportDriverInfoAckEntity entity = new ReportDriverInfoAckEntity();
        entity.sourceDateType = sourceDateType;
        entity.sourceMsgSn = sourceMsgSn;
        entity.driverName = driverName;
        entity.driverId = driverId;
        entity.licence = licence;
        entity.orgName = orgName;
        entity.valId_date = valId_date;

        byte[] driverNameBytes = copyBytes(driverName, 16);
        byte[] driverIdBytes = copyBytes(driverId, 20);
        byte[] licenceBytes = copyBytes(licence, 40);
        byte[] orgNameBytes = copyBytes(orgName, 200);

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO_ACK,
            ByteUtil.combineByteArray(
                ByteUtil.short2Bytes((short) sourceDateType),
                ByteUtil.int2Byte((int) sourceMsgSn),
                driverNameBytes,
                driverIdBytes,
                licenceBytes,
                orgNameBytes,
                ByteUtil.long2Bytes(valId_date)
            )
        );
        return entity;
    }

    /**
     * JT/T 809-2011 格式创建方法
     */
    public static ReportDriverInfoAckEntity create(String vehicleNo, byte vehicleColor, String driverName,
            String driverId, String licence, String orgName) {
        ReportDriverInfoAckEntity entity = new ReportDriverInfoAckEntity();
        entity.driverName = driverName;
        entity.driverId = driverId;
        entity.licence = licence;
        entity.orgName = orgName;

        byte[] driverNameBytes = copyBytes(driverName, 16);
        byte[] driverIdBytes = copyBytes(driverId, 20);
        byte[] licenceBytes = copyBytes(licence, 40);
        byte[] orgNameBytes = copyBytes(orgName, 200);

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO_ACK,
            ByteUtil.combineByteArray(driverNameBytes, driverIdBytes, licenceBytes, orgNameBytes)
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
