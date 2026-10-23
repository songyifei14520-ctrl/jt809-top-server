package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.util.PacketUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.MsgConstant;
import org.dromara.common.jt809.netty.codec.entity.BaseEntity;
import org.dromara.common.jt809.netty.codec.entity.baseMsg.BaseMsgEntity;
import org.dromara.common.jt809.netty.codec.entity.baseMsg.VehicleAddedAckEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpBaseMsgHandler implements CommonHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {

        ByteBuf copiedBuf = Unpooled.copiedBuffer(PacketUtil.message2Bytes(msg));
        BaseMsgEntity baseEntity = new BaseMsgEntity().decode(copiedBuf);
        switch (baseEntity.getDataType()){
            case MsgConstant.SubServiceType.UP_BASE_MSG_VEHICLE_ADDED_ACK:
                VehicleAddedAckEntity vehicleAddedAckEntity = new VehicleAddedAckEntity().decode(copiedBuf);
                log.info("主链路静态信息交换消息-补报车辆静态信息应答：" + JSONObject.toJSONString(vehicleAddedAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_BASE_MSG_DRVLINE_ADDED_REQ:
                //todo 2019
                break;
            default:
                break;
        }
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
