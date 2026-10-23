package cn.com.onlinetool.jt809.netty.codec.entity.linkManager;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;

/**
 * 上级平台主动关闭主从链路通知消息
 * 业务数据类型标识: DOWN_CLOSELINK_INFORM
 * 用于通知下级平台主从链路关闭，无需应答
 */
@Getter
@Setter
@NoArgsConstructor
public class SubCloseLinkInformEntity implements Codec<SubCloseLinkInformEntity> {

    private BaseEntity base;

    /**
     * 链路关闭原因
     * 0x00: 网关重启
     * 0xFF: 其他
     */
    private byte reasonCode;

    @Override
    public SubCloseLinkInformEntity decode(ByteBuf buf) {
        this.base = new BaseEntity().decode(buf);
        ByteBuf msgBody = this.base.getMessageBody();
        this.reasonCode = msgBody.readByte();
        return this;
    }

    public static SubCloseLinkInformEntity create(byte reasonCode) {
        SubCloseLinkInformEntity entity = new SubCloseLinkInformEntity();
        entity.reasonCode = reasonCode;
        entity.base = BaseEntity.create(
                MsgConstant.PrimaryServiceType.DOWN_CLOSELINK_INFORM,
                new byte[]{reasonCode}
        );
        return entity;
    }

    @Override
    public byte[] encode() {
        return base.encode();
    }
}
