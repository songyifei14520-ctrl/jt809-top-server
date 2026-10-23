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
 * 链路类型:主链路 上报报警信息消息 子业务类型标识:UP_WARN_MSG_ADPT_INFO
 * 描述:下级平台向上级平台上报某车辆的报警信息,本条消息上级平台无需应答
 */
@Setter
@Getter
@NoArgsConstructor
public class AdptInfoEntity implements Codec<AdptInfoEntity> {
	private BaseWarnMsgEntity baseMsg;

    @Field(length = 11, desc = "报警平台唯一编码",version = {2019})
    private String platformId;//发起报警平台唯一编码，由平台所在地行政区域代码和平台编号组成

	/**
	 * 报警信息来源，定义如下： 0x01：车载终端 0x02：企业监控平台 0x03：政府监管平台 0x09：其他
	 */
    @Field(length = 1, desc = "报警信息来源",version = {2011})
	private byte warnSrc; // 报警信息来源 byte[1]
    @Field(length = 2, desc = "报警类型",version = {2011,2019})
	private int warnType; // 报警类型 byte[2]
    @Field(length = 8, desc = "报警时间",version = {2011})
	private long warnTime; // 报警时间 byte[8]

    @Field(length = 8, desc = "事件开始时间",version = {2019})
    private long startTime;//事件开始时间，用UTC时间表示
    @Field(length = 8, desc = "事件结束时间",version = {2019})
    private long endTime;//事件结束时间，用UTC时间表示

    @Field(length = 21, desc = "车牌号,长度data_length",version = {2019})
    private String vehicleNo; // 车牌号 byte[21]
    @Field(length = 1, desc = "车辆颜色",version = {2019})
    private byte vehicleColor; // 车辆颜色 byte[1]

    @Field(length = 11, desc = "发起报警平台唯一编码",version = {2019})
    private String srcPlatformId;//发起报警平台唯一编码，由平台所在地行政区域代码和平台编号组成

    @Field(length = 4, desc = "信息ID",version = {2011,2019})
	private long infoId; // 信息ID byte[4]
    @Field(length = 4, desc = "报警信息长度",version = {2011,2019})
	private long infoLength; // 报警信息长度，最长 1024 字节 byte[4]

    @Field(length = 4, desc = "线路ID",version = {2019})
    private long drvLineId;

	private String infoContent; // 上报报警信息内容， byte[infoLength]

	@Override
	public AdptInfoEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseWarnMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();
            this.platformId = new String(BufUtil.bufToBytes(data.slice(0, 11)), Charset.forName("GBK")).trim();
            this.warnType = BufUtil.buf2UnsignedInt(data.slice(11, 2));
            this.startTime = BufUtil.buf2SignedLong(data.slice(13, 8));
            this.endTime = BufUtil.buf2SignedLong(data.slice(21, 8));
            this.vehicleNo = new String(BufUtil.bufToBytes(data.slice(29, 21)), Charset.forName("GBK")).trim();
            this.vehicleColor = data.getByte(50);
            this.srcPlatformId = new String(BufUtil.bufToBytes(data.slice(51, 11)), Charset.forName("GBK")).trim();
            this.drvLineId = BufUtil.buf2UnsignedLong(data.slice(62, 4));
            this.infoLength = BufUtil.buf2UnsignedLong(data.slice(66, 4));
            this.infoContent = new String(BufUtil.bufToBytes(data.slice(70, data.readableBytes() - 70)), Charset.forName("GBK"));
            return this;
        }

        //2011版本协议
		this.warnSrc = baseMsg.getData().getByte(0);
		this.warnType = BufUtil.buf2UnsignedInt(baseMsg.getData().slice(1, 2));
		this.warnTime = BufUtil.buf2SignedLong(baseMsg.getData().slice(3, 8));
		this.infoId = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(11, 4));
		this.infoLength = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(15, 4));
		this.infoContent = new String(
				BufUtil.bufToBytes(baseMsg.getData().slice(19, baseMsg.getData().readableBytes() - 19)));
		return this;
	}

	public static AdptInfoEntity create(String vehicleNo, byte vehicleColor, byte warnSrc, int warnType, long warnTime,
			long infoId, String infoContent) {
		AdptInfoEntity entity = new AdptInfoEntity();
		entity.warnSrc = warnSrc;
		entity.warnType = warnType;
		entity.warnTime = warnTime;
		entity.infoId = infoId;
		byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;
		entity.infoContent = infoContent;

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.UP_WARN_MSG,vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_WARN_MSG_ADPT_INFO,
				ByteUtil.combineByteArray(new byte[] { entity.warnSrc }, ByteUtil.short2Bytes((short) entity.warnType),
						ByteUtil.long2Bytes(entity.warnTime), ByteUtil.int2Byte((int) entity.infoId),
						ByteUtil.int2Byte((int) entity.infoLength), infoContentBytes));
		return entity;
	}

	/**
	 * 创建报警督办信息消息实体（2019协议版本）
	 * @param platformId 平台唯一编码
	 * @param warnType 报警类型
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param vehicleNo 车牌号
	 * @param vehicleColor 车辆颜色
	 * @param srcPlatformId 源平台唯一编码
	 * @param infoId 信息ID
	 * @param infoContent 信息内容
	 * @return AdptInfoEntity实例
	 */
	public static AdptInfoEntity create(String platformId, int warnType, long startTime, long endTime,
									   String vehicleNo, byte vehicleColor, String srcPlatformId,
									   long infoId, String infoContent,long warnTime,long drvLineId) {
		AdptInfoEntity entity = new AdptInfoEntity();
		entity.platformId = platformId;
		entity.warnType = warnType;
        entity.warnTime = warnTime;
		entity.startTime = startTime;
		entity.endTime = endTime;
		entity.vehicleNo = vehicleNo;
		entity.vehicleColor = vehicleColor;
		entity.srcPlatformId = srcPlatformId;
        entity.drvLineId = drvLineId;
		entity.infoContent = infoContent;

		byte[] infoContentBytes = infoContent.getBytes(Charset.forName("GBK"));
		entity.infoLength = infoContentBytes.length;


		// 构建2019协议数据部分
		byte[] platformIdBytes = platformId.getBytes(Charset.forName("GBK"));
        byte[] srcPlatformIdBytes = "0".equals(srcPlatformId)?new byte[11]:srcPlatformId.getBytes(Charset.forName("GBK"));
		byte[] vehicleNoBytes = vehicleNo.getBytes(Charset.forName("GBK"));

		// 确保字段长度正确
		byte[] platformIdFixed = new byte[11];
		System.arraycopy(platformIdBytes, 0, platformIdFixed, 0, Math.min(platformIdBytes.length, 11));

		byte[] vehicleNoFixed = new byte[21];
		System.arraycopy(vehicleNoBytes, 0, vehicleNoFixed, 0, Math.min(vehicleNoBytes.length, 21));

		byte[] srcPlatformIdFixed = new byte[11];
		if (srcPlatformId != null) {
			System.arraycopy(srcPlatformIdBytes, 0, srcPlatformIdFixed, 0, Math.min(srcPlatformIdBytes.length, 11));
		}

		byte[] data = ByteUtil.combineByteArray(
			platformIdFixed,                                    // 平台唯一编码 11字节
			ByteUtil.short2Bytes((short) warnType),            // 报警类型 2字节
            ByteUtil.long2Bytes(warnTime),
			ByteUtil.long2Bytes(startTime),                    // 开始时间 8字节
			ByteUtil.long2Bytes(endTime),                      // 结束时间 8字节
			vehicleNoFixed,                                    // 车牌号 21字节
			new byte[]{vehicleColor},                          // 车辆颜色 1字节
			srcPlatformIdFixed,                                // 源平台唯一编码 11字节
            ByteUtil.int2Byte((int)drvLineId),                    // 信息ID 4字节
			ByteUtil.int2Byte((int) entity.infoLength),        // 信息长度 4字节
			infoContentBytes                                   // 信息内容
		);

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.UP_WARN_MSG, MsgConstant.SubServiceType.UP_WARN_MSG_ADPT_INFO, data);
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
