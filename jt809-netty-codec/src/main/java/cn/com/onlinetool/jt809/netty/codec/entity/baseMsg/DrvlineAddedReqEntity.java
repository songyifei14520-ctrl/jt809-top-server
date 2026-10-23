package cn.com.onlinetool.jt809.netty.codec.entity.baseMsg;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 补报车辆行驶路线信息请求消息 子业务类型标识:UP_BASE_MSG_DRVLINE_ADDED_REQ
 * 描述:下级平台向上级平台补报车辆行驶路线信息
 * 补报车辆行驶路线信息请求消息数据体
 * 根据JT/T808—2019中0x8606规定的报文格式
 */
@Data
@NoArgsConstructor
public class DrvlineAddedReqEntity implements Codec<DrvlineAddedReqEntity> {
    private BaseMsgEntity baseMsg;

    private byte[] drvLine; // 路线信息，按照JT/T808—2019中0x8606规定的报文格式

    @Override
    public DrvlineAddedReqEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseMsgEntity().decode(buf);
        this.drvLine = BufUtil.bufToBytes(baseMsg.getData());
        return this;
    }

    public static DrvlineAddedReqEntity create(String vehicleNo, byte vehicleColor, byte[] drvLine) {
        DrvlineAddedReqEntity entity = new DrvlineAddedReqEntity();
        entity.drvLine = drvLine;
        entity.baseMsg = BaseMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_BASE_MSG,
            vehicleNo, vehicleColor,
                MsgConstant.SubServiceType.UP_BASE_MSG_DRVLINE_ADDED_REQ, drvLine);
        return entity;
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }

}
