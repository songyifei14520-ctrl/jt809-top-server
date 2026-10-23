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
 * 链路类型:从链路 上报驾驶员身份识别信息请求消息 子业务类型标识:DOWN_EXG_MSG_REPORT_DRIVER_INFO
 * 描述:上级平台向下级平台下发上报车辆驾驶员身份识别信息
 */
@Setter
@Getter
@NoArgsConstructor
public class UpReportDriverInfoEntity implements Codec<UpReportDriverInfoEntity> {
	private BaseExgMsgEntity baseMsg;

    @Field(length = 16, desc = "驾驶员姓名")
    private String driverName;// 驾驶员姓名 byte[16]
    @Field(length = 20, desc = "身份证编号")
    private String driverId;// 身份证编号 byte[20]
    @Field(length = 20, desc = "从业资格证")
    private String licence;// 从业资格证 byte[20]
    @Field(length = 200, desc = "发证机构名称")
    private String orgName;// 发证机构名称 byte[200]
    @Field(length = 8, desc = "证件有效期")
    private long validDate;// 证件有效期 byte[8]

	@Override
	public UpReportDriverInfoEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);

		//2019版本协议
		if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
			ByteBuf data = baseMsg.getData();
			if (data.readableBytes() >= 264) {
				this.driverName = new String(BufUtil.bufToBytes(data.slice(0, 16)), Charset.forName("GBK")).trim();
				this.driverId = new String(BufUtil.bufToBytes(data.slice(16, 20)), Charset.forName("GBK")).trim();
				this.licence = new String(BufUtil.bufToBytes(data.slice(36, 20)), Charset.forName("GBK")).trim();
				this.orgName = new String(BufUtil.bufToBytes(data.slice(56, 200)), Charset.forName("GBK")).trim();
				this.validDate = BufUtil.buf2SignedLong(data.slice(256, 8));
			}
			return this;
		}

		//2011版本协议
		ByteBuf data = baseMsg.getData();
		if (data.readableBytes() >= 264) {
			this.driverName = new String(BufUtil.bufToBytes(data.slice(0, 16)), Charset.forName("GBK")).trim();
			this.driverId = new String(BufUtil.bufToBytes(data.slice(16, 20)), Charset.forName("GBK")).trim();
			this.licence = new String(BufUtil.bufToBytes(data.slice(36, 20)), Charset.forName("GBK")).trim();
			this.orgName = new String(BufUtil.bufToBytes(data.slice(56, 200)), Charset.forName("GBK")).trim();
			this.validDate = BufUtil.buf2SignedLong(data.slice(256, 8));
		}
		return this;
	}

	public static UpReportDriverInfoEntity create(String vehicleNo, byte vehicleColor, String driverName, String driverId, String licence, String orgName, long validDate) {
		UpReportDriverInfoEntity entity = new UpReportDriverInfoEntity();
		entity.driverName = driverName;
		entity.driverId = driverId;
		entity.licence = licence;
		entity.orgName = orgName;
		entity.validDate = validDate;

		byte[] driverNameBytes = new byte[16];
		byte[] driverIdBytes = new byte[20];
		byte[] licenceBytes = new byte[20];
		byte[] orgNameBytes = new byte[200];

		if (driverName != null) {
			byte[] nameBytes = driverName.getBytes(Charset.forName("GBK"));
			System.arraycopy(nameBytes, 0, driverNameBytes, 0, Math.min(nameBytes.length, 16));
		}
		if (driverId != null) {
			byte[] idBytes = driverId.getBytes(Charset.forName("GBK"));
			System.arraycopy(idBytes, 0, driverIdBytes, 0, Math.min(idBytes.length, 20));
		}
		if (licence != null) {
			byte[] licBytes = licence.getBytes(Charset.forName("GBK"));
			System.arraycopy(licBytes, 0, licenceBytes, 0, Math.min(licBytes.length, 20));
		}
		if (orgName != null) {
			byte[] orgBytes = orgName.getBytes(Charset.forName("GBK"));
			System.arraycopy(orgBytes, 0, orgNameBytes, 0, Math.min(orgBytes.length, 200));
		}

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_EXG_MSG_REPORT_DRIVER_INFO,
				ByteUtil.combineByteArray(
						driverNameBytes,
						driverIdBytes,
						licenceBytes,
						orgNameBytes,
						ByteUtil.long2Bytes(validDate)));
		return entity;
	}



	public static UpReportDriverInfoEntity create(String vehicleNo, byte vehicleColor) {
		UpReportDriverInfoEntity entity = new UpReportDriverInfoEntity();

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.DOWN_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_EXG_MSG_REPORT_DRIVER_INFO, new byte[0]);
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
