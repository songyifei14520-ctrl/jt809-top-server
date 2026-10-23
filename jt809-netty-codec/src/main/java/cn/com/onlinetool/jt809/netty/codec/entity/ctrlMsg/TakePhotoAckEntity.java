package cn.com.onlinetool.jt809.netty.codec.entity.ctrlMsg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.dto.GnssData2019;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.entity.Header2019;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;

/**
 * 链路类型:主链路
 * 车辆拍照应答消息
 * 子业务类型标识:UP_CTRL_MSG_TAKE_PHOTO_ACK
 * 描述:下级平台应答上级平台发送的车辆拍照请求消息，上传图片信息到上级平台
 */
@Setter
@Getter
@NoArgsConstructor
public class TakePhotoAckEntity implements Codec<TakePhotoAckEntity> {
	private BaseCtrlMsgEntity baseMsg;

    @Field(length = 2, desc = "消息源子业务类型标识",version = {2019})
    private int sourceDataType;// 消息源子业务类型标识 byte[2]
    @Field(length = 4, desc = "源报文序列号",version = {2019})
    private long sourceMsgSn;// 源报文序列号 byte[4]

    /**
     * 拍照应答标识，标识拍照后的结果或原因,定义如下:
     * 0x00:布支持拍照相;
     * 0x01:完成拍照:
     * 0x02:完成拍照、照片数据稍后传送;
     * 0x03:未拍照(不在线);
     * 0x04:未拍照;(无法使用指定镜头);
     * 0x05:未拍照(其他原因）；
     * 0x09:车牌号码错误
     */
    private byte photoRspFlag; //拍照应答标识  byte[1]
    
    @JsonIgnore
    private byte[] gnssData; //拍照位置地点  byte[36]
    
    private byte lensId; //镜头ID  byte[1]
    private long photoLen; //图片长度  byte[4]

    /**
     * 图片大小，定义如下：
     * 0x01:320x240:
     * 0x02:640x48;
     * 0x03:800xG00;
     * 0x04:1024x76;
     * 0x05: l 76x 144[QCIF];
     * 0x06:352*288[CIF];
     * 0x07:704*288[HALF D1];
     * 0x08:704*576[D1]。
     */
    private byte size; //图片大小 byte[1]

    /**
     * 图像格式，定义如下：
     * 0x0l:jpg:
     * 0x02:gif;
     * 0x03:tiff;
     * 0x04:png.
     */
    private byte type; //图像格式  byte[1]
    private byte[] photoContent; //图片内容  byte[photoLen]

    /**
     * 获取解析后的GNSS数据对象，用于JSON序列化
     * @return 解析后的GnssData2019对象，如果解析失败则返回null
     */
    @JsonProperty("gnssData")
    public GnssData2019 getGnssDataObject() {
        if (gnssData == null || gnssData.length == 0) {
            return null;
        }
        
        try {
            return GnssData2019.decode(gnssData);
        } catch (Exception e) {
            // 如果解析失败，返回null或者可以记录日志
            return null;
        }
    }
    
    /**
     * 设置GNSS数据对象，用于JSON反序列化
     * @param gnssDataObject GnssData2019对象
     */
    @JsonProperty("gnssData")
    public void setGnssDataObject(GnssData2019 gnssDataObject) {
        if (gnssDataObject != null) {
            this.gnssData = gnssDataObject.encode();
        } else {
            this.gnssData = null;
        }
    }

	@Override
	public TakePhotoAckEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseCtrlMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.sourceDataType = BufUtil.buf2UnsignedInt(data.slice(0,2));
            this.sourceMsgSn = BufUtil.buf2UnsignedLong(data.slice(2,4));
            this.photoRspFlag = data.getByte(6);

            // 解析GNSS数据 - 根据GnssData2019结构：1字节加密标识 + 4字节数据长度 + 变长gnssData + 固定字段
            int gnssDataStartOffset = 7;
            // 读取加密标识（1字节）
            byte encrypt = data.getByte(gnssDataStartOffset);
            // 读取GNSS数据长度（4字节）
            long gnssDataLength = BufUtil.buf2UnsignedLong(data.slice(gnssDataStartOffset + 1, 4));
            // 计算完整GNSS数据块的长度：1(加密标识) + 4(数据长度) + gnssDataLength(实际数据) + 50(固定字段：11+4+11+4+11+4)
            int totalGnssBlockLength = 1 + 4 + (int)gnssDataLength + 45;
            this.gnssData = BufUtil.bufToBytes(data.slice(gnssDataStartOffset, totalGnssBlockLength));

            // 计算后续字段的偏移量
            int nextOffset = gnssDataStartOffset + totalGnssBlockLength;
            this.lensId = data.getByte(nextOffset);
            this.photoLen = BufUtil.buf2UnsignedLong(data.slice(nextOffset + 1, 4));
            this.size = data.getByte(nextOffset + 5);
            this.type = data.getByte(nextOffset + 6);
            this.photoContent = BufUtil.bufToBytes(data.slice(nextOffset + 7, data.readableBytes() - (nextOffset + 7)));
            return this;
        }

		this.photoRspFlag=baseMsg.getData().getByte(0);
        this.gnssData= BufUtil.bufToBytes(baseMsg.getData().slice(1, 36));
        this.lensId=baseMsg.getData().getByte(37);
        this.photoLen= BufUtil.buf2UnsignedLong(baseMsg.getData().slice(38, 4));
        this.size=baseMsg.getData().getByte(42);
        this.type=baseMsg.getData().getByte(43);
        this.photoContent=BufUtil.bufToBytes(baseMsg.getData().slice(44, baseMsg.getData().readableBytes() - 44));
		return this;
	}

	public static TakePhotoAckEntity create(String vehicleNo, byte vehicleColor, int sourceDataType, long sourceMsgSn, byte photoRspFlag, byte[] gnssData, byte lensId, long photoLen, byte size, byte type, byte[] photoContent) {
		TakePhotoAckEntity entity = new TakePhotoAckEntity();
        entity.sourceDataType = sourceDataType;
        entity.sourceMsgSn = sourceMsgSn;
		entity.photoRspFlag = photoRspFlag;
        entity.gnssData = gnssData;
        entity.lensId = lensId;
        entity.photoLen = photoLen;
        entity.size = size;
        entity.type = type;
        entity.photoContent = photoContent;

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_PHOTO_ACK, ByteUtil.combineByteArray(ByteUtil.short2Bytes((short)sourceDataType), ByteUtil.int2Byte((int)sourceMsgSn),
                    new byte[]{entity.photoRspFlag}, gnssData,
		                new byte[]{entity.lensId}, ByteUtil.int2Byte((int)entity.photoLen), new byte[]{entity.size}, new byte[]{entity.type}, entity.photoContent));
		return entity;
	}

    public static TakePhotoAckEntity create(String vehicleNo, byte vehicleColor, byte photoRspFlag, byte[] gnssData, byte lensId, long photoLen, byte size, byte type, byte[] photoContent) {
		TakePhotoAckEntity entity = new TakePhotoAckEntity();
		entity.photoRspFlag = photoRspFlag;
        byte[] gnssDataBytes = new byte[36];
        System.arraycopy(gnssData, 0, gnssDataBytes, 0, gnssData.length > 36 ? 36 : gnssData.length);
        entity.gnssData = gnssDataBytes;
        entity.lensId = lensId;
        entity.photoLen = photoLen;
        entity.size = size;
        entity.type = type;
        entity.photoContent = photoContent;

		entity.baseMsg = BaseCtrlMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_CTRL_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_CTRL_MSG_TAKE_PHOTO_ACK, ByteUtil.combineByteArray(new byte[]{entity.photoRspFlag}, gnssDataBytes,
		                new byte[]{entity.lensId}, ByteUtil.int2Byte((int)entity.photoLen), new byte[]{entity.size}, new byte[]{entity.type}, entity.photoContent));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}


}
