package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.entity.linkManager.MainDisConnectRspEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class UpDisconnectHandler implements CommonHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到主链路注销应答消息",new Date());
        //msg.getMsgHead().setMsgId((short) MsgConstant.PrimaryServiceType.UP_DISCONNECT_RSP);
        ctx.write(MainDisConnectRspEntity.create(0,null).encode());
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
