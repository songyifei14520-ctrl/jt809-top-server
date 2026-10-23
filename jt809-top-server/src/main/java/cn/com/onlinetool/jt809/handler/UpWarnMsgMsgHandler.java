package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.util.PacketUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.warnMsg.AdptInfoEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.warnMsg.BaseWarnMsgEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.warnMsg.UrgeTodoAckEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpWarnMsgMsgHandler implements CommonHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {

        ByteBuf copiedBuf = Unpooled.copiedBuffer(PacketUtil.message2Bytes(msg));
        BaseWarnMsgEntity baseEntity = new BaseWarnMsgEntity().decode(copiedBuf);
        switch (baseEntity.getDataType()){
            case MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_ACK:
                UrgeTodoAckEntity urgeTodoAckEntity = new UrgeTodoAckEntity().decode(copiedBuf);
                log.info("主链路报警信息交互消息- 报警督办应答：" + JSONObject.toJSONString(urgeTodoAckEntity));
                break;
            case MsgConstant.SubServiceType.UP_WARN_MSG_ADPT_INFO:
                AdptInfoEntity adptInfoEntity = new AdptInfoEntity().decode(copiedBuf);
                log.info("主链路报警信息交互消息- 上报报警信息：" + JSONObject.toJSONString(adptInfoEntity));
                break;
            case MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_ACK_INFO:
                //todo 2019 新增协议
                break;
            case MsgConstant.SubServiceType.UP_WARN_MSG_ADPT_TODO_INFO:
                //todo 2019 新增协议
                break;
            case MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_REQ_INFO:
                //todo 2019 新增协议
                break;
            case MsgConstant.SubServiceType.UP_WARN_MSG_INFORM_TIPS:
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
