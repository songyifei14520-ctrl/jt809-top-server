package cn.com.onlinetool.jt809.annotation;

import cn.com.onlinetool.jt809.config.SchedulerConfig;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * 定时任务条件判断类
 */
public class SchedulerCondition implements Condition {
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(
                ConditionalOnSchedulerEnabled.class.getName());
        
        if (attrs == null || attrs.isEmpty()) {
            return false;
        }
        
        String schedulerType = (String) attrs.getFirst("value");
        if (schedulerType == null || schedulerType.trim().isEmpty()) {
            return false;
        }
        
        try {
            SchedulerConfig config = context.getBeanFactory().getBean(SchedulerConfig.class);
            return config.isSchedulerEnabled(schedulerType);
        } catch (Exception e) {
            // 如果无法获取配置，默认返回false（禁用）
            return false;
        }
    }
}