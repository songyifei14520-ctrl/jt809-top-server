package cn.com.onlinetool.jt809.netty.codec.entity.platformMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 链路类型:主链路 下发平台间报文应答消息 子业务类型标识:UP_PLATFORM_MSG_INFO_ACK
 * 描述:下级平台收到上级平台发送的下发平台间报文请求消息后，发送应答消息
 */
@Setter
@Getter
@NoArgsConstructor
public class InfoAckEntity implements Codec<InfoAckEntity> {
	private BasePlatFormMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

    @Field(length = 4, desc = "信息ID",version = {2011})
	private long infoId; // 信息ID，本ID跟下发的ID相同 byte[4]

	@Override
	public InfoAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BasePlatFormMsgEntity().decode(buf);

		//2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            if (data.readableBytes() >= 6) {
                this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0, 2));
                this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2, 4));
                // infoId字段标记为version={2011}，在2019协议中排除
            }
            return this;
        }

        //2011版本协议
		this.infoId = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(0, 4));
		return this;
	}

	public static InfoAckEntity create(int sourceDataType, long sourceMsgSn) {
		InfoAckEntity entity = new InfoAckEntity();
		entity.sourceDataType = sourceDataType;
		entity.sourceMsgSn = sourceMsgSn;
		// infoId字段标记为version={2011}，在2019协议中排除

		entity.baseMsg = BasePlatFormMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
            MsgConstant.SubServiceType.UP_PLATFORM_MSG_INFO_ACK,
				ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn)));
		return entity;
	}

	public static InfoAckEntity create(long infoId) {
		InfoAckEntity entity = new InfoAckEntity();
		entity.infoId = infoId;

		entity.baseMsg = BasePlatFormMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_PLATFORM_MSG,
            MsgConstant.SubServiceType.UP_PLATFORM_MSG_INFO_ACK,
				ByteUtil.int2Byte((int) infoId));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
