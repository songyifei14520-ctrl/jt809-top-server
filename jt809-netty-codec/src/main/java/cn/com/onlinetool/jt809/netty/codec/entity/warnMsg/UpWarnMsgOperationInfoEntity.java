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
 * 子业务类型标识: UP_WARN_MSG_OPERATION_INFO (0x1403)
 * 描述: 下级平台向主动向上级平台上报报警处理结果, 其数据体定义见表 47 及表 47（续）。本条消息上级平台无需应答。本条消息服务端无需应答。
 * 依据: DB32/T 3610.3—2025 6.2.2.2 表 47 与 表 47（续） 主动上报报警处理结果信息消息数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class UpWarnMsgOperationInfoEntity implements Codec<UpWarnMsgOperationInfoEntity> {

    // ========== 车牌颜色常量 (DB32/T 3610.3—2025 表47) ==========
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

    // ========== 报警处理结果常量 (DB32/T 3610.3—2025 表47续) ==========
    /** 0x00: 处理中 */
    public static final byte RESULT_PROCESSING = 0x00;
    /** 0x01: 已处理完毕 */
    public static final byte RESULT_COMPLETED = 0x01;

    // ========== 报警处理方式常量 (DB32/T 3610.3—2025 表47续) ==========
    /** 0x00: 快速拍照 */
    public static final byte METHOD_FAST_PHOTO = 0x00;
    /** 0x01: 语音下发 */
    public static final byte METHOD_VOICE_SEND = 0x01;
    /** 0x02: 不做处理 */
    public static final byte METHOD_NO_ACTION = 0x02;
    /** 0x03: 其他 */
    public static final byte METHOD_OTHER = 0x03;

    private BaseWarnMsgEntity baseMsg;

    // ========== 表 47 字段 ==========
    @Field(length = 21, desc = "车牌号码", version = {2025})
    private String vehicleNo; // 车牌号码 byte[21]

    @Field(length = 1, desc = "车牌颜色", version = {2025})
    private byte vehicleColor; // 车牌颜色 byte[1]

    // ========== 表 47（续）数据部分字段 ==========
    @Field(length = 32, desc = "报警信息ID", version = {2025})
    private String infoId; // 报警信息ID Octet String 32字节

    @Field(length = 1, desc = "报警处理结果: 0x00.处理中 0x01.已处理完毕", version = {2025})
    private byte result; // 报警处理结果 BYTE 1字节

    @Field(length = 1, desc = "报警处理方式: 0x00.快速拍照 0x01.语音下发 0x02.不做处理 0x03.其他", version = {2025})
    private byte method; // 报警处理方式 BYTE 1字节

    @Field(length = 1, desc = "报警处理人姓名长度", version = {2025})
    private short operatorLength; // 报警处理人姓名长度 BYTE 1字节

    @Field(desc = "报警处理人姓名", version = {2025})
    private String operator; // 报警处理人姓名 Octet String

    @Field(length = 1, desc = "报警处理人所属公司名称长度", version = {2025})
    private short companyLength; // 报警处理人所属公司名称长度 BYTE 1字节

    @Field(desc = "报警处理人所属公司名称", version = {2025})
    private String company; // 报警处理人所属公司名称 Octet String

    @Override
    public UpWarnMsgOperationInfoEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseWarnMsgEntity().decodeWithVehicle(buf);
        this.vehicleNo = cleanString(this.baseMsg.getVehicleNo());
        this.vehicleColor = this.baseMsg.getVehicleColor();

        ByteBuf data = this.baseMsg.getData();
        if (data != null && data.readableBytes() >= 34) { // 32(infoId) + 1(result) + 1(method) = 34
            byte[] infoIdBytes = new byte[32];
            data.readBytes(infoIdBytes);
            this.infoId = cleanString(new String(infoIdBytes, Charset.forName("GBK")));

            this.result = data.readByte();
            this.method = data.readByte();

            if (data.readableBytes() >= 1) {
                int opLen = data.readUnsignedByte();
                this.operatorLength = (short) opLen;
                if (opLen > 0 && data.readableBytes() >= opLen) {
                    byte[] opBytes = new byte[opLen];
                    data.readBytes(opBytes);
                    this.operator = cleanString(new String(opBytes, Charset.forName("GBK")));
                } else {
                    this.operator = "";
                }
            }

            if (data.readableBytes() >= 1) {
                int cpLen = data.readUnsignedByte();
                this.companyLength = (short) cpLen;
                if (cpLen > 0 && data.readableBytes() >= cpLen) {
                    byte[] cpBytes = new byte[cpLen];
                    data.readBytes(cpBytes);
                    this.company = cleanString(new String(cpBytes, Charset.forName("GBK")));
                } else {
                    this.company = "";
                }
            }
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表47 及 表47（续）完整主动上报报警处理结果信息消息构造方法
     *
     * @param vehicleNo    车牌号码 (21字节)
     * @param vehicleColor 车牌颜色 (0x01:蓝色, 0x02:黄色, 0x03:黑色, 0x04:白色, 0x05:绿色, 0x06:黄绿色, 0x07:渐变绿, 0x09:其他)
     * @param infoId       报警信息ID (32字节)
     * @param result       报警处理结果 (0x00:处理中, 0x01:已处理完毕)
     * @param method       报警处理方式 (0x00:快速拍照, 0x01:语音下发, 0x02:不做处理, 0x03:其他)
     * @param operator     报警处理人姓名
     * @param company      报警处理人所属公司名称
     * @return UpWarnMsgOperationInfoEntity 实例
     */
    public static UpWarnMsgOperationInfoEntity create(
            String vehicleNo, byte vehicleColor, String infoId,
            byte result, byte method, String operator, String company) {

        UpWarnMsgOperationInfoEntity entity = new UpWarnMsgOperationInfoEntity();
        entity.vehicleNo = vehicleNo;
        entity.vehicleColor = vehicleColor;
        entity.infoId = infoId;
        entity.result = result;
        entity.method = method;
        entity.operator = operator;
        entity.company = company;

        byte[] infoIdBytes = copyBytes(infoId, 32);

        byte[] opBytes = (operator != null) ? operator.getBytes(Charset.forName("GBK")) : new byte[0];
        entity.operatorLength = (short) opBytes.length;

        byte[] cpBytes = (company != null) ? company.getBytes(Charset.forName("GBK")) : new byte[0];
        entity.companyLength = (short) cpBytes.length;

        byte[] data = ByteUtil.combineByteArray(
            infoIdBytes,
            new byte[]{result, method},
            new byte[]{(byte) entity.operatorLength},
            opBytes,
            new byte[]{(byte) entity.companyLength},
            cpBytes
        );

        entity.baseMsg = BaseWarnMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_WARN_MSG,
            vehicleNo,
            vehicleColor,
            MsgConstant.SubServiceType.UP_WARN_MSG_OPERATION_INFO,
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
