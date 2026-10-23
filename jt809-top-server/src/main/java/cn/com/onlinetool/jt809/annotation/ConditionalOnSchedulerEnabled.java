package cn.com.onlinetool.jt809.annotation;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 定时任务条件注解
 * 用于控制定时任务的启用/禁用
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(SchedulerCondition.class)
public @interface ConditionalOnSchedulerEnabled {
    
    /**
     * 调度器类型
     * @return 调度器类型名称
     */
    String value();
}