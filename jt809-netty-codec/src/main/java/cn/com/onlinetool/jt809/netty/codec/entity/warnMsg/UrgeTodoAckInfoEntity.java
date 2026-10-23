package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 协议版本 2019
 * 链路类型:主链路 上报报警督办应答消息 子业务类型标识:UP_WARN_MSG_URGE_TODO_ACK_INFO
 * 描述:下级平台上报对上级平台下发的报警督办请求消息的处理结果
 * 表66报警督办应答消息数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class UrgeTodoAckInfoEntity implements Codec<UrgeTodoAckInfoEntity> {
	private BaseWarnMsgEntity baseMsg;

    @Field(length = 2, desc = "对应报警督办请求消息源子业务类型标识",version = {2019})
    private int sourceDataType; // 对应报警督办请求消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "对应报警督办请求消息源报文序列号",version = {2019})
    private long sourceMsgSn; // 对应报警督办请求消息源报文序列号 byte[4]

	/**
	 * 报警处理结果，定义如下：
	 * 0x00：处理中
	 * 0x01：已处理完毕
	 * 0x02：不做处理
	 * 0x03：将来处理
	 */
	private byte result; // byte[1]

	@Override
	public UrgeTodoAckInfoEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseWarnMsgEntity().decode(buf);

		ByteBuf data = baseMsg.getData();
		// 解析对应报警督办请求消息源子业务类型标识 (2字节)
		this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0, 2));
		// 解析对应报警督办请求消息源报文序列号 (4字节)
		this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2, 4));
		// 解析报警处理结果 (1字节)
		this.result = data.getByte(6);

		return this;
	}

	public static UrgeTodoAckInfoEntity create(String vehicleNo, byte vehicleColor,
			int sourceDataType, long sourceMsgSn, byte result) {
		UrgeTodoAckInfoEntity entity = new UrgeTodoAckInfoEntity();

		// 设置字段值
		entity.sourceDataType = sourceDataType;
		entity.sourceMsgSn = sourceMsgSn;
		entity.result = result;

		// 构建数据部分：sourceDataType(2) + sourceMsgSn(4) + result(1) = 7字节
		byte[] data = ByteUtil.combineByteArray(
				ByteUtil.short2Bytes((short) sourceDataType),  // 对应报警督办请求消息源子业务类型标识 2字节
				ByteUtil.int2Byte((int) sourceMsgSn),          // 对应报警督办请求消息源报文序列号 4字节
				new byte[] { result }                          // 报警处理结果 1字节
		);

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.UP_WARN_MSG,MsgConstant.SubServiceType.UP_WARN_MSG_URGE_TODO_ACK_INFO, data);
		return entity;
	}


	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
