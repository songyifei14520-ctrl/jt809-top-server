package cn.com.onlinetool.jt809.netty.codec.dto;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.entity.infoStatic.BaseStaticMsgEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.infoStatic.DownMsgSnInformEntity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.util.ArrayList;

/**
 * 序列信息
 */
@Getter
@Setter
@NoArgsConstructor
public class SeriesInfo {
    /**
     * 子业务数据类型 (2字节)
     */
    private int dataType;

    /**
     * 对应的子业务数据类型消息序列号 (4字节)
     */
    private long msgSn;

    /**
     * 系统UTC时间，长度为8个字节 (8字节)
     */
    private long time;

    /**
     * 构造函数
     * @param dataType 子业务数据类型
     * @param msgSn 消息序列号
     * @param time UTC时间
     */
    public SeriesInfo(int dataType, long msgSn, long time) {
        this.dataType = dataType;
        this.msgSn = msgSn;
        this.time = time;
    }

    public SeriesInfo decode(ByteBuf buf) {

        this.dataType = BufUtil.buf2UnsignedInt(buf.slice(0, 2));
        this.msgSn = BufUtil.buf2UnsignedLong(buf.slice(2, 4));
        this.time = BufUtil.buf2SignedLong(buf.slice(6, 8));

        return this;
    }

    public byte[] encode() {
        return ByteUtil.combineByteArray(
            ByteUtil.short2Bytes((short)dataType),
            ByteUtil.int2Byte((int)msgSn),
            ByteUtil.long2Bytes(time)
        );
    }
}
