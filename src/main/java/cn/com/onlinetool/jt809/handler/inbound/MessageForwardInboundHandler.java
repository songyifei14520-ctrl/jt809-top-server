package cn.com.onlinetool.jt809.handler.inbound;

import cn.com.onlinetool.jt809.config.BusinessConfig;
import cn.com.onlinetool.jt809.handler.CommonHandlerFactory;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.com.onlinetool.jt809.util.ByteArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.JT809Config;
import org.dromara.common.jt809.netty.codec.MsgConstant;
import org.dromara.common.jt809.netty.codec.entity.BaseEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;
import org.dromara.common.jt809.netty.codec.util.JT809Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author choice
 * 将解码后的Message对象转发各个消息类型的handler
 * @date 2019-07-03 10:26
 *
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class MessageForwardInboundHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    BusinessConfig businessConfig;
    @Autowired
    CommonHandlerFactory handlerFactory;
    @Resource
    TcpChannelMsgManage channelMsgManage;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        JT809Config.initUpperPlatformInfo(null, null,
            0, null,null, null,
            70000001, "1.2.15",
            56107078, 37382110, 50361430);

        log.info("接收原始报文:{}", BufUtil.buf2HexStr((ByteBuf)msg));

        try {
            ByteBuf message = JT809Util.decode((ByteBuf) msg);
            log.info("接收解码后报文:{}", BufUtil.buf2HexStr(message));
            BaseEntity baseEntity = new BaseEntity().decode(message);

            // 处理消息分发逻辑
            handleMessage(ctx, message, baseEntity);

        } catch (IllegalArgumentException e) {
            log.error("JT809报文解码失败: {}, 原始报文: {}", e.getMessage(), BufUtil.buf2HexStr((ByteBuf)msg));
            return; // 直接返回，不继续处理
        } catch (Exception e) {
            log.error("JT809报文处理异常: {}, 原始报文: {}", e.getMessage(), BufUtil.buf2HexStr((ByteBuf)msg), e);
            return; // 直接返回，不继续处理
        }
    }

    /**
     * 处理消息分发逻辑
     */
    private void handleMessage(ChannelHandlerContext ctx, ByteBuf message, BaseEntity baseEntity) throws Exception {

        log.info("接收消息类型:{}", baseEntity.getHeader().getMsgId());
        switch (baseEntity.getHeader().getMsgId()) {
            /**
             * 链路管理
             */
            case MsgConstant.PrimaryServiceType.UP_CONNECT_REQ: {
                log.info("【接收】主链路登录请求消息");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }
            case MsgConstant.PrimaryServiceType.UP_DISCONNECT_REQ: {
                log.info("【接收】主链路注销请求消息");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }
            case MsgConstant.PrimaryServiceType.UP_LINKTEST_REQ: {
                log.info("【接收】主链路连接保持请求消息");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }
            case MsgConstant.PrimaryServiceType.UP_DISCONNECT_INFORM: {
                log.info("【接收】主链路断开通知消息");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }
            case MsgConstant.PrimaryServiceType.UP_CLOSELINK_INFORM: {
                log.info("【接收】下级平台主动关闭链路通知消息");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }

            case MsgConstant.PrimaryServiceType.DOWN_LINKTEST_RSP: {
                log.info("【接收】从链路连接保持应答消息");
                break;
            }

            /**
             * 信息统计
             */
            case MsgConstant.PrimaryServiceType.UP_MANAGE_MSG_RSP: {
                log.info("【接收】平台间报文传输情况应答");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }

            case MsgConstant.PrimaryServiceType.UP_MANAGE_TOTAL_RECV_BACK_MSG: {
                log.info("【接收】车辆定位信息数量通知消息");
                handlerFactory.getHandler(baseEntity.getHeader().getMsgId()).handler(ctx, message);
                break;
            }

            /**
             * 车辆动态信息交换业务
             */
            case MsgConstant.PrimaryServiceType.UP_EXG_MSG: {
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_EXG_MSG).handler(ctx, message);
                break;
            }

            /**
             * 平台间信息交互业务
             */
            case MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG: {
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG).handler(ctx, message);
                break;
            }

            /**
             * 车辆报警信息业务
             */
            case MsgConstant.PrimaryServiceType.UP_WARN_MSG: {
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_WARN_MSG).handler(ctx, message);
                break;
            }

            /**
             * 车辆监管业务
             */
            case MsgConstant.PrimaryServiceType.UP_CTRL_MSG: {
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_CTRL_MSG).handler(ctx, message);
                break;
            }

            /**
             * 车辆静态信息交换业务
             */
            case MsgConstant.PrimaryServiceType.UP_BASE_MSG: {
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_BASE_MSG).handler(ctx, message);
                break;
            }

            /**
             * 时效口令
             */
            case MsgConstant.PrimaryServiceType.UP_AUTHORIZE_MSG: {
                log.info("【接收】 时效口令(时效口令上报) 消息类型:{}", baseEntity.getHeader().getMsgId());
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_AUTHORIZE_MSG).handler(ctx, message);
                break;
            }

            /**
             * 远程录像检索
             */
            case MsgConstant.PrimaryServiceType.UP_SEARCH_MSG: {
                log.info("【接收】 远程录像检索(主动上传音视频资源目录信息) 消息类型:{}", baseEntity.getHeader().getMsgId());
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_SEARCH_MSG).handler(ctx, message);
                break;
            }
            /**
             * 远程录像下载
             */
            case MsgConstant.PrimaryServiceType.UP_DOWNLOAD_MSG: {
                log.info("【接收】 远程录像下载(远程录像下载完成通知消息) 消息类型:{}", baseEntity.getHeader().getMsgId());
                handlerFactory.getHandler(MsgConstant.PrimaryServiceType.UP_DOWNLOAD_MSG).handler(ctx, message);
                break;
            }

            default:
                log.info("未知消息:{}", baseEntity);
                log.warn("暂不支持此类型消息：" + (short) baseEntity.getHeader().getMsgId());
                break;
        }

//        if(null == handlerFactory.getHandler(baseEntity.getHeader().getMsgId())){
//            log.warn("暂不支持此类型消息：" + ByteArrayUtil.short2HexStr((short) baseEntity.getHeader().getMsgId()));
//            return;
//        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("数据读取完成");
        super.channelReadComplete(ctx);
    }
}
