package cn.com.onlinetool.jt809.netty.codec.entity.infoStatic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

@Setter
@Getter
@NoArgsConstructor
public class BaseStaticMsgEntity implements Codec<BaseStaticMsgEntity> {

	private BaseEntity base;

	private int dataType; // 子业务类型标识 byte[2]
	private long dataLength; // 后续数据长度 byte[4]

    @JsonIgnore
	private ByteBuf data; // 数据部分 byte[dataLength]

	@Override
	public BaseStaticMsgEntity decode(ByteBuf buf) {
		this.base = new BaseEntity().decode(buf);

		ByteBuf msgbody = this.base.getMessageBody();

		this.dataType = BufUtil.buf2UnsignedInt(msgbody.slice(0, 2));
		this.dataLength = BufUtil.buf2UnsignedLong(msgbody.slice(2, 4));
		this.data = msgbody.slice(6, msgbody.readableBytes() - 6);

		return this;
	}

	public static BaseStaticMsgEntity create( int dataType, byte[] data) {
		BaseStaticMsgEntity baseMsg = new BaseStaticMsgEntity();

		baseMsg.dataType = dataType;
		baseMsg.dataLength = data.length;

		byte[] msgbody = new byte[ 2 + 4 + (int) baseMsg.dataLength];

		System.arraycopy(ByteUtil.short2Bytes((short) dataType), 0, msgbody, 0, 2);
		System.arraycopy(ByteUtil.int2Byte((int) baseMsg.dataLength), 0, msgbody, 2, 4);
		System.arraycopy(data, 0, msgbody, 6, (int) baseMsg.dataLength);

		baseMsg.base = BaseEntity.create(dataType, msgbody);
		return baseMsg;
	}

	@Override
	public byte[] encode() {
		return base.encode();
	}
}
