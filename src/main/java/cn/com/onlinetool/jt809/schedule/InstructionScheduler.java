package cn.com.onlinetool.jt809.schedule;

import cn.com.onlinetool.jt809.annotation.ConditionalOnSchedulerEnabled;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JT809协议消息定时调度器主类
 * 统一管理各类消息的定时发送任务
 * 已禁用 - 通过配置文件控制启用状态
 *
 * 该类已被拆分为以下子调度器：
 * - BaseMsgScheduler: 基础消息调度器
 * - LinkManagerScheduler: 链路管理调度器
 * - InfoStaticScheduler: 信息统计调度器
 * - ExgMsgScheduler: 交换消息调度器
 * - WarnMsgScheduler: 报警消息调度器
 * - CtrlMsgScheduler: 控制消息调度器
 */
@Slf4j
@Component
@ConditionalOnSchedulerEnabled("instruction")
public class InstructionScheduler {

    @Resource
    private BaseMsgScheduler baseMsgScheduler;

    @Resource
    private LinkManagerScheduler linkManagerScheduler;

    @Resource
    private InfoStaticScheduler infoStaticScheduler;

    @Resource
    private ExgMsgScheduler exgMsgScheduler;

    @Resource
    private WarnMsgScheduler warnMsgScheduler;

    @Resource
    private CtrlMsgScheduler ctrlMsgScheduler;

    @Resource
    private JTT1078To809MsgScheduler jtt1078To809MsgScheduler;

    /**
     * 获取基础消息调度器
     */
    public BaseMsgScheduler getBaseMsgScheduler() {
        return baseMsgScheduler;
    }

    /**
     * 获取链路管理调度器
     */
    public LinkManagerScheduler getLinkManagerScheduler() {
        return linkManagerScheduler;
    }

    /**
     * 获取信息统计调度器
     */
    public InfoStaticScheduler getInfoStaticScheduler() {
        return infoStaticScheduler;
    }

    /**
     * 获取交换消息调度器
     */
    public ExgMsgScheduler getExgMsgScheduler() {
        return exgMsgScheduler;
    }

    /**
     * 获取报警消息调度器
     */
    public WarnMsgScheduler getWarnMsgScheduler() {
        return warnMsgScheduler;
    }

    /**
     * 获取控制消息调度器
     */
    public CtrlMsgScheduler getCtrlMsgScheduler() {
        return ctrlMsgScheduler;
    }

    /**
     * 获取控制消息调度器
     */
    public JTT1078To809MsgScheduler getJTT1078To809MsgScheduler() {
        return jtt1078To809MsgScheduler;
    }

    /**
     * 初始化方法，用于启动时日志输出
     */
    public void init() {
        log.info("JT809协议消息定时调度器已启动，包含以下子调度器：");
        log.info("- 基础消息调度器: {}", baseMsgScheduler.getClass().getSimpleName());
        log.info("- 链路管理调度器: {}", linkManagerScheduler.getClass().getSimpleName());
        log.info("- 信息统计调度器: {}", infoStaticScheduler.getClass().getSimpleName());
        log.info("- 交换消息调度器: {}", exgMsgScheduler.getClass().getSimpleName());
        log.info("- 报警消息调度器: {}", warnMsgScheduler.getClass().getSimpleName());
        log.info("- 控制消息调度器: {}", ctrlMsgScheduler.getClass().getSimpleName());
        log.info("- 1078消息调度器: {}", jtt1078To809MsgScheduler.getClass().getSimpleName());
    }
}
