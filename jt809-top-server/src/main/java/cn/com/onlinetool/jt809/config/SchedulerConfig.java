package cn.com.onlinetool.jt809.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JT809定时任务配置类
 * 统一管理所有定时任务的开关状态
 */
@Data
@Component
@ConfigurationProperties(prefix = "jt809.scheduler")
public class SchedulerConfig {

    /**
     * 全局定时任务开关
     */
    private boolean enabled = false;

    /**
     * 基础消息调度器开关
     */
    private boolean baseMsgEnabled = false;

    /**
     * 控制消息调度器开关
     */
    private boolean ctrlMsgEnabled = false;

    /**
     * 交换消息调度器开关
     */
    private boolean exgMsgEnabled = false;

    /**
     * 信息统计调度器开关
     */
    private boolean infoStaticEnabled = false;

    /**
     * 指令调度器开关
     */
    private boolean instructionEnabled = false;

    /**
     * 链路管理调度器开关
     */
    private boolean linkManagerEnabled = false;

    /**
     * 平台消息调度器开关
     */
    private boolean platformMsgEnabled = false;

    /**
     * 告警消息调度器开关
     */
    private boolean warnMsgEnabled = false;

    /**
     * 1078To809消息调度器开关
     */
    private boolean jtt1078To809Msg = false;

    /**
     * 检查指定调度器是否启用
     * @param schedulerType 调度器类型
     * @return 是否启用
     */
    public boolean isSchedulerEnabled(String schedulerType) {
        if (!enabled) {
            return false;
        }

        switch (schedulerType.toLowerCase()) {
            case "basemsg":
                return baseMsgEnabled;
            case "ctrlmsg":
                return ctrlMsgEnabled;
            case "exgmsg":
                return exgMsgEnabled;
            case "infostatic":
                return infoStaticEnabled;
            case "instruction":
                return instructionEnabled;
            case "linkmanager":
                return linkManagerEnabled;
            case "platformmsg":
                return platformMsgEnabled;
            case "warnmsg":
                return warnMsgEnabled;
            case "jtt1078to809msg":
                return jtt1078To809Msg;
            default:
                return false;
        }
    }
}
