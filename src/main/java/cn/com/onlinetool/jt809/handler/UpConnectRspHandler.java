package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author choice
 * @description: 主链路登录应答消息处理器
 * @date 2024-01-01
 */
@Slf4j
@Component
public class UpConnectRspHandler implements CommonHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到主链路登录应答消息", new Date());
        // 处理主链路登录应答逻辑
        log.info("主链路登录应答处理完成");
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
