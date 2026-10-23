package cn.com.onlinetool.jt809.netty.codec.entity.ctrlMsg;

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
 * 链路类型:主链路 下发车辆报文应答消息 子业务类型标识:UP_CTRL_MSG_TEXT_INFO_ACK
 * 描述:下级平台应答上级平台下发的报文是否成功到达指定车辆
 */
@Setter
@Getter
@NoArgsConstructor
public class TextInfoAckEntity implements Codec<TextInfoAckEntity> {
	private BaseCtrlMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

    @Field(length = 4, desc = "MSG_ID",version = {2011})
	private long msgSequence; // 对应“下发车辆报文请求消息”中的MSG_ID byte[4]
	/**
	 * 下发车辆报文应答结果，定义如下： 0x00：下发成功 0x01：下发失败
	 */
	private byte result; // 下发车辆报文应答结果 byte[1]

	@Override
	public TextInfoAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseCtrlMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0,2));
            this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2,4));
            // msgSequence字段在2019版本中不存在，因为它标记为version={2011}
            this.result = data.getByte(6);
            return this;
        }

        //2011版本协议
		this.msgSequence = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(0, 4));
		this.result = baseMsg.getData().getByte(4);
		return this;
	}

	public static TextInfoAckEntity create(String vehicleNo, byte vehicleColor, int sourceDataType, long sourceMsgSn, byte result) {
		TextInfoAckEntity entity = new TextInfoAckEntity();
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
		entity.result = result;
        // msgSequence字段在2019版本中不存在，因为它标记为version={2011}

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_TEXT_INFO_ACK,
				ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn),
                    new byte[] { entity.result }));
		return entity;
	}

    public static TextInfoAckEntity create(String vehicleNo, byte vehicleColor, long msgSequence, byte result) {
		TextInfoAckEntity entity = new TextInfoAckEntity();
		entity.msgSequence = msgSequence;
		entity.result = result;

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_TEXT_INFO_ACK,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.msgSequence), new byte[] { entity.result }));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
