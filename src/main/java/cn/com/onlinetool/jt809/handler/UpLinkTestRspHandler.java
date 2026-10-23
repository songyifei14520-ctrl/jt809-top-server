package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.linkManager.MainConnectRspEntity;
import org.dromara.common.jt809.netty.codec.entity.linkManager.MainLinkTestRspEntity;
import org.dromara.common.jt809.netty.codec.enums.LinkManagerEnum;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author choice
 * @description: 主链路连接保持应答消息处理器
 * @date 2024-01-01
 */
@Slf4j
@Component
public class UpLinkTestRspHandler implements CommonHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        log.info("{}:接收到主链路连接保持应答消息", new Date());
        // 处理主链路连接保持应答逻辑
        log.info("主链路连接保持应答处理完成");
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {
        log.info("主链路连接保持应答处理完成");

        ctx.write(MainLinkTestRspEntity.create(0,null).encode());
    }
}
