package cn.com.onlinetool.jt809.netty.codec.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * JT809消息序号管理器
 * 负责生成和管理JT809协议消息的序列号
 * 支持按子业务数据类型分别管理序号
 * 
 * @author Song Yifei
 */
public class JT809SequenceManager {

    /**
     * 全局消息序列号计数器（兼容旧版本）
     */
    private static volatile AtomicLong msgSnNo = new AtomicLong(0);
    
    /**
     * 按子业务数据类型分别管理的序号计数器
     * Key: dataType (子业务数据类型)
     * Value: AtomicLong (对应的序号计数器)
     */
    private static final Map<Integer, AtomicLong> dataTypeSequenceMap = new ConcurrentHashMap<>();

    /**
     * JT809协议最大序号值（2^32-1）
     */
    private static final long MAX_SEQUENCE = 4294967295L;

    /**
     * 获取下一个消息序列号
     * 根据JT809协议要求：
     * - 程序开始运行时编号等于零
     * - 发送第一个消息数据时开始计数
     * - 到最大数4,294,967,295后归零
     * 
     * @return 下一个消息序列号
     */
    public static long getNextMsgSn() {
        long current = msgSnNo.get();
        
        // 检查是否需要重置序号
        if (current >= MAX_SEQUENCE || current <= 0) {
            msgSnNo.set(1);
            return 1L;
        } else {
            return msgSnNo.incrementAndGet();
        }
    }

    /**
     * 获取当前消息序列号（不递增）
     * 
     * @return 当前消息序列号
     */
    public static long getCurrentMsgSn() {
        return msgSnNo.get();
    }

    /**
     * 重置消息序列号
     * 通常在系统重启或链路重连时使用
     */
    public static void resetMsgSn() {
        msgSnNo.set(0);
    }

    /**
     * 设置消息序列号
     * 用于链路重连后恢复序号状态
     * 
     * @param msgSn 要设置的序列号
     */
    public static void setMsgSn(long msgSn) {
        if (msgSn >= 0 && msgSn <= MAX_SEQUENCE) {
            msgSnNo.set(msgSn);
        } else {
            throw new IllegalArgumentException("消息序列号必须在0到" + MAX_SEQUENCE + "之间");
        }
    }

    /**
     * 根据子业务数据类型获取下一个消息序列号
     * 按照JT809协议要求，每个子业务数据类型分别编号
     * 
     * @param dataType 子业务数据类型
     * @return 下一个消息序列号
     */
    public static long getNextMsgSnByDataType(int dataType) {
        AtomicLong counter = dataTypeSequenceMap.computeIfAbsent(dataType, k -> new AtomicLong(0));
        
        long current = counter.get();
        
        // 检查是否需要重置序号
        if (current >= MAX_SEQUENCE || current <= 0) {
            counter.set(1);
            return 1L;
        } else {
            return counter.incrementAndGet();
        }
    }
    
    /**
     * 根据子业务数据类型获取当前消息序列号（不递增）
     * 
     * @param dataType 子业务数据类型
     * @return 当前消息序列号
     */
    public static long getCurrentMsgSnByDataType(int dataType) {
        AtomicLong counter = dataTypeSequenceMap.get(dataType);
        return counter != null ? counter.get() : 0L;
    }
    
    /**
     * 重置指定子业务数据类型的消息序列号
     * 
     * @param dataType 子业务数据类型
     */
    public static void resetMsgSnByDataType(int dataType) {
        AtomicLong counter = dataTypeSequenceMap.get(dataType);
        if (counter != null) {
            counter.set(0);
        }
    }
    
    /**
     * 设置指定子业务数据类型的消息序列号
     * 用于链路重连后恢复序号状态
     * 
     * @param dataType 子业务数据类型
     * @param msgSn 要设置的序列号
     */
    public static void setMsgSnByDataType(int dataType, long msgSn) {
        if (msgSn >= 0 && msgSn <= MAX_SEQUENCE) {
            AtomicLong counter = dataTypeSequenceMap.computeIfAbsent(dataType, k -> new AtomicLong(0));
            counter.set(msgSn);
        } else {
            throw new IllegalArgumentException("消息序列号必须在0到" + MAX_SEQUENCE + "之间");
        }
    }
    
    /**
     * 重置所有子业务数据类型的消息序列号
     * 通常在系统重启时使用
     */
    public static void resetAllDataTypeSequences() {
        dataTypeSequenceMap.clear();
    }
    
    /**
     * 获取所有子业务数据类型的当前序号状态
     * 用于链路重连时的序号通知
     * 
     * @return 包含所有dataType和对应序号的Map
     */
    public static Map<Integer, Long> getAllDataTypeSequences() {
        Map<Integer, Long> result = new ConcurrentHashMap<>();
        dataTypeSequenceMap.forEach((dataType, counter) -> {
            result.put(dataType, counter.get());
        });
        return result;
    }

    /**
     * 获取最大序列号值
     * 
     * @return 最大序列号值
     */
    public static long getMaxSequence() {
        return MAX_SEQUENCE;
    }
}