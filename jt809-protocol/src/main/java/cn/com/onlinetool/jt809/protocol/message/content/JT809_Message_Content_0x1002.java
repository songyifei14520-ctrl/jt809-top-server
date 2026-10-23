package cn.com.onlinetool.jt809.protocol.message.content;

import lombok.*;
import cn.com.onlinetool.jt809.protocol.IVersionedSpecificationContext;
import cn.com.onlinetool.jt809.protocol.enums.JT809MessageId;
import cn.com.onlinetool.jt809.protocol.enums.JT809SubMessageId;
import cn.com.onlinetool.jt809.protocol.exceptions.UnsupportedJT809OperationException;
import cn.com.onlinetool.jt809.protocol.message.content.JT809_Message_Content_0x1400_Sub.JT809_Message_Content_0x1400_SubMessage;
import cn.com.onlinetool.jt809.protocol.message.content.JT809_Message_Content_0x1400_Sub.JT809_Message_Content_0x1400_SubMessageRegistration;
import cn.com.onlinetool.jt809.protocol.serialization.IJT809MessageBufferReader;
import cn.com.onlinetool.jt809.protocol.serialization.IJT809MessageBufferWriter;

/**
 * 主链路报警信息交互消息 UP_WARN_MSG
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JT809_Message_Content_0x1002 extends JT809MessageContent {
    public static final JT809MessageId MESSAGE_ID = JT809MessageId.JT809_Message_0x1002;

    @Override
    public JT809MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Getter
    @Setter
    private byte result; // 验证结果 byte

    @Getter
    @Setter
    private long verifyCode; // 校验码 byte[4]

    @Override
    public void serialize(IVersionedSpecificationContext ctx, IJT809MessageBufferWriter writer) {
        writer.writeUInt16(getMessageId().getValue());

    }

    @Override
    public void deserialize(IVersionedSpecificationContext ctx, IJT809MessageBufferReader reader) {

        setResult(reader.readByte());
        setVerifyCode(reader.readUInt32());

        System.out.println("result:" + getResult());
        System.out.println("verifyCode:" + getVerifyCode());
    }

    public static JT809_Message_Content_0x1002 decode(IVersionedSpecificationContext ctx, IJT809MessageBufferReader reader) {
        JT809_Message_Content_0x1002 content = new JT809_Message_Content_0x1002();
        content.deserialize(ctx, reader);
        return content;
    }
}
