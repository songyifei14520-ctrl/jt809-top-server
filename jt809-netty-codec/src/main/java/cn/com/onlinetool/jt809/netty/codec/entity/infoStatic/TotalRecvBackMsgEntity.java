package cn.com.onlinetool.jt809.netty.codec.entity.infoStatic;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 接收车辆定位信息数量通知消息 业务数据类型标识: UP_MANAGE_TOTAL_RECV_BACK_MSG
 * 描述：下级平台向上级平台定时通知已经收到的车辆定位信息数量
 */
@Setter
@Getter
@NoArgsConstructor
public class TotalRecvBackMsgEntity implements Codec<TotalRecvBackMsgEntity> {

	private BaseEntity base;

	private long dynamicInfoTotal; // 定位信息数量 byte[4]
	private long startTime; // 开始时间 byte[8]
	private long endTime; // 结束时间 byte[8]


	@Override
	public TotalRecvBackMsgEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();
		this.dynamicInfoTotal = BufUtil.buf2UnsignedLong(msgbody.slice(0, 4));
		this.startTime = BufUtil.buf2SignedLong(msgbody.slice(4, 8));
		this.endTime = BufUtil.buf2SignedLong(msgbody.slice(12, 8));

		return this;
	}

	public static TotalRecvBackMsgEntity create(long dynamicInfoTotal,
			long startTime, long endTime) {
		TotalRecvBackMsgEntity baseMsg = new TotalRecvBackMsgEntity();

		baseMsg.dynamicInfoTotal = dynamicInfoTotal;
		baseMsg.startTime = startTime;
		baseMsg.endTime = endTime;

		byte[] msgbody = ByteUtil.combineByteArray(ByteUtil.int2Byte((int) baseMsg.dynamicInfoTotal),
				ByteUtil.long2Bytes(baseMsg.startTime), ByteUtil.long2Bytes(baseMsg.endTime));

		baseMsg.base = BaseEntity.create(MsgConstant.PrimaryServiceType.UP_MANAGE_TOTAL_RECV_BACK_MSG, msgbody);
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}

}
