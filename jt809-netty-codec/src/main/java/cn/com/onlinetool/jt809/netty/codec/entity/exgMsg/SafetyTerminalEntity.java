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
 * 业务类型标识: UP_EXG_MSG_SAFETY_TERMINAL (0x1240)
 * 描述: 主动上传车辆智能监控终端安装信息消息 (依据 DB32/T 3610.3—2025 6.2.1.1 表42)
 *
 * 智能监控系统平台收到车辆安全智能防控终端安装信息，并确认符合要求后，
 * 向上级平台上传该车辆安全智能防控终端安装信息。本条消息服务端无需应答。
 *
 * @author Song Yifei
 */
@Setter
@Getter
@NoArgsConstructor
public class SafetyTerminalEntity implements Codec<SafetyTerminalEntity> {

    private BaseExgMsgEntity baseMsg;

    @Field(length = 11, desc = "车辆所属运营商平台唯一编码(部平台过检编号)")
    private String platformId; // byte[11]

    @Field(length = 50, desc = "设备厂商名称")
    private String producer; // byte[50]

    @Field(length = 30, desc = "设备型号，不足30位时以\"\\0\"终结")
    private String terminalModel; // byte[30]

    @Field(length = 20, desc = "设备编号")
    private String terminalId; // byte[20]

    @Field(length = 8, desc = "安装时间，用UTC时间表示(time_t)")
    private long installTime; // byte[8]

    @Field(length = 50, desc = "安装单位")
    private String installCompany; // byte[50]

    @Field(length = 20, desc = "联系人")
    private String contacts; // byte[20]

    @Field(length = 20, desc = "联系电话")
    private String telephone; // byte[20]

    @Field(length = 1, desc = "是否符合要求: 0.否; 1.是")
    private byte complianceRequirements; // byte[1]

    @Override
    public SafetyTerminalEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);
        ByteBuf data = baseMsg.getData();
        if (data != null && data.readableBytes() >= 210) {
            this.platformId = cleanString(new String(BufUtil.bufToBytes(data.slice(0, 11)), Charset.forName("GBK")));
            this.producer = cleanString(new String(BufUtil.bufToBytes(data.slice(11, 50)), Charset.forName("GBK")));
            this.terminalModel = cleanString(new String(BufUtil.bufToBytes(data.slice(61, 30)), Charset.forName("GBK")));
            this.terminalId = cleanString(new String(BufUtil.bufToBytes(data.slice(91, 20)), Charset.forName("GBK")));
            this.installTime = BufUtil.buf2SignedLong(data.slice(111, 8));
            this.installCompany = cleanString(new String(BufUtil.bufToBytes(data.slice(119, 50)), Charset.forName("GBK")));
            this.contacts = cleanString(new String(BufUtil.bufToBytes(data.slice(169, 20)), Charset.forName("GBK")));
            this.telephone = cleanString(new String(BufUtil.bufToBytes(data.slice(189, 20)), Charset.forName("GBK")));
            this.complianceRequirements = data.getByte(209);
        }
        return this;
    }

    public static SafetyTerminalEntity create(String vehicleNo, byte vehicleColor, String platformId, String producer,
                                              String terminalModel, String terminalId, long installTime,
                                              String installCompany, String contacts, String telephone, byte complianceRequirements) {
        SafetyTerminalEntity entity = new SafetyTerminalEntity();
        entity.platformId = platformId;
        entity.producer = producer;
        entity.terminalModel = terminalModel;
        entity.terminalId = terminalId;
        entity.installTime = installTime;
        entity.installCompany = installCompany;
        entity.contacts = contacts;
        entity.telephone = telephone;
        entity.complianceRequirements = complianceRequirements;

        byte[] platformIdBytes = copyBytes(platformId, 11);
        byte[] producerBytes = copyBytes(producer, 50);
        byte[] terminalModelBytes = copyBytes(terminalModel, 30);
        byte[] terminalIdBytes = copyBytes(terminalId, 20);
        byte[] installTimeBytes = ByteUtil.long2Bytes(installTime);
        byte[] installCompanyBytes = copyBytes(installCompany, 50);
        byte[] contactsBytes = copyBytes(contacts, 20);
        byte[] telephoneBytes = copyBytes(telephone, 20);
        byte[] complianceBytes = new byte[]{complianceRequirements};

        byte[] dataBytes = ByteUtil.combineByteArray(
                platformIdBytes,
                producerBytes,
                terminalModelBytes,
                terminalIdBytes,
                installTimeBytes,
                installCompanyBytes,
                contactsBytes,
                telephoneBytes,
                complianceBytes
        );

        entity.baseMsg = BaseExgMsgEntity.create(
                MsgConstant.PrimaryServiceType.UP_EXG_MSG,
                vehicleNo,
                vehicleColor,
                MsgConstant.SubServiceType.UP_EXG_MSG_SAFETY_TERMINAL,
                dataBytes
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
