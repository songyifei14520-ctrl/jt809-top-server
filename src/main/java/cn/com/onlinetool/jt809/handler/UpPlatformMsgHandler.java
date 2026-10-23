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
import org.dromara.common.jt809.netty.codec.entity.platformMsg.BasePlatFormMsgEntity;
import org.dromara.common.jt809.netty.codec.entity.platformMsg.InfoAckEntity;
import org.dromara.common.jt809.netty.codec.entity.platformMsg.PostQueryAckEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpPlatformMsgHandler implements CommonHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {

        ByteBuf copiedBuf = Unpooled.copiedBuffer(PacketUtil.message2Bytes(msg));
        BasePlatFormMsgEntity baseEntity = new BasePlatFormMsgEntity().decode(copiedBuf);
        switch (baseEntity.getDataType()){
            case MsgConstant.SubServiceType.UP_PLATFORM_MSG_POST_QUERY_ACK:
                PostQueryAckEntity postQueryAckEntity = new PostQueryAckEntity().decode(copiedBuf);
                log.info("平台管理消息-平台信息应答：" + JSONObject.toJSONString(postQueryAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_PLATFORM_MSG_INFO_ACK:
                InfoAckEntity infoAckEntity = new InfoAckEntity().decode(copiedBuf);
                log.info("平台管理消息-平台信息应答：" + JSONObject.toJSONString(infoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_PLATFORM_MSG_RETRAN_REQ:
                //todo 2019 新增协议
                break;
            default:
                break;
        }

    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
