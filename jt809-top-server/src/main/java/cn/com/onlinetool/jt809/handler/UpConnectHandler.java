package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.bean.UpConnectReq;
import cn.com.onlinetool.jt809.config.BusinessConfig;
import cn.com.onlinetool.jt809.constants.JT809ResCodeConstants;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.com.onlinetool.jt809.util.ByteArrayUtil;
import cn.com.onlinetool.jt809.util.PacketUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.linkManager.MainConnectReqEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.linkManager.MainConnectRspEntity;
import cn.com.onlinetool.jt809.netty.codec.enums.LinkManagerEnum;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


/**
 * @author choice
 * 主链路登陆逻辑处理
 * @date 2019-06-28 14:35
 *
 */
@Slf4j
@Component
public class UpConnectHandler implements CommonHandler {
    @Resource
    TcpChannelMsgManage channelMsgManage;

    @Resource
    private BusinessConfig businessConfig;

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

//    @Autowired
//    KafkaTemplate<String,Object> kafkaTemplate;
//    @Autowired
//    TcpChannelMsgManage tcpChannelMsgManage;

    @Override
    public void handler(ChannelHandlerContext ctx, Message msg) {
        this.login(ctx,msg);

    }

    @Override
    public void handler(ChannelHandlerContext ctx, ByteBuf msg) {

        JT809Config.initUpperPlatformInfo(null, null,
            0, null,null, null,
            10000001, "1.2.15",
            10000002, 10000003, 10000004);

        MainConnectReqEntity reqs = new MainConnectReqEntity().decode(msg);
        log.info("登陆请求参数...：{}",reqs);

        int userId = (int) reqs.getUserId();
        String password = reqs.getPassword();
        String downLinkIp = reqs.getDownLinkIp();
        int downLinkPort = reqs.getDownLinkPort() ;

        UpConnectReq req = new UpConnectReq();
        req.setUserId(userId);
        req.setPassword(password);
        req.setDownLinkIp(downLinkIp);
        req.setDownLinkPort(downLinkPort);
        log.info("登陆请求信息：" + JSONObject.toJSONString(req));

        // 接入校验账号改由配置提供（演示默认值见 application.properties 的 business.platform-*）
        int testUser = businessConfig.getPlatformUserId();
        String testPass = businessConfig.getPlatformPassword();

        byte[] result = new byte[]{JT809ResCodeConstants.UpConnect.SUCCESS};
        int verifyCode = 0;
        if (testUser == req.getUserId() && testPass.equals(password.trim())) {
            log.info("登陆成功");

//            byte[] body = ByteArrayUtil.append(result, verifyCode);
//            msg.getMsgHead().setMsgLength(msg.getMsgHead().getMsgLength() - msg.getMsgBody().length + body.length);
//            msg.setMsgBody(body);
//            ctx.write(msg);

            ctx.write(MainConnectRspEntity.create(LinkManagerEnum.MainConnectRspEnum.SUCCESS.getValue(),verifyCode).encode());
            channelMsgManage.addChannel(Integer.toString(testUser),ctx.channel());

            // 登录成功后启动从链路
            cn.com.onlinetool.jt809.server.JT809Server jt809Server = getJT809Server();
            if (jt809Server != null) {
                log.info("下级平台登录成功，开始启动从链路...");
                jt809Server.startSubLink(ByteUtil.int2Byte(verifyCode));
            } else {
                log.error("JT809Server未注入，无法启动从链路");
            }

            return;
        }

        ctx.write(MainConnectRspEntity.create((byte) JT809ResCodeConstants.UpConnect.USER_NOT_EXIST,1L).encode());
        ctx.channel().close();
    }

    /**
     * 登陆逻辑处理
     * @param msg
     */
    private void login(ChannelHandlerContext ctx,Message msg) {

        ByteBuf copiedBuf = Unpooled.copiedBuffer(PacketUtil.message2Bytes(msg));
        MainConnectReqEntity reqs = new MainConnectReqEntity().decode(copiedBuf);
        log.info("登陆请求参数...：{}",reqs);

        int userId = (int) reqs.getUserId();
        String password = reqs.getPassword();
        String downLinkIp = reqs.getDownLinkIp();
        int downLinkPort = reqs.getDownLinkPort() ;

        UpConnectReq req = new UpConnectReq();
        req.setUserId(userId);
        req.setPassword(password);
        req.setDownLinkIp(downLinkIp);
        req.setDownLinkPort(downLinkPort);
        log.info("登陆请求信息：" + JSONObject.toJSONString(req));

        // 接入校验账号改由配置提供（演示默认值见 application.properties 的 business.platform-*）
        int testUser = businessConfig.getPlatformUserId();
        String testPass = businessConfig.getPlatformPassword();

        byte[] result = new byte[]{JT809ResCodeConstants.UpConnect.SUCCESS};
        byte[] verifyCode = new byte[4];
        msg.getMsgHead().setMsgId((short) MsgConstant.PrimaryServiceType.UP_CONNECT_RSP);
        if (testUser == req.getUserId() && testPass.equals(password.trim())) {
            log.info("登陆成功");

//            byte[] body = ByteArrayUtil.append(result, verifyCode);
//            msg.getMsgHead().setMsgLength(msg.getMsgHead().getMsgLength() - msg.getMsgBody().length + body.length);
//            msg.setMsgBody(body);
//            ctx.write(msg);

            ctx.write(MainConnectRspEntity.create((byte) JT809ResCodeConstants.UpConnect.SUCCESS,0L).encode());
            channelMsgManage.addChannel(Integer.toString(testUser),ctx.channel());

            // 登录成功后启动从链路
            cn.com.onlinetool.jt809.server.JT809Server jt809Server = getJT809Server();
            if (jt809Server != null) {
                log.info("下级平台登录成功，开始启动从链路...");
                jt809Server.startSubLink(verifyCode);
            } else {
                log.error("JT809Server未注入，无法启动从链路");
            }

            return;
        }
//        if ("".equals(req.getDownLinkIp())) {
//            log.info("ip地址不正确");
//            result = new byte[]{JT809ResCodeConstants.UpConnect.IP_ERROR};
//        } else if (86896940 != msg.getMsgHead().getMsgGnssCenterId()) {
//            log.info("接入码不正确");
//            result = new byte[]{JT809ResCodeConstants.UpConnect.GUSSCENTERID_ERROR};
//        } else if (testUser != req.getUserId()) {
//            log.info("用户没有注册");
//            result = new byte[]{JT809ResCodeConstants.UpConnect.USER_NOT_EXIST};
//        } else if (!testPass.equals(password)) {
//            log.info("密码错误");
//            result = new byte[]{JT809ResCodeConstants.UpConnect.PASSWORD_ERROR};
//        } else {
//            log.info("其他错误");
//            result = new byte[]{JT809ResCodeConstants.UpConnect.OTHER_ERROR};
//        }
        byte[] body = ByteArrayUtil.append(result, verifyCode);
        msg.getMsgHead().setMsgLength(msg.getMsgHead().getMsgLength() - msg.getMsgBody().length + body.length);
        msg.setMsgBody(body);
        ctx.write(MainConnectRspEntity.create((byte) JT809ResCodeConstants.UpConnect.USER_NOT_EXIST,1L).encode());
        ctx.channel().close();
    }
}
