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
 * 链路类型:从链路
 * 报警预警消息
 * 子业务类型标识:DOWN_WARN_MSG_EXG_INFORM
 * 描述:用于上级平台向车辆跨域目的地下级平台下发相关车辆的当前报警情况,本条消息下级平台无需应答
 */
@Setter
@Getter
@NoArgsConstructor
public class ExgInformEntity implements Codec<ExgInformEntity> {
	private BaseWarnMsgEntity baseMsg;

    @Field(length = 11, desc = "报警平台唯一编码",version = {2019})
    private String platformId; // 发起报警平台唯一编码，由平台所在地行政区域代码和平台编号组成

    @Field(length = 1, desc = "报警信息来源",version = {2011})
    private byte warnSrc; // 报警信息来源 byte[1]
    @Field(length = 2, desc = "报警类型",version = {2011,2019})
    private int warnType; // 报警类型 byte[2]
    @Field(length = 8, desc = "报警时间",version = {2011})
    private long warnTime; // 报警时间 byte[8] UTC 时间格式

    @Field(length = 8, desc = "事件开始时间",version = {2019})
    private long startTime; // 事件开始时间，用UTC时间表示 byte[8]
    @Field(length = 8, desc = "事件结束时间",version = {2019})
    private long endTime; // 事件结束时间，用UTC时间表示 byte[8]

    @Field(length = 21, desc = "车牌号",version = {2019})
    private String vehicleNo; // 车牌号 byte[21]
    @Field(length = 1, desc = "车辆颜色",version = {2019})
    private byte vehicleColor; // 车辆颜色 byte[1]
    @Field(length = 11, desc = "发起报警平台唯一编码",version = {2019})
    private byte[] srcPlatformId; // 发起报警平台唯一编码，由平台所在地行政区域代码和平台编号组成 byte[11]
    @Field(length = 4, desc = "线路ID",version = {2019})
    private long drvLineId; // 线路ID byte[4]

    private long warnLength; //报警信息长度  byte[4]
    private String warnContent; //报警描述，信息提示  byte[warnLength]


    @Override
	public ExgInformEntity decode(ByteBuf buf) {
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
            this.srcPlatformId = BufUtil.bufToBytes(data.slice(51, 11));
            this.drvLineId = BufUtil.buf2UnsignedLong(data.slice(62, 4));
            this.warnLength = BufUtil.buf2UnsignedLong(data.slice(66, 4));
            this.warnContent = new String(BufUtil.bufToBytes(data.slice(70, data.readableBytes() - 70)), Charset.forName("GBK"));
            return this;
        }

        //2011版本协议
		this.warnSrc = baseMsg.getData().getByte(0);
		this.warnType = BufUtil.buf2UnsignedInt(baseMsg.getData().slice(1, 2));
		this.warnTime = BufUtil.buf2SignedLong(baseMsg.getData().slice(3, 8));
        this.warnLength = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(11, 4));
        this.warnContent = new String(BufUtil.bufToBytes(baseMsg.getData().slice(15, baseMsg.getData().readableBytes() - 15)), Charset.forName("GBK"));
		return this;
	}

	public static ExgInformEntity create(String vehicleNo, byte vehicleColor, byte warnSrc, int warnType,
            long warnTime, String warnContent) {
		ExgInformEntity entity = new ExgInformEntity();
		entity.warnSrc = warnSrc;
		entity.warnType = warnType;
		entity.warnTime = warnTime;
		byte[] warnContentBytes = warnContent.getBytes(Charset.forName("GBK"));

		entity.warnLength = warnContentBytes.length;
		entity.warnContent = warnContent;

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.DOWN_WARN_MSG,vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_WARN_MSG_EXG_INFORM, ByteUtil.combineByteArray(new byte[]{entity.warnSrc}, ByteUtil.short2Bytes((short) entity.warnType),
		                ByteUtil.long2Bytes(entity.warnTime), ByteUtil.int2Byte((int)entity.warnLength), warnContentBytes));
		return entity;
	}

    /**
     * 创建报警预警消息实体（2019协议版本）
     * @param platformId 报警平台唯一编码
     * @param warnType 报警类型
     * @param startTime 事件开始时间
     * @param endTime 事件结束时间
     * @param vehicleNoParam 车牌号码
     * @param vehicleColorParam 车牌颜色
     * @param srcPlatformId 发起报警平台唯一编码
     * @param drvLineId 线路ID
     * @param warnContent 报警描述，信息提示
     * @return ExgInformEntity实例
     */
    public static ExgInformEntity create(String platformId, int warnType,
                                        long startTime, long endTime, String vehicleNoParam, byte vehicleColorParam,
                                        byte[] srcPlatformId, long drvLineId, String warnContent, long warnTime) {
        ExgInformEntity entity = new ExgInformEntity();

        // 设置2019协议字段
        entity.platformId = platformId;
        entity.warnType = warnType;
        entity.warnTime = warnTime;
        entity.startTime = startTime;
        entity.endTime = endTime;
        entity.vehicleNo = vehicleNoParam;
        entity.vehicleColor = vehicleColorParam;
        entity.srcPlatformId = srcPlatformId;
        entity.drvLineId = drvLineId;
        entity.warnContent = warnContent;

        byte[] warnContentBytes = warnContent.getBytes(Charset.forName("GBK"));
        entity.warnLength = warnContentBytes.length;

        // 构建2019协议数据部分
        byte[] platformIdBytes = platformId.getBytes(Charset.forName("GBK"));
        byte[] platformIdFixed = new byte[11];
        System.arraycopy(platformIdBytes, 0, platformIdFixed, 0, Math.min(platformIdBytes.length, 11));

        byte[] vehicleNoBytes = vehicleNoParam.getBytes(Charset.forName("GBK"));
        byte[] vehicleNoFixed = new byte[21];
        System.arraycopy(vehicleNoBytes, 0, vehicleNoFixed, 0, Math.min(vehicleNoBytes.length, 21));

        byte[] srcPlatformIdFixed = new byte[11];
        if (srcPlatformId != null) {
            System.arraycopy(srcPlatformId, 0, srcPlatformIdFixed, 0, Math.min(srcPlatformId.length, 11));
        }

        byte[] data = ByteUtil.combineByteArray(
            platformIdFixed,                                    // 报警平台唯一编码 11字节
            ByteUtil.short2Bytes((short) warnType),            // 报警类型 2字节
            ByteUtil.long2Bytes(warnTime),                     // 报警时间 8字节
            ByteUtil.long2Bytes(startTime),                    // 事件开始时间 8字节
            ByteUtil.long2Bytes(endTime),                      // 事件结束时间 8字节
            vehicleNoFixed,                                    // 车牌号码 21字节
            new byte[]{vehicleColorParam},                     // 车牌颜色 1字节
            srcPlatformIdFixed,                                // 发起报警平台唯一编码 11字节
            ByteUtil.int2Byte((int) drvLineId),               // 线路ID 4字节
            ByteUtil.int2Byte((int) entity.warnLength),        // 报警信息长度 4字节
            warnContentBytes                                   // 报警描述，信息提示
        );

        entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.DOWN_WARN_MSG, MsgConstant.SubServiceType.DOWN_WARN_MSG_EXG_INFORM, data);
        return entity;
    }

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}


}


