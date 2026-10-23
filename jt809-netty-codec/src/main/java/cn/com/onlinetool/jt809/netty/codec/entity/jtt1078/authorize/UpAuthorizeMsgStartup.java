package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.authorize;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

/**
 * 时效口令上报消息
 * <p>
 * 链路类型：主链路
 * 消息方向：下级平台向上级平台
 * 子业务类型标识：UP AUTHORIZE MSG STARTUP。
 * 描述：企业视频监控平台向政府视频监管平台或下级政府视频监管平台向上级政府视频监管平台主动上报时效口令，数据体见表 39。 该指令无须应答。
 *
 * @author syz
 * @since 2025/10/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpAuthorizeMsgStartup extends BaseEntity {
    private int dataType;
    /**
     * 企业视频监控平台唯一编码，平台所属企业行政区划代码 + 平台公告编号
     */
    private String plateFormId;
    /**
     * 归属地区政府平台使用的时效口令
     */
    private String authorizeCode1;
    /**
     * 跨域地区政府平台使用的时效口令
     */
    private String authorizeCode2;

    @Override
    public UpAuthorizeMsgStartup decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf messageBody = this.getMessageBody();
        this.dataType = BufUtil.buf2UnsignedInt(messageBody.readBytes(2));
        this.plateFormId = new String(BufUtil.bufToBytes(messageBody.readBytes(11)));
        this.authorizeCode1 = new String(BufUtil.bufToBytes(messageBody.readBytes(64)));
        this.authorizeCode2 = new String(BufUtil.bufToBytes(messageBody.readBytes(64)));
        return this;
    }

    public byte[] encode() {
        byte[] msgBody = new byte[2 + 11 + 64 + 64];
        System.arraycopy(ByteUtil.short2Bytes((short) this.dataType), 0, msgBody, 0, 2);
        byte[] plateFormIdBytes = plateFormId.getBytes();
        System.arraycopy(plateFormIdBytes, 0, msgBody, 2, 11);
        byte[] authorizeCode1Bytes = authorizeCode1.getBytes();
        System.arraycopy(authorizeCode1Bytes, 0, msgBody, 13, 64);
        byte[] authorizeCode2Bytes = authorizeCode2.getBytes();
        System.arraycopy(authorizeCode2Bytes, 0, msgBody, 77, 64);
        BaseEntity baseEntity = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_AUTHORIZE_MSG, msgBody);
        baseEntity.setMessageBody(BufUtil.bytesToBuf(msgBody));
        return baseEntity.encode();
    }

    public static void main(String[] args) {
        UpAuthorizeMsgStartup upAuthorizeMsgStartup = new UpAuthorizeMsgStartup();
        upAuthorizeMsgStartup.setDataType(MsgConstant.SubServiceType.UP_AUTHORIZE_MSG_STARTUP);
        upAuthorizeMsgStartup.setPlateFormId("JC2025PT047");
        upAuthorizeMsgStartup.setAuthorizeCode1("5r99JeevbiNDc2eSyUXeA5DGGrKyj7lCo4UmGKNwUtWVKWX2t7h2vUI9DlMfjjpW");
        upAuthorizeMsgStartup.setAuthorizeCode2("DmSwTSJGil2cYaYO32UIgGzotcDIQgMMCmPpCt6AjenilLd6xTWS1onjg43vxNpt");
        byte[] encode = upAuthorizeMsgStartup.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        UpAuthorizeMsgStartup decode = new UpAuthorizeMsgStartup().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);
    }
}
