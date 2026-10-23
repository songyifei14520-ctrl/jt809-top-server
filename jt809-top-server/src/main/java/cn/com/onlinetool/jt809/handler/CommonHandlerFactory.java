package cn.com.onlinetool.jt809.handler;


import jakarta.annotation.PostConstruct;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author choice
 * @description: 初始化逻辑处理类
 * @date 2018-12-27 12:27
 *
 */
@Component
public class CommonHandlerFactory implements ApplicationContextAware {
    private static Map<Integer, CommonHandler> handlers = new ConcurrentHashMap<>();
    private static ApplicationContext context;


    @PostConstruct
    public void serverHandlerFactory() {
        // 链路管理相关消息
        handlers.put(MsgConstant.PrimaryServiceType.UP_CONNECT_REQ,context.getBean(UpConnectHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_CONNECT_RSP,context.getBean(UpConnectRspHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_DISCONNECT_REQ,context.getBean(UpDisconnectHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_DISCONNECT_RSP,context.getBean(UpDisconnectRspHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_LINKTEST_REQ,context.getBean(UpLinkTestHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_LINKTEST_RSP,context.getBean(UpLinkTestRspHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_DISCONNECT_INFORM,context.getBean(UpDisconnectInformHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_CLOSELINK_INFORM,context.getBean(UpCloseLinkInformHandler.class));

        // 业务数据交换相关消息
        handlers.put(MsgConstant.PrimaryServiceType.UP_EXG_MSG,context.getBean(UpExgMsgHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,context.getBean(UpPlatformMsgHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_WARN_MSG,context.getBean(UpWarnMsgMsgHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_CTRL_MSG,context.getBean(UpCtrlMsgHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_BASE_MSG,context.getBean(UpBaseMsgHandler.class));

        // 信息统计相关消息
        handlers.put(MsgConstant.PrimaryServiceType.UP_MANAGE_TOTAL_RECV_BACK_MSG,context.getBean(UpManageTotalRecvBackMsgHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_MANAGE_MSG_RSP,context.getBean(UpManageMsgRspHandler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_MANAGE_MSG_SN_INFORM,context.getBean(UpManageMsgSnInformHandler.class));

        // 1078相关
        handlers.put(MsgConstant.PrimaryServiceType.UP_AUTHORIZE_MSG,context.getBean(JTT1078Handler.class));
        handlers.put(MsgConstant.PrimaryServiceType.UP_SEARCH_MSG,context.getBean(JTT1078Handler.class));

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(null == context){
            context = applicationContext;
        }
    }

    public CommonHandler getHandler(Integer flag){
        return handlers.get(flag);
    }


}
