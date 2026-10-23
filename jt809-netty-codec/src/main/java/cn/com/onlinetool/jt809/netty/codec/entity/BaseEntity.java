package cn.com.onlinetool.jt809.netty.codec.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.enums.EncryptFlagEnum;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.JT809Util;


@Slf4j
@Data
@NoArgsConstructor
public class BaseEntity implements Codec<BaseEntity> {

	/**
	 * |头标识|消息头|消息体|校验码|尾标识|
	 */
	private byte headFlag = MsgConstant.HEAD_FLAG; // 头标识 byte

	private Header header; // 数据头 byte[22]

    @JsonIgnore
	private ByteBuf messageBody; // 解密后的数据体

	private byte[] crcCode;// CRC校验码 byte[2]

	private byte tailFlag = MsgConstant.TAIL_FLAG;;// 尾标识 byte

	@Override
	public BaseEntity decode(ByteBuf buf) {
		//this.headFlag = buf.readByte();
        this.headFlag = buf.getByte(0);
		//ByteBuf decodedBuf = JT809Util.decode(buf.slice(1, buf.readableBytes() - 2));
        if(JT809Config.isJT809_2019()){
            this.header = new Header2019().decode(buf);
        }else {
            this.header = new Header2011().decode(buf);
        }

        if(buf.readableBytes() - 3 - 1 > this.header.getHeaderLength()){
            ByteBuf bodyTmp = buf.slice(this.header.getHeaderLength()+1, buf.readableBytes() - 3 - this.header.getHeaderLength() -1);
            log.debug("解密前：bodyTmp length is {}, buf is {}", bodyTmp.readableBytes(),buf.readableBytes());
            this.messageBody = this.header.getEncryptFlag() == EncryptFlagEnum.YES.getValue()
                ? BufUtil.bytesToBuf(JT809Util.encrypt(this.header.getEncryptKey(), BufUtil.bufToBytes(bodyTmp)))
                : bodyTmp;
        }

		this.crcCode = BufUtil.bufToBytes(buf.slice(buf.readableBytes() - 3, 2));
		return this;
	}

	public static BaseEntity create(int msgId, byte[] msgbody) {
		BaseEntity base = new BaseEntity();
        if(JT809Config.isJT809_2019()){
            base.header = Header2019.create(msgId, msgbody.length);
        }else {
            base.header = Header2011.create(msgId, msgbody.length);
        }

		base.messageBody = BufUtil.bytesToBuf(msgbody);
		base.crcCode = JT809Util.createCrcCode(base.header, msgbody);
		return base;
	}

	@Override
	public byte[] encode() {
		byte[] bodyBytes = BufUtil.bufToBytes(messageBody);

		// 如果需要加密，则先对消息体进行加密
		if (header.getEncryptFlag() == EncryptFlagEnum.YES.getValue()) {
			bodyBytes = JT809Util.encrypt(header.getEncryptKey(), bodyBytes);
			// 对加密后的消息体重新计算CRC
			this.crcCode = JT809Util.createCrcCode(header, bodyBytes);
		}

		byte[] body = ByteUtil.combineByteArray(header.encode(), bodyBytes, crcCode);
		byte[] result = ByteUtil.combineByteArray(new byte[] { headFlag }, body, new byte[] { tailFlag });

		byte[] encodedBytes = JT809Util.encode(result);

		return encodedBytes;
	}

    private int subDataType;
    public int getSubDataType() {
        if(messageBody == null || messageBody.readableBytes() < 24){
            return 0;
        }
        messageBody.markReaderIndex();
        messageBody.skipBytes(22);
        byte[] bytes = new byte[2];
        messageBody.readBytes(bytes);
        int dataType = (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
        messageBody.resetReaderIndex();
        return dataType;
    }
}
