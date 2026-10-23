package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

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
 * 链路类型:主链路 启动车辆定位信息交换应答消息 子业务类型标识:UP_EXG_MSG_RETURN_STARTUP_ACK
 * 描述:本条消息是下级平台对上级平台下发的 DOWN_EXG_MSG_RETURN_STARTUP 消息的应答消息
 */
@Setter
@Getter
@NoArgsConstructor
public class ReturnStartupAckEntity implements Codec<ReturnStartupAckEntity> {
	private BaseExgMsgEntity baseMsg;

    @Field(length = 2, desc = "对应启动车辆定位信息交换请求消息源子业务类型标识",version = {2019})
    private int sourceDataType; // 对应启动车辆定位信息交换请求消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "对应启动车辆定位信息交换请求消息源报文序列号",version = {2019})
    private long sourceMsgSn; // 对应启动车辆定位信息交换请求消息源报文序列号 byte[4]
    @Field(length = 4, desc = "后续数据长度",version = {2019})
    private long dataLength; // 后续数据长度 值为0x00 byte[4]

	@Override
	public ReturnStartupAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().withSorceSnDecode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {

            this.sourceDataType = baseMsg.getSourceDataType();
            this.sourceMsgSn = baseMsg.getSourceMsgSn();
            this.dataLength = baseMsg.getDataLength();

            return this;
        }

        //2011版本协议 - 无额外字段
		return this;
	}

	public static ReturnStartupAckEntity create(String vehicleNo, byte vehicleColor, int sourceDataType, long sourceMsgSn) {
		ReturnStartupAckEntity entity = new ReturnStartupAckEntity();
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
        entity.dataLength = 0; // 后续数据长度值为0x00

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_RETURN_STARTUP_ACK,
                sourceDataType, sourceMsgSn);
		return entity;
	}

    public static ReturnStartupAckEntity create(String vehicleNo, byte vehicleColor) {
		ReturnStartupAckEntity entity = new ReturnStartupAckEntity();

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_RETURN_STARTUP_ACK, new byte[0]);
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
