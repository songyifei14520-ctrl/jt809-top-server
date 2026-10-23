package cn.com.onlinetool.jt809.netty.codec.entity;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.enums.EncryptFlagEnum;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.JT809Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class Header2011 extends Header{
    /**
     * 消息头长度 (2011版本)
     */
    public static final int MSG_HEADER_LENTH = 22;

    public Header2011() {
        super();
        this.setMsgGnssscenterId(JT809Config.msmsgGnssscenterId);
        this.setVersionFlag(JT809Config.VERSION_FLAG);
        this.setEncryptFlag(JT809Config.encrypt? EncryptFlagEnum.YES.getValue() : EncryptFlagEnum.NO.getValue());
        this.setEncryptKey(JT809Config.encrypt? JT809Config.encryptkey : 0L);
    }

    /**
     * 获取协议版本对应的消息头长度
     */
    @Override
    public int getHeaderLength() {
        return MSG_HEADER_LENTH;
    }


    @Override
    public Header2011 decode(ByteBuf buf) {
        Header2011 header = new Header2011();
        // 跳过起始标识符，从第2个字节开始解析
        header.setMsgLength(BufUtil.buf2UnsignedLong(buf.slice(1, 4)));
        header.setMsgSn(BufUtil.buf2UnsignedLong(buf.slice(5, 4)));
        header.setMsgId(BufUtil.buf2UnsignedInt(buf.slice(9, 2)));
        header.setMsgGnssscenterId(BufUtil.buf2UnsignedLong(buf.slice(11, 4)));
        header.setVersionFlag(BufUtil.bufToBytes(buf.slice(15, 3)));
        header.setEncryptFlag(buf.getByte(18));
        header.setEncryptKey(BufUtil.buf2UnsignedLong(buf.slice(19, 4)));

        return header;
    }

    /**
     * 创建2011版本消息头
     * @param msgId 消息ID
     * @param msgbodyLength 消息体长度
     * @return Header2011实例
     */
    public static Header2011 create(int msgId, int msgbodyLength) {
        Header2011 header = new Header2011();
        header.setMsgLength(1 + Header2011.MSG_HEADER_LENTH + msgbodyLength + 2 + 1);
        header.setMsgSn(JT809Util.getMsgSn(msgId));
        header.setMsgId(msgId);
        header.setMsgGnssscenterId(JT809Config.msmsgGnssscenterId);
        header.setVersionFlag(JT809Config.VERSION_FLAG);
        header.setEncryptFlag(JT809Config.encrypt? EncryptFlagEnum.YES.getValue() : EncryptFlagEnum.NO.getValue());
        header.setEncryptKey(JT809Config.encrypt? JT809Config.encryptkey : 0L);
        return header;
    }


    @Override
    public byte[] encode() {
        byte[] head = new byte[MSG_HEADER_LENTH];
        int offset = 0;

        // 消息长度 (4字节)
        System.arraycopy(ByteUtil.int2Byte((int) getMsgLength()), 0, head, offset, 4);
        offset += 4;

        // 报文序列号 (4字节)
        System.arraycopy(ByteUtil.int2Byte((int) getMsgSn()), 0, head, offset, 4);
        offset += 4;

        // 业务数据类型 (2字节)
        System.arraycopy(ByteUtil.short2Bytes((short) getMsgId()), 0, head, offset, 2);
        offset += 2;

        // 下级平台接入码 (4字节)
        System.arraycopy(ByteUtil.int2Byte((int) getMsgGnssscenterId()), 0, head, offset, 4);
        offset += 4;

        // 协议版本号标识 (3字节)
        System.arraycopy(getVersionFlag(), 0, head, offset, 3);
        offset += 3;

        // 报文加密标识位 (1字节)
        head[offset] = getEncryptFlag();
        offset += 1;

        // 数据加密的密匙 (4字节)
        System.arraycopy(ByteUtil.int2Byte((int) getEncryptKey()), 0, head, offset, 4);

        return head;
    }

}


