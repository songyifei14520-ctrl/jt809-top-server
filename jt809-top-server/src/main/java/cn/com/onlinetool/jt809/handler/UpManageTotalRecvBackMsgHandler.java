package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author choice
 * @description: 接收定位信息数量通知消息处理器
 * @date 2024-01-01
 */
@Slf4j
@Component
public class UpManageTotalRecvBackMsgHandler implements CommonHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到接收定位信息数量通知消息", new Date());
        // 处理接收定位信息数量通知逻辑
        log.info("接收定位信息数量通知处理完成");
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
