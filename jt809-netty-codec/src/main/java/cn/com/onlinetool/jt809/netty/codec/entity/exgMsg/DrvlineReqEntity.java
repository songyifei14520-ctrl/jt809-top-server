package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import java.nio.charset.Charset;

/**
 * 链路类型:从链路 上报车辆行驶路线请求 子业务类型标识:DOWN_BASE_MSG_DRVLINE_REQ
 * 描述:上级平台向下级平台下发上报车辆行驶路线请求消息
 */
@Setter
@Getter
@NoArgsConstructor
public class DrvlineReqEntity implements Codec<DrvlineReqEntity> {
    private BaseExgMsgEntity baseMsg;

    @Override
    public DrvlineReqEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseExgMsgEntity().decode(buf);
        return this;
    }

    public static DrvlineReqEntity create(String vehicleNo, byte vehicleColor) {
        DrvlineReqEntity entity = new DrvlineReqEntity();
        entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG ,
            vehicleNo, vehicleColor,
                MsgConstant.SubServiceType.DOWN_BASE_MSG_DRVLINE_REQ, new byte[0]);
        return entity;
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}
