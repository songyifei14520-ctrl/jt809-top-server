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
 * 平台链路连接情况与车辆定位消息传输情况上报应答消息
 * 业务数据类型标识: UP_MANAGE_MSG_RSP
 * 描述：下级平台向上级平台上报平台链路连接情况与车辆定位消息传输情况的应答消息
 */
@Setter
@Getter
@NoArgsConstructor
public class MsgRspEntity implements Codec<MsgRspEntity> {
    private BaseStaticMsgEntity baseMsg;

    private String platformId; // 平台ID 11字节
    private long startTime; // 开始时间 8字节 time_t 用UTC时间表示
    private long endTime; // 结束时间 8字节 time_t 用UTC时间表示
    private int loseDynamicSum; // START_TIME~END_TIME期间下级平台丢失的车辆定位消息总数 4 字节 uint32_t
    private byte disconnectNum; // START_TIME~END_TIME期间下级监控平台链路断开次数 1字节 BYTE
    private int disconnectTime; // START_TIME~END_TIME期间下级监控平台链路断开总时长 4字节 uint32_t 用秒表示


    @Override
    public MsgRspEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseStaticMsgEntity().decode(buf);

        // 从 baseMsg 中获取 platformId

        ByteBuf data = baseMsg.getData();
        this.platformId = data.getCharSequence(0, 11, Charset.forName("GBK")).toString();
        this.startTime = BufUtil.buf2SignedLong(data.slice(11, 8));
        this.endTime = BufUtil.buf2SignedLong(data.slice(19, 8));
        this.loseDynamicSum = (int)BufUtil.buf2UnsignedLong(data.slice(27, 4));
        this.disconnectNum = data.getByte(31);
        this.disconnectTime = (int) BufUtil.buf2UnsignedLong(data.slice(32, 4));
        return this;
    }



    public static MsgRspEntity create(String platformId, long startTime, long endTime,
                                    int loseDynamicSum, byte disconnectNum, int disconnectTime) {
        MsgRspEntity entity = new MsgRspEntity();

        entity.platformId = platformId;
        entity.startTime = startTime;
        entity.endTime = endTime;
        entity.loseDynamicSum = loseDynamicSum;
        entity.disconnectNum = disconnectNum;
        entity.disconnectTime = disconnectTime;

        byte[] platformIdBytes = new byte[11];
        byte[] platformIdTmp = platformId.getBytes(Charset.forName("GBK"));
        System.arraycopy(platformIdTmp, 0, platformIdBytes, 0, platformIdTmp.length > 11 ? 11 : platformIdTmp.length);

        entity.baseMsg = BaseStaticMsgEntity.create(
                MsgConstant.PrimaryServiceType.UP_MANAGE_MSG_RSP, ByteUtil.combineByteArray(
                    platformIdBytes,
                    ByteUtil.long2Bytes(entity.startTime),
                    ByteUtil.long2Bytes(entity.endTime),
                    ByteUtil.int2Byte(entity.loseDynamicSum),
                    new byte[]{entity.disconnectNum},
                    ByteUtil.int2Byte(entity.disconnectTime)
                ));
        return entity;
    }


    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}
