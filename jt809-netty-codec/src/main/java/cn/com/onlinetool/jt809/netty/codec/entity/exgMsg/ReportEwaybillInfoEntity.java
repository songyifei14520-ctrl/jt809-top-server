package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路 上报车辆电子运单应答消息 子业务类型标识:UP_EXG_MSG_REPORT_EWAYBILL_INFO
 * 描述:下级平台“主动上报车辆电子运单信息”，向上级平台上报车辆当前电子运单。
 */
@Setter
@Getter
@NoArgsConstructor
public class ReportEwaybillInfoEntity implements Codec<ReportEwaybillInfoEntity> {
	private BaseExgMsgEntity baseMsg;

	private long ewaybillLength; // 电子运单数据体长度 byte[4]
	private String ewaybillInfo; // 电子运单数据内容 byte[ewaybillLength]

	@Override
	public ReportEwaybillInfoEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);
		this.ewaybillLength = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(0, 4));
		this.ewaybillInfo = new String(
				BufUtil.bufToBytes(baseMsg.getData().slice(4, baseMsg.getData().readableBytes() - 4)),
				Charset.forName("GBK"));
		return this;
	}

	public static ReportEwaybillInfoEntity create(String vehicleNo, byte vehicleColor, String ewaybillInfo) {
		ReportEwaybillInfoEntity entity = new ReportEwaybillInfoEntity();
		entity.ewaybillInfo = ewaybillInfo;
        entity.ewaybillLength = ewaybillInfo.length();

		byte[] ewaybillInfoBytes = ewaybillInfo.getBytes(Charset.forName("GBK"));

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_REPORT_EWAYBILL_INFO,
				ByteUtil.combineByteArray(ByteUtil.int2Byte((int) entity.ewaybillLength), ewaybillInfoBytes));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
