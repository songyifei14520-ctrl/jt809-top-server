package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.filelist;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

/**
 * 上传音视频资源目录项列表格式
 * <p>
 * TODO 详细描述
 *
 * @author syz
 * @since 2025/10/6
 */
@Data
public class Item implements Serializable {
    private byte channelId;
    private long startTime;
    private Date startTimeStr;
    private long endTime;
    private Date endTimeStr;
    /**
     * bit0-31 按照 JT/ T 808—2011 表 18 报警标志位定义；
     */
    private long alarmType1;
    /**
     * bit32-63 见表 10
     */
    private long alarmType2;
    /**
     * 音视频类型，定义如下：
     * 0x00：音视频；0x01：音频；0x02：视频
     */
    private byte avitemType;
    /**
     * 码流类型，定义如下：
     * 0x01：主码流；0x02：子码流
     */
    private byte streamType;
    /**
     * 存储器类型，定义如下：
     * 0x01：主存储器；0x02：灾备存储器
     */
    private byte memType;
    /**
     * 文件大小，单位字节(BYTE)
     */
    private int fileSize;

    public Item decode(ByteBuf realData) {
        this.channelId = realData.readByte();
        this.startTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.startTimeStr = Date.from(Instant.ofEpochSecond(this.startTime));
        this.endTime = BufUtil.buf2SignedLong(realData.readBytes(8));
        this.endTimeStr = Date.from(Instant.ofEpochSecond(this.endTime));
        this.alarmType1 = BufUtil.buf2UnsignedLong(realData.readBytes(4));
        this.alarmType2 = BufUtil.buf2UnsignedLong(realData.readBytes(4));
        this.avitemType = realData.readByte();
        this.streamType = realData.readByte();
        this.memType = realData.readByte();
        this.fileSize = (int)BufUtil.buf2UnsignedLong(realData.readBytes(4));
        return this;
    }

    public byte[] encode() {
        byte[] data = new byte[1 + 8 + 8 + 8 + 1 + 1 + 1 + 4];
        data[0] = channelId;
        System.arraycopy(ByteUtil.long2Bytes(startTime), 0, data, 1, 8);
        System.arraycopy(ByteUtil.long2Bytes(endTime), 0, data, 9, 8);
        System.arraycopy(ByteUtil.int2Byte((int)alarmType1), 0, data, 17, 4);
        System.arraycopy(ByteUtil.int2Byte((int)alarmType2), 0, data, 21, 4);
        data[25] = avitemType;
        data[26] = streamType;
        data[27] = memType;
        System.arraycopy(ByteUtil.int2Byte(fileSize), 0, data, 28, 4);
        return data;
    }

    public static void main(String[] args) {
        Item item = new Item();
        item.setChannelId((byte) 0x01);
        item.setStartTime(1693708800L);
        item.setEndTime(1693708800L);
        item.setAlarmType1(0x00);
        item.setAlarmType2(0x00);
        item.setAvitemType((byte) 0x00);
        item.setStreamType((byte) 0x01);
        item.setMemType((byte) 0x01);
        item.setFileSize(1024);

        byte[] encode = item.encode();
        BytesPrintUtil.prettyPrint(encode);
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
