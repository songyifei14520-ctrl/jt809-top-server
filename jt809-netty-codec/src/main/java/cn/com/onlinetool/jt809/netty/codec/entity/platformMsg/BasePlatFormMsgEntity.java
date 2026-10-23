package cn.com.onlinetool.jt809.netty.codec.entity.platformMsg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;


/**
 * 主链路平台间信息交互业务 链路类型:主链路 消息方向:下级平台往上级平台 业务数据类型标识:UP_PLATFORM_MSG
 * 描述:下级平台向上级平台发送平台间交互信息
 * <p>
 * <p>
 * 从链路平台间信息交互业务 链路类型:从链路 消息方向:上级平台往下级平台 业务数据类型标识:DOWN_PLATFORM_MSG
 * 描述:上级平台向下级平台发送平台问交互信息
 */
@Setter
@Getter
@NoArgsConstructor
public class BasePlatFormMsgEntity implements Codec<BasePlatFormMsgEntity> {

    private BaseEntity base;

    private int dataType; // 子业务类型标识 byte[2]
    private long dataLength; // 后续数据长度 byte[4]

    @JsonIgnore
    private ByteBuf data; // 数据部分 byte[dataLength]

    @Override
    public BasePlatFormMsgEntity decode(ByteBuf buf) {
        this.base = new BaseEntity().decode(buf);

        ByteBuf msgbody = this.base.getMessageBody();
        this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(0, 2));
        this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(2, 4));
        this.data = msgbody.slice(6, msgbody.readableBytes() - 6);

        return this;
    }

    public static BasePlatFormMsgEntity create(int primaryDataType,int dataType, byte[] data) {
        BasePlatFormMsgEntity baseMsg = new BasePlatFormMsgEntity();
        baseMsg.dataType = dataType;
        baseMsg.dataLength = data.length;

        byte[] msgbody = new byte[2 + 4 + (int) baseMsg.dataLength];
        System.arraycopy(ByteUtil.short2Bytes((short) baseMsg.dataType), 0, msgbody, 0, 2);
        System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, 2, 4);
        System.arraycopy(data, 0, msgbody, 6, (int) baseMsg.dataLength);

        baseMsg.base = BaseEntity.create(primaryDataType, msgbody);
        return baseMsg;
    }

    @Override
    public byte[] encode() {
        return base.encode();
    }

}

