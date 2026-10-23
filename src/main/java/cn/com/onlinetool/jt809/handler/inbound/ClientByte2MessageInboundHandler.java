package cn.com.onlinetool.jt809.handler.inbound;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.config.BusinessConfig;
import cn.com.onlinetool.jt809.config.NettyConfig;
import cn.com.onlinetool.jt809.decoder.Byte2MessageDecoder;
import cn.com.onlinetool.jt809.handler.CommonHandlerFactory;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.com.onlinetool.jt809.util.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.linkManager.SubConnectReqEntity;
import org.dromara.common.jt809.netty.codec.entity.linkManager.SubLinkTestReqEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author choice
 * 解码处理
 * @date 2019-07-03 09:55
 *
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class ClientByte2MessageInboundHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    Byte2MessageDecoder decoder;
    @Autowired
    NettyConfig.NettyServerConfig nettyConfig;
    @Autowired
    BusinessConfig businessConfig;
    @Autowired
    TcpChannelMsgManage tcpChannelMsgManage;
    @Autowired
    CommonHandlerFactory handlerFactory;
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        // 从链路连接成功后，发送从链路连接请求消息
        try {

            byte[] verifyCode = getJT809Server().getVerifyCode();
            if (verifyCode != null && verifyCode.length >= 4) {
                // 将byte[]转换为long类型的verifyCode
                long verifyCodeLong = ((long)(verifyCode[0] & 0xFF) << 24) |
                                    ((long)(verifyCode[1] & 0xFF) << 16) |
                                    ((long)(verifyCode[2] & 0xFF) << 8) |
                                    (long)(verifyCode[3] & 0xFF);

                SubConnectReqEntity subConnectReq = SubConnectReqEntity.create(verifyCodeLong);
                ByteBuf copiedBuf = Unpooled.copiedBuffer(subConnectReq.encode());
                log.info("发送从链路连接请求报文，verifyCode: {}, 报文: {}",
                        verifyCodeLong, BufUtil.buf2HexStr(copiedBuf));

                ctx.write(subConnectReq.encode());
                ctx.flush();
            } else {
                log.warn("未获取到有效的verifyCode，无法发送从链路连接请求");
            }
        } catch (Exception e) {
            log.error("发送从链路连接请求失败", e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //计数器减1
        tcpChannelMsgManage.decrementTcpClientConnectNum();
        log.info("断开服务端连接,服务端信息 : {}",ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf byteBuf = (ByteBuf) msg;
//        decoder.decode(ctx,byteBuf);
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

            }else if(state == IdleState.WRITER_IDLE){
                log.warn("发送心跳包到服务端 : {}",ctx.channel().remoteAddress().toString());
                this.sendTestMsgToServer(ctx);
            }else if(state == IdleState.ALL_IDLE){
                log.warn("发送心跳包到服务端 : {}",ctx.channel().remoteAddress().toString());
                this.sendTestMsgToServer(ctx);
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
        ctx.close();
    }

    /**
     * 测试心跳包发送
     * @param ctx
     * @return
     */
    private void sendTestMsgToServer(ChannelHandlerContext ctx){
        SubLinkTestReqEntity subLinkTestReqEntity = SubLinkTestReqEntity.create();

        ByteBuf copiedBuf = Unpooled.copiedBuffer(subLinkTestReqEntity.encode());
        log.info("发送心跳报文:{}", BufUtil.buf2HexStr(copiedBuf));

        ctx.write(subLinkTestReqEntity.encode());
    }
}
