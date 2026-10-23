package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author choice
 * @description: 平台链路连接情况与车辆定位消息传输情况上报应答消息处理器
 * @date 2024-01-01
 */
@Slf4j
@Component
public class UpManageMsgRspHandler implements CommonHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到平台链路连接情况与车辆定位消息传输情况上报应答消息", new Date());
        // 处理平台链路连接情况与车辆定位消息传输情况上报应答逻辑
        log.info("平台链路连接情况与车辆定位消息传输情况上报应答处理完成");
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

    }
}
