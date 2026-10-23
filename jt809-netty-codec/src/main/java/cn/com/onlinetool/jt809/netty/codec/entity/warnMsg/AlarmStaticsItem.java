package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.io.Serializable;

/**
 * 依据: DB32/T 3610.3—2025 表 55 报警统计核查请求应答数据体 - LIST 报警统计列表项
 * 用于智能监控报警统计核查请求应答 (0x1406)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmStaticsItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 报警类型 uint16_t (2字节)
     * 详见常量定义中的报警类型 (表 60 车辆报警类型编码表)
     */
    @Field(length = 2, desc = "报警类型(表60)", version = {2025})
    private int warnType;

    /**
     * 报警数量 uint32_t (4字节)
     */
    @Field(length = 4, desc = "报警数量", version = {2025})
    private long statics;

    /**
     * 编码为 6 字节二进制数据 (2字节 warnType + 4字节 statics)
     */
    public byte[] encode() {
        return ByteUtil.combineByteArray(
                ByteUtil.short2Bytes((short) warnType),
                ByteUtil.int2Byte((int) statics)
        );
    }

    /**
     * 从 ByteBuf 中解码单项 (固定 6 字节)
     */
    public static AlarmStaticsItem decode(ByteBuf buf) {
        if (buf == null || buf.readableBytes() < 6) {
            return null;
        }
        int warnType = buf.readUnsignedShort();
        long statics = buf.readUnsignedInt();
        return new AlarmStaticsItem(warnType, statics);
    }
}
