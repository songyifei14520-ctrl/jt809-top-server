package cn.com.onlinetool.jt809.handler.inbound;

import cn.com.onlinetool.jt809.config.BusinessConfig;
import cn.com.onlinetool.jt809.config.NettyConfig;
import cn.com.onlinetool.jt809.decoder.Byte2MessageDecoder;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author choice
 * @description: 服务端字节转消息处理器
 * @date 2018-12-27 13:14
 *
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class ServerByte2MessageInboundHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    Byte2MessageDecoder decoder;
    @Autowired
    NettyConfig.NettyServerConfig nettyConfig;
    @Autowired
    BusinessConfig businessConfig;
    @Autowired
    TcpChannelMsgManage tcpChannelMsgManage;
    @Autowired
    private ApplicationContext applicationContext;

    // 延迟获取JT809Server实例，避免循环依赖
    private cn.com.onlinetool.jt809.server.JT809Server getJT809Server() {
        try {
            return applicationContext.getBean(cn.com.onlinetool.jt809.server.JT809Server.class);
        } catch (Exception e) {
            log.warn("无法获取JT809Server实例: {}", e.getMessage());
            return null;
        }
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * 作用：当新客户端连接成功建立时触发（TCP三次握手完成）
     * 触发时机：客户端首次连接或重连成功时
     * 继承关系：通常继承自 ChannelInboundHandlerAdapter
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(null != nettyConfig.getMaxConnectNum() && nettyConfig.getMaxConnectNum() > 0){
            //判断当前连接数是否大于 配置的最大连接数 如果大于最大连接数直接关闭连接
            if(tcpChannelMsgManage.getTcpClientConnectNum() > nettyConfig.getMaxConnectNum()){
                log.warn("当前节点连接数已经超过最大连接数 : {}", tcpChannelMsgManage.getTcpClientConnectNum());
                //关闭客户端连接
                ctx.channel().close();
                return;
            }
        }

        //计数器加1
        tcpChannelMsgManage.incrementTcpClientConnectNum();
        log.info("连接到新客户端，当前节点连接数 : {}", tcpChannelMsgManage.getTcpClientConnectNum());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //计数器减1
        tcpChannelMsgManage.decrementTcpClientConnectNum();
        //移除通道缓存
        tcpChannelMsgManage.removeChannelByChannel(ctx.channel());
        log.info("断开客户端连接，客户端信息: {}，当前节点连接数: {}",
                ctx.channel().remoteAddress().toString(),
                tcpChannelMsgManage.getTcpClientConnectNum());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf byteBuf = (ByteBuf) msg;
        //decoder.decode(ctx, byteBuf);
        // 注释掉直接传递，让解码器处理数据
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("客户端连接超时,客户端信息 : {}",ctx.channel().remoteAddress().toString());
                //移除通道缓存
                tcpChannelMsgManage.removeChannelByChannel(ctx.channel());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生错误.......",cause);
        //移除通道缓存
        tcpChannelMsgManage.removeChannelByChannel(ctx.channel());
        ctx.close();
    }
}
