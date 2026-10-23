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
 * 协议版本 2019
 * 子业务类型标识:UP_BASE_MSG_DRVLINE_INFO/DOWN_EXG_MSG_REPORT_DRIVER_INFO
 * 描述：在首次启动跨域车辆定位信息交换，或者以后交换过程中车辆行驶线路信息有更新时，
 * 由上级平台向跨域的下级平台下发一次车辆行驶线路信息。下级平台接收后自行更新该车辆行驶线路信息。本条信息客户端无需应答
 */
@Setter
@Getter
@NoArgsConstructor
public class DrvlineInfoEntity implements Codec<DrvlineInfoEntity> {
    private BaseExgMsgEntity baseMsg;
    private byte[] drvLine; // 处理结果 BYTE

    @Override
    public DrvlineInfoEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);
        this.drvLine = BufUtil.bufToBytes(baseMsg.getData());

        return this;
    }

    public static DrvlineInfoEntity create(String vehicleNo, byte vehicleColor, byte[] drvLine) {
        DrvlineInfoEntity entity = new DrvlineInfoEntity();
        entity.drvLine = drvLine;

        entity.baseMsg = BaseExgMsgEntity.create(MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor, MsgConstant.SubServiceType.UP_BASE_MSG_DRVLINE_INFO,
            entity.drvLine);

        return entity;
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}
