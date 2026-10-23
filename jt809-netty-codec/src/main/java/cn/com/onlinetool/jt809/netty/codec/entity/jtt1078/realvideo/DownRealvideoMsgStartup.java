package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.realvideo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

/**
 * 实时音视频请求消息
 * <p>
 * 链路类型：从链路
 * 消息方向：发起方平台向接收方平台
 * 子业务类型标识：DOWN REALVIDEO MSG STARTUP。
 * 描述：政府视频监管平台向企业视频监控平台、上级政府平台向下级政府平台或跨域地区政府平台向归属地区政府平台下发该命令请求车辆的实时音视频。 数据体见表 42。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class DownRealvideoMsgStartup extends Base1078Entity {
    private byte channelId;
    /**
     * 音视频类型，定义如下：
     * 0x00：音视频；0x01：音频；0x02：视频
     */
    private byte avitemType;
    /**
     * 时效口令
     */
    private String authorizeCode;
    /**
     * 车辆进入跨域地区后 5min 之内的任一位置，仅跨域访问请求时使用此字段，
     * 按照 JT/ T 809—2011 协议 4. 5. 8. 1 的规定
     */
    private String gnssData;

    @Override
    public DownRealvideoMsgStartup decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.channelId = realData.readByte();
        this.avitemType = realData.readByte();
        this.authorizeCode = new String(BufUtil.bufToBytes(realData.readBytes(64)));
        if(realData.readableBytes() > 0) {
            this.gnssData = new String(BufUtil.bufToBytes(realData.readBytes(36)));
        }
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] data = new byte[1 + 1 + 64 + 36];
        data[0] = channelId;
        data[1] = avitemType;
        byte[] authorizeCodeBytes = authorizeCode.getBytes();
        System.arraycopy(authorizeCodeBytes, 0, data, 2, 64);
        if(gnssData != null && !gnssData.isEmpty()) {
            byte[] gnssDataBytes = gnssData.getBytes();
            System.arraycopy(gnssDataBytes, 0, data, 66, 36);
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.DOWN_REALVIDEO_MSG);
    }

    public static void main(String[] args) {
        DownRealvideoMsgStartup downRealvideoMsgStartup = new DownRealvideoMsgStartup();
        downRealvideoMsgStartup.setVehicleNo("粤A12340");
        downRealvideoMsgStartup.setVehicleColor((byte) 0x01);
        downRealvideoMsgStartup.setDataType(MsgConstant.SubServiceType.DOWN_REALVIDEO_MSG_STARTUP);
        downRealvideoMsgStartup.setDataLength(1 + 1 + 64 + 36);
        downRealvideoMsgStartup.setChannelId((byte) 0x01);
        downRealvideoMsgStartup.setAvitemType((byte) 0x00);
        downRealvideoMsgStartup.setAuthorizeCode("5r99JeevbiNDc2eSyUXeA5DGGrKyj7lCo4UmGKNwUtWVKWX2t7h2vUI9DlMfjjpW");
//        downRealvideoMsgStartup.setGnssData("20251008-141930-N39.046565-E117.794291");

        byte[] encode = downRealvideoMsgStartup.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        DownRealvideoMsgStartup decode = new DownRealvideoMsgStartup().decode(BufUtil.bytesToBuf(encode));
        System.out.println(decode);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String msgBody = objectMapper.writeValueAsString(decode);
            System.out.println(msgBody);
            DownRealvideoMsgStartup test = objectMapper.readValue(msgBody, DownRealvideoMsgStartup.class);
            System.out.println(test);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
