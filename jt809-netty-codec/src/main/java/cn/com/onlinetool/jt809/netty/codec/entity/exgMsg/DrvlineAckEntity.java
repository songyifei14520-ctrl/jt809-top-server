package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:从链路 车辆行驶线路请求应答 子业务类型标识:DOWN_BASE_MSG_DRVLINE_ACK
 * 描述:上级平台向下级平台下发车辆行驶线路请求应答消息
 */
@Setter
@Getter
@NoArgsConstructor
public class DrvlineAckEntity implements Codec<DrvlineAckEntity> {
    private BaseExgMsgEntity baseMsg;
    private long drvLineId; // 线路ID uint32_t  4
    private byte result; // 处理结果 BYTE 1
    private int reasonLength; // 未通过原因内容长度 uint16_t 2
    private String reason; // 未通过原因 GBK编码

    @Override
    public DrvlineAckEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);
        ByteBuf data = baseMsg.getData();
        this.drvLineId = data.getUnsignedInt(0);
        this.result = data.getByte(4);
        this.reasonLength = data.getUnsignedShort(5);
        if (reasonLength > 0 && data.readableBytes() >= 7 + reasonLength) {
            this.reason = new String(BufUtil.bufToBytes(data.slice(7, reasonLength)), Charset.forName("GBK"));
        } else {
            this.reason = "";
        }
        return this;
    }

    public static DrvlineAckEntity create(String vehicleNo, byte vehicleColor, long drvLineId, byte result, String reason) {
        DrvlineAckEntity entity = new DrvlineAckEntity();
        entity.drvLineId = drvLineId;
        entity.result = result;
        entity.reason = reason == null ? "" : reason;
        byte[] reasonBytes = entity.reason.getBytes(Charset.forName("GBK"));
        entity.reasonLength = reasonBytes.length;
        byte[] data = new byte[4 + 1 + 2 + entity.reasonLength];
        // drvLineId
        System.arraycopy(ByteUtil.long2Bytes(drvLineId), 0, data, 0, 4);
        // result
        data[4] = result;
        // reasonLength
        System.arraycopy(ByteUtil.int2Byte(entity.reasonLength), 0, data, 5, 2);
        // reason
        if (entity.reasonLength > 0) {
            System.arraycopy(reasonBytes, 0, data, 7, entity.reasonLength);
        }
        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
                MsgConstant.SubServiceType.DOWN_BASE_MSG_DRVLINE_ACK, data);
        return entity;
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}
