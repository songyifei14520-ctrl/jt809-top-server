package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

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
 * 子业务类型标识: UP_EXG_MSG_REPORT_DRIVER_INFO (0x120C)
 * 描述: 下级平台在接收到车载终端上传的驾驶员身份信息后, 主动向上级平台上报该信息。
 * 依据 JT/T 809-2019 及 DB32/T 3610.3—2025 6.2.1.3 表44
 */
@Setter
@Getter
@NoArgsConstructor
public class AutoReportDriverInfoEntity implements Codec<AutoReportDriverInfoEntity> {

    // ========== 驾驶员人脸识别事件常量 (DB32/T 3610.3—2025 表44) ==========
    /** 0x00: 代表未进行人脸识别 */
    public static final byte DRIVER_FACE_NONE = 0x00;
    /** 0x01: 代表人脸识别通过 */
    public static final byte DRIVER_FACE_PASS = 0x01;
    /** 0x02: 代表人脸识别未通过 */
    public static final byte DRIVER_FACE_FAIL = 0x02;

    private BaseExgMsgEntity baseMsg;

    @Field(length = 16, desc = "驾驶员姓名")
    private String driverName; // 驾驶员姓名 byte[16]

    @Field(length = 20, desc = "驾驶证编号")
    private String driverId; // 驾驶证编号 byte[20]

    @Field(length = 20, desc = "从业资格证号")
    private String licence; // 从业资格证号 byte[20]

    @Field(length = 200, desc = "发证机构名称")
    private String orgName; // 发证机构名称 byte[200]

    @Field(length = 8, desc = "证件有效期，时分秒均用0表示")
    private long validDate; // 证件有效期时分秒均用0表示 byte[8]

    @Field(length = 1, desc = "驾驶员人脸识别事件: 0x00.未进行人脸识别 0x01.人脸识别通过 0x02.人脸识别未通过", version = {2025})
    private Byte driverFace; // 驾驶员人脸识别事件 byte[1]

    @Override
    public AutoReportDriverInfoEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);
        ByteBuf data = baseMsg.getData();
        if (data != null && data.readableBytes() >= 264) {
            // DRIVER_NAME 16
            this.driverName = cleanString(new String(BufUtil.bufToBytes(data.slice(0, 16)), Charset.forName("GBK")));
            // DRIVER_ID 20
            this.driverId = cleanString(new String(BufUtil.bufToBytes(data.slice(16, 20)), Charset.forName("GBK")));
            // LICENCE 20
            this.licence = cleanString(new String(BufUtil.bufToBytes(data.slice(36, 20)), Charset.forName("GBK")));
            // ORG_NAME 200
            this.orgName = cleanString(new String(BufUtil.bufToBytes(data.slice(56, 200)), Charset.forName("GBK")));
            // VALID_DATE 8
            this.validDate = BufUtil.buf2SignedLong(data.slice(256, 8));

            // 苏标2025扩展: 驾驶员人脸识别事件 1字节 (总长度为265字节)
            if (data.readableBytes() >= 265) {
                this.driverFace = data.getByte(264);
            }
        }
        return this;
    }

    /**
     * 苏标2025 (DB32/T 3610.3—2025 表44) 支持驾驶员人脸识别事件字段
     */
    public static AutoReportDriverInfoEntity create(String vehicleNo, byte vehicleColor, String driverName,
                                                   String driverId, String licence, String orgName, long validDate, byte driverFace) {
        AutoReportDriverInfoEntity entity = new AutoReportDriverInfoEntity();
        entity.driverName = driverName;
        entity.driverId = driverId;
        entity.licence = licence;
        entity.orgName = orgName;
        entity.validDate = validDate;
        entity.driverFace = driverFace;

        byte[] driverNameBytes = copyBytes(driverName, 16);
        byte[] driverIdBytes = copyBytes(driverId, 20);
        byte[] licenceBytes = copyBytes(licence, 20);
        byte[] orgNameBytes = copyBytes(orgName, 200);

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO,
            ByteUtil.combineByteArray(
                driverNameBytes,
                driverIdBytes,
                licenceBytes,
                orgNameBytes,
                ByteUtil.long2Bytes(validDate),
                new byte[]{driverFace}
            )
        );
        return entity;
    }

    /**
     * JT/T 809-2019 标准格式创建方法
     */
    public static AutoReportDriverInfoEntity create(String vehicleNo, byte vehicleColor, String driverName,
                                                   String driverId, String licence, String orgName, long validDate) {
        AutoReportDriverInfoEntity entity = new AutoReportDriverInfoEntity();
        entity.driverName = driverName;
        entity.driverId = driverId;
        entity.licence = licence;
        entity.orgName = orgName;
        entity.validDate = validDate;

        byte[] driverNameBytes = copyBytes(driverName, 16);
        byte[] driverIdBytes = copyBytes(driverId, 20);
        byte[] licenceBytes = copyBytes(licence, 20);
        byte[] orgNameBytes = copyBytes(orgName, 200);

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
            MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_DRIVER_INFO,
            ByteUtil.combineByteArray(
                driverNameBytes,
                driverIdBytes,
                licenceBytes,
                orgNameBytes,
                ByteUtil.long2Bytes(validDate)
            )
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
