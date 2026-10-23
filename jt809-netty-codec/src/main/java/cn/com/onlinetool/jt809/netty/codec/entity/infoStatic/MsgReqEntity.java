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

import java.nio.charset.Charset;

/**
 * 2019新协议
 * 平台链路连接情况与车辆定位消息传输情况上报应答消息
 * 业务类型标识：DOWN_MANAGE_MSG_REQ。
 * 描述：上级平台向下级平台发送上报指定下级监控平台一段时间内链路连接情况与车辆定位消息传输情况请求。
 */
@Setter
@Getter
@NoArgsConstructor
public class MsgReqEntity implements Codec<MsgReqEntity> {

    private BaseStaticMsgEntity base;

    private String platformId; // 平台ID 11字节
    private long startTime; // 开始时间 byte[8]
    private long endTime; // 结束时间 byte[8]

    @Override
    public MsgReqEntity decode(ByteBuf buf) {
        this.base = new BaseStaticMsgEntity().decode(buf);

        ByteBuf msgbody = this.base.getData();
        // 平台ID字段长度为11字节，使用GBK编码解析并去除空字符
        this.platformId = new String(BufUtil.bufToBytes(msgbody.slice(0, 11)), Charset.forName("GBK")).trim();
        this.startTime = BufUtil.buf2SignedLong(msgbody.slice(11, 8));
        this.endTime = BufUtil.buf2SignedLong(msgbody.slice(19, 8));

        return this;
    }

    public static MsgReqEntity create(String platformId, long startTime, long endTime) {
        MsgReqEntity entity = new MsgReqEntity();

        entity.platformId = platformId;
        entity.startTime = startTime;
        entity.endTime = endTime;

        // 参考ReportDriverInfoAckEntity的字符串处理方式，创建固定长度的字节数组
        byte[] platformIdBytes = new byte[11];
        byte[] platformIdTmp = platformId.getBytes(Charset.forName("GBK"));
        System.arraycopy(platformIdTmp, 0, platformIdBytes, 0, platformIdTmp.length > 11 ? 11 : platformIdTmp.length);

        byte[] msgbody = ByteUtil.combineByteArray(
            platformIdBytes,
            ByteUtil.long2Bytes(entity.startTime),
            ByteUtil.long2Bytes(entity.endTime)
        );

        entity.base = BaseStaticMsgEntity.create(MsgConstant.PrimaryServiceType.DOWN_MANAGE_MSG_REQ, msgbody);
        return entity;
    }

    @Override
    public byte[] encode() {
        return base.encode();
    }
}
