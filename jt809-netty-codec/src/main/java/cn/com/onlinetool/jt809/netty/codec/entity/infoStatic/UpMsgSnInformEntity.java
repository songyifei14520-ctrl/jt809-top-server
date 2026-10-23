package cn.com.onlinetool.jt809.netty.codec.entity.infoStatic;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.dto.SeriesInfo;
import cn.com.onlinetool.jt809.netty.codec.entity.BaseEntity;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 平台间消息序列号通知消息体
 * 结构见协议表24
 */
@Setter
@Getter
@NoArgsConstructor
public class UpMsgSnInformEntity implements Codec<UpMsgSnInformEntity> {
    private BaseStaticMsgEntity base;

    /**
     * 返回的子业务类型标识序列总数 (1字节)
     */
    private int seriesTotal;

    /**
     * 序列信息列表
     */
    private List<SeriesInfo> seriesList = new ArrayList<>();

    @Override
    public UpMsgSnInformEntity decode(ByteBuf buf) {
        this.base = new BaseStaticMsgEntity().decode(buf);

        ByteBuf msgbody = this.base.getData();

        this.seriesTotal = msgbody.readUnsignedByte();

        this.seriesList = new ArrayList<>();
        for (int i = 0; i < seriesTotal; i++) {
            int seriesDataType = msgbody.readUnsignedShort();
            long msgSn = msgbody.readUnsignedInt();
            long time = msgbody.readLong();
            seriesList.add(new SeriesInfo(seriesDataType, msgSn, time));
        }
        return this;
    }

    /**
     * 创建消息序列号通知实体
     * @param seriesList 序列信息列表
     * @return MsgSnInformEntity实例
     */
    public static UpMsgSnInformEntity create( List<SeriesInfo> seriesList) {
        UpMsgSnInformEntity entity = new UpMsgSnInformEntity();

        entity.seriesList = seriesList != null ? seriesList : new ArrayList<>();
        entity.seriesTotal = entity.seriesList.size();

        // 构建消息体
        List<byte[]> bodyParts = new ArrayList<>();
        bodyParts.add(new byte[]{(byte) entity.seriesTotal});

        // 添加序列信息
        for (SeriesInfo info : entity.seriesList) {
            bodyParts.add(ByteUtil.short2Bytes((short) info.getDataType()));
            bodyParts.add(ByteUtil.int2Byte((int) info.getMsgSn()));
            bodyParts.add(ByteUtil.long2Bytes(info.getTime()));
        }

        byte[] msgbody = ByteUtil.combineByteArray(bodyParts.toArray(new byte[0][]));

        entity.base = BaseStaticMsgEntity.create(MsgConstant.PrimaryServiceType.UP_MANAGE_MSG_SN_INFORM, msgbody);
        return entity;
    }

    @Override
    public byte[] encode() {
        return base.encode();
    }

}
