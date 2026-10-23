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
 * 链路类型:从链路 报警督办请求消息 子业务类型标识:DOWN_WARN_MSG_URGE_TODO_REQ
 * 描述:上级平台向车辆归属下级平台下发本消息，催促其及时处理相关车辆的报警信息
 */
@Setter
@Getter
@NoArgsConstructor
public class UrgeTodoReqEntity implements Codec<UrgeTodoReqEntity> {
	private BaseWarnMsgEntity baseMsg;

    @Field(length = 11, desc = "报警平台唯一编码",version = {2019})
    private String platformId;//发起报警平台唯一编码，由平台所在地行政区域代码和平台编号组成

    @Field(length = 2, desc = "报警消息源子业务类型标识",version = {2019})
    private int dataType;//报警消息源子业务类型标识 byte[2]

    @Field(length = 4, desc = "报警信息消息或车辆定位信息报文序号",version = {2019})
    private long msgSn;

    @Field(length = 1, desc = "报警信息来源",version = {2011})
	private byte warnSrc; // 报警信息来源 byte[1]
	private int warnType; // 报警类型 byte[2]
	private long warnTime; // 报警时间 byte[8]
	private long supervisionId; // 报警督办ID byte[4]
	private long supervisionEndTime; // 督办截止时间，UTC 时间格式 byte[8]

	/**
	 * 督办级别，定义如下： 0x00：紧急 0x01：一般
	 */
	private byte supervisionLevel; // 督办级别 byte[1]
	private String supervisor; // 督办人 byte[16]
	private String supervisionTel; // 督办联系电话 byte[20]
	private String supervisionEmail; // 督办联系电子邮件 byte[30]

	@Override
	public UrgeTodoReqEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseWarnMsgEntity().decode(buf);

        //2019版本协议
        if (baseMsg.getBase().getHeader().getHeaderLength() == Header2019.MSG_HEADER_LENTH) {
            ByteBuf data = baseMsg.getData();

            // PLATFORM_ID (11字节)
            this.platformId = new String(BufUtil.bufToBytes(data.slice(0, 11)), Charset.forName("GBK")).trim();
            // SOUCE_WARN_TIME (8字节)
            this.warnTime = BufUtil.buf2SignedLong(data.slice(11, 8));
            // DATA_TYPE (2字节)
            this.dataType = BufUtil.buf2UnsignedInt(data.slice(19, 2));
            // MSG_SN (4字节)
            this.msgSn = BufUtil.buf2UnsignedLong(data.slice(21, 4));
            // SUPERVISION_ENDTIME (8字节)
            this.supervisionEndTime = BufUtil.buf2SignedLong(data.slice(25, 8));
            // SUPERVISION_LEVEL (1字节)
            this.supervisionLevel = data.getByte(33);
            // SUPERVISOR (16字节)
            this.supervisor = new String(BufUtil.bufToBytes(data.slice(34, 16)), Charset.forName("GBK")).trim();
            // SUPERVISOR_TEL (20字节)
            this.supervisionTel = new String(BufUtil.bufToBytes(data.slice(50, 20)), Charset.forName("GBK")).trim();
            // SUPERVISOR_EMAIL (32字节)
            this.supervisionEmail = new String(BufUtil.bufToBytes(data.slice(70, 32)), Charset.forName("GBK")).trim();

            return this;
        }

        //2011版本协议
		this.warnSrc = baseMsg.getData().getByte(0);
		this.warnType = BufUtil.buf2UnsignedInt(baseMsg.getData().slice(1, 2));
		this.warnTime = BufUtil.buf2SignedLong(baseMsg.getData().slice(3, 8));
		this.supervisionId = BufUtil.buf2UnsignedLong(baseMsg.getData().slice(11, 4));
		this.supervisionEndTime = BufUtil.buf2SignedLong(baseMsg.getData().slice(15, 8));
		this.supervisionLevel = baseMsg.getData().getByte(23);
		this.supervisor = new String(BufUtil.bufToBytes(baseMsg.getData().slice(24, 16)), Charset.forName("GBK")).trim();
		this.supervisionTel = new String(BufUtil.bufToBytes(baseMsg.getData().slice(40, 20)), Charset.forName("GBK")).trim();
		this.supervisionEmail = new String(BufUtil.bufToBytes(baseMsg.getData().slice(60, 30)), Charset.forName("GBK")).trim();

		return this;
	}

	public static UrgeTodoReqEntity create(String vehicleNo, byte vehicleColor, byte warnSrc, int warnType,
			long warnTime, long supervisionId, long supervisionEndTime, byte supervisionLevel, String supervisor,
			String supervisionTel, String supervisionEmail) {
		UrgeTodoReqEntity entity = new UrgeTodoReqEntity();

		entity.warnSrc = warnSrc;
		entity.warnType = warnType;
		entity.warnTime = warnTime;
		entity.supervisionId = supervisionId;
		entity.supervisionEndTime = supervisionEndTime;
		entity.supervisionLevel = supervisionLevel;

		entity.supervisor = supervisor;
		entity.supervisionTel = supervisionTel;
		entity.supervisionEmail = supervisionEmail;

		byte[] supervisorBytes = new byte[16];
		byte[] supervisorTmp = supervisor.getBytes(Charset.forName("GBK"));
		System.arraycopy(supervisorTmp, 0, supervisorBytes, 0, supervisorTmp.length > 16 ? 16 : supervisorTmp.length);

		byte[] supervisionTelBytes = new byte[20];
		byte[] supervisionTelTmp = supervisionTel.getBytes(Charset.forName("GBK"));
		System.arraycopy(supervisionTelTmp, 0, supervisionTelBytes, 0,
				supervisionTelTmp.length > 20 ? 20 : supervisionTelTmp.length);

		byte[] supervisionEmailBytes = new byte[30];
		byte[] supervisionEmailBytesTmp = supervisionEmail.getBytes(Charset.forName("GBK"));
		System.arraycopy(supervisionEmailBytesTmp, 0, supervisionEmailBytes, 0,
				supervisionEmailBytesTmp.length > 30 ? 30 : supervisionEmailBytesTmp.length);

		entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.DOWN_WARN_MSG,vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.DOWN_WARN_MSG_URGE_TODO_REQ,
				ByteUtil.combineByteArray(new byte[] { entity.warnSrc }, ByteUtil.short2Bytes((short) entity.warnType),
						ByteUtil.long2Bytes(entity.warnTime), ByteUtil.int2Byte((int) entity.supervisionId),
						ByteUtil.long2Bytes(entity.supervisionEndTime), new byte[] { entity.supervisionLevel },
						supervisorBytes, supervisionTelBytes, supervisionEmailBytes));
		return entity;
	}

    /**
     * 创建报警督办请求消息实体（2019协议版本）
     * @param platformId 报警平台唯一编码
     * @param dataType 报警消息源子业务类型标识
     * @param msgSn 报警信息消息或车辆定位信息报文序号
     * @param warnTime 报警时间
     * @param supervisionEndTime 督办截止时间
     * @param supervisionLevel 督办级别
     * @param supervisor 督办人
     * @param supervisionTel 督办联系电话
     * @param supervisionEmail 督办联系电子邮件
     * @return UrgeTodoReqEntity实例
     */
    public static UrgeTodoReqEntity create( String platformId, int dataType, long msgSn,
                                          long warnTime, long supervisionEndTime,
                                          byte supervisionLevel, String supervisor, String supervisionTel, String supervisionEmail) {
        UrgeTodoReqEntity entity = new UrgeTodoReqEntity();

        // 设置2019协议字段
        entity.platformId = platformId;
        entity.dataType = dataType;
        entity.msgSn = msgSn;

        entity.warnTime = warnTime;
        entity.supervisionEndTime = supervisionEndTime;
        entity.supervisionLevel = supervisionLevel;
        entity.supervisor = supervisor;
        entity.supervisionTel = supervisionTel;
        entity.supervisionEmail = supervisionEmail;

        // 构建2019协议数据部分
        byte[] platformIdFixed = new byte[11];
        if (platformId != null) {
            byte[] platformIdBytes = platformId.getBytes(Charset.forName("GBK"));
            System.arraycopy(platformIdBytes, 0, platformIdFixed, 0, Math.min(platformIdBytes.length, 11));
        }

        byte[] supervisorBytes = new byte[16];
        byte[] supervisorTmp = supervisor.getBytes(Charset.forName("GBK"));
        System.arraycopy(supervisorTmp, 0, supervisorBytes, 0, Math.min(supervisorTmp.length, 16));

        byte[] supervisionTelBytes = new byte[20];
        byte[] supervisionTelTmp = supervisionTel.getBytes(Charset.forName("GBK"));
        System.arraycopy(supervisionTelTmp, 0, supervisionTelBytes, 0, Math.min(supervisionTelTmp.length, 20));

        byte[] supervisionEmailBytes = new byte[32];
        byte[] supervisionEmailBytesTmp = supervisionEmail.getBytes(Charset.forName("GBK"));
        System.arraycopy(supervisionEmailBytesTmp, 0, supervisionEmailBytes, 0, Math.min(supervisionEmailBytesTmp.length, 30));

        byte[] data = ByteUtil.combineByteArray(
            platformIdFixed,                                    // 报警平台唯一编码 11字节
            ByteUtil.long2Bytes(warnTime),                     // 报警时间 8字节
            ByteUtil.short2Bytes((short) dataType),            // 报警消息源子业务类型标识 2字节
            ByteUtil.int2Byte((int) msgSn),                    // 报警信息消息或车辆定位信息报文序号 4字节
            ByteUtil.long2Bytes(supervisionEndTime),           // 督办截止时间 8字节
            new byte[] { supervisionLevel },                   // 督办级别 1字节
            supervisorBytes,                                   // 督办人 16字节
            supervisionTelBytes,                               // 督办联系电话 20字节
            supervisionEmailBytes                              // 督办联系电子邮件 30字节
        );

        entity.baseMsg = BaseWarnMsgEntity.create(MsgConstant.PrimaryServiceType.DOWN_WARN_MSG, MsgConstant.SubServiceType.DOWN_WARN_MSG_URGE_TODO_REQ, data);
        return entity;
    }

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}

}
