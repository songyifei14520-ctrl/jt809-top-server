package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 2019协议
 * 链路类型:从链路 车辆注册信息应答消息 子业务类型标识:DOWN_EXG_MSG_REGISTER_ACK
 * 描述:上级平台收到下级平台上报的车辆注册信息后，向下级平台下发车辆注册信息应答消息
 */
@Setter
@Getter
@NoArgsConstructor
public class RegisterAckEntity implements Codec<RegisterAckEntity> {
    private BaseExgMsgEntity baseMsg;

    private int dataType;// 车辆注册信息消息源子业务类型标识 byte[2]
    private long msgSn; // 车辆注册信息消息流水号 byte[4]
    private byte result; // 处理结果 byte[1] 0x00:完成注册，加注册 0x01:审核通过，完成注册 0x02:信息错误，未完成注册 0x03:审核设通过，未完成注册

    @Override
    public RegisterAckEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);

        ByteBuf data = baseMsg.getData();
        this.dataType = BufUtil.buf2UnsignedInt(data.slice(0, 2));
        this.msgSn = BufUtil.buf2UnsignedLong(data.slice(2, 4));
        this.result = data.getByte(6);

        return this;
    }

    public static RegisterAckEntity create(String vehicleNo, byte vehicleColor,int dataType, long msgSn, byte result) {
        RegisterAckEntity entity = new RegisterAckEntity();
        entity.msgSn = msgSn;
        entity.result = result;
        entity.dataType = dataType;

        byte[] dataTypeBytes = ByteUtil.short2Bytes((short)dataType);
        byte[] msgSnBytes = ByteUtil.int2Byte((int)msgSn);
        byte[] resultBytes = new byte[]{result};

        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
                vehicleNo, vehicleColor,
                MsgConstant.SubServiceType.DOWN_EXG_MSG_REGISTER_ACK, ByteUtil.combineByteArray(dataTypeBytes,msgSnBytes, resultBytes));
        return entity;
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}
