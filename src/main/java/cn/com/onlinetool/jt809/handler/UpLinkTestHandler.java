package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.linkManager.MainLinkTestRspEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;
import org.springframework.stereotype.Component;

/**
 * @author choice
 * 主链路链接保持处理
 * @date 2019-06-28 15:49
 *
 */
@Slf4j
@Component
public class UpLinkTestHandler implements CommonHandler{

    @Resource
    TcpChannelMsgManage channelMsgManage;
    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        ByteBuf copiedBuf = Unpooled.copiedBuffer(MainLinkTestRspEntity.create(0, "").encode());
        log.info("接收到心跳包:{}",BufUtil.buf2HexStr(copiedBuf));
//        msg.getMsgHead().setMsgId((short) MsgConstant.PrimaryServiceType.UP_LINKTEST_RSP);
//        ctx.write(msg);
        ctx.write(MainLinkTestRspEntity.create(0, "").encode());

    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

        log.info("主链路连接保持应答处理完成");

        ctx.write(MainLinkTestRspEntity.create(0,null).encode());

    }
}
