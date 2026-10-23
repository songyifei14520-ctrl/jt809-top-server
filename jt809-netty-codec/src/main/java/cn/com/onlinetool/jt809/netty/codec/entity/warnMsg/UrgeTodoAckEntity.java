package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

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

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 报警督办应答消息 子业务类型标识:UP_WARN_MSG_URGE_TODO_ACK
 * 描述:下级平台应答上级平台下发的报警督办请求消息，向上级平台上报车辆的报瞥处理结果
 */
@Setter
@Getter
@NoArgsConstructor
public class UrgeTodoAckEntity implements Codec<UrgeTodoAckEntity> {
	private BaseWarnMsgEntity baseMsg;

    @Field(length = 4, desc = "报警督办ID",version = {2011})
	private long supervisionId; // 报警督办ID byte[4]
	/**
	 * 报警处理结果，定义如下： 0x00：处理中 0x01：已处理完毕 0x02：不做处理 0x03：将来处理
	 */
	private byte result; // 报警督办结果 byte[1]

    @Field(length = 4, desc = "对应报警督办请求消息报文序列号",version = {2019})
    private int msgSn; //对应报警督办请求消息报文序列号 byte[4]

    @Field(length = 2, desc = "对应报警督办请求消息源子业务类型标识",version = {2019})
    private int sourceDataType; // 对应报警督办请求消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "对应报警督办请求消息源报文序列号",version = {2019})
    private long sourceMsgSn; // 对应报警督办请求消息源报文序列号 byte[4]

	@Override
	public UrgeTodoAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseWarnMsgEntity().decodeWarnSuperviseInfo(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            if (data.readableBytes() >= 11) {
                this.msgSn = (int) BufUtil.buf2UnsignedLong(data.slice(0, 4));
                this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(4, 2));
                this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(6, 4));
                this.result = data.getByte(10);
            }
            return this;
        }

        //2011版本协议
		this.supervisionId = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(0, 4));
		this.result = baseMsg.getData().getByte(4);
		return this;
	}

	// 2019版本协议create方法
	public static UrgeTodoAckEntity create(int sourceDataType, long sourceMsgSn,int msgSn, byte result) {
		UrgeTodoAckEntity entity = new UrgeTodoAckEntity();
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
        entity.msgSn = msgSn;
        entity.result = result;

		entity.baseMsg = BaseWarnMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_WARN_MSG,
				MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_ACK,
                ByteUtil.combineByteArray(
                    ByteUtil.int2Byte(msgSn),
                    ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn), new byte[] { result }));
		return entity;
	}

	// 2011版本协议create方法
	public static UrgeTodoAckEntity create(String vehicleNo, byte vehicleColor, long supervisionId, byte result) {
		UrgeTodoAckEntity entity = new UrgeTodoAckEntity();
		entity.supervisionId = supervisionId;
		entity.result = result;

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.UP_WARN_MSG,vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_ACK,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.supervisionId), new byte[] { entity.result }));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
