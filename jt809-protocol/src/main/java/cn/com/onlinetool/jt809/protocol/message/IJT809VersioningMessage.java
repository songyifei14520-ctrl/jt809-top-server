package cn.com.onlinetool.jt809.protocol.message;

import cn.com.onlinetool.jt809.protocol.enums.JT809MessageId;
import cn.com.onlinetool.jt809.protocol.enums.JT809ProtocolVersion;
import cn.com.onlinetool.jt809.protocol.serialization.IJT809MessageFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * JT809 版本化消息
 */
public interface IJT809VersioningMessage extends IJT809MessageFormatter {
    /**
     * 获取协议版本
     *
     * @return 协议版本
     */
    @JsonIgnore
    JT809ProtocolVersion getProtocolVersion();

    /**
     * 获取消息ID
     *
     * @return 消息ID
     */
    @JsonIgnore
    JT809MessageId getMessageId();
}
