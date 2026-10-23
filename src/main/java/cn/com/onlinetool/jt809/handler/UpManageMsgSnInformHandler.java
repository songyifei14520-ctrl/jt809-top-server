package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author choice
 * @description: 上传平台间消息序列号通知消息处理器
 * @date 2024-01-01
 */
@Slf4j
@Component
public class UpManageMsgSnInformHandler implements CommonHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到上传平台间消息序列号通知消息", new Date());
        // 处理上传平台间消息序列号通知逻辑
        log.info("上传平台间消息序列号通知处理完成");
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
