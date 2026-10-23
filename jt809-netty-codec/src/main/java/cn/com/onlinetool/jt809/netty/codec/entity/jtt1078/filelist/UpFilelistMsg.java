package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.filelist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.JT809Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 主动上传音视频资源目录信息消息
 * <p>
 * 链路类型：主链路
 * 消息方向：下级平台向上级平台
 * 子业务类型标识：UP FILELIST MSG。
 * 描述：企业视频监控平台向政府视频监管平台，或下级政府平台向上级政府平台主动发送带有特殊
 * 报警标识的音视频资源目录。 数据体见表 46。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpFilelistMsg extends Base1078Entity {
    /**
     * 资源目录项总数
     */
    private long itemNum;
    /**
     * 资源目录项列表，见表 47
     */
    private List<Item> itemList;

    @Override
    public UpFilelistMsg decode(ByteBuf buf) {
        super.decode(buf);
        ByteBuf realData = this.getData();
        this.itemNum = BufUtil.buf2UnsignedLong(realData.readBytes(4));
        List<Item> itemList = new ArrayList<>();
        int itemCount = realData.readableBytes() / 32;
        for (int i = 0; i < itemCount; i++) {
            Item decode = new Item().decode(realData.readBytes(32));
            itemList.add(decode);
        }
        this.itemList = itemList;
        return this;
    }

    @Override
    public byte[] encode() {
        int dataLength = (int) (4 + (1 + 8 + 8 + 8 + 1 + 1 + 1 + 4) * this.itemNum);
        byte[] data = new byte[dataLength];

        System.arraycopy(ByteUtil.int2Byte((int) this.itemNum), 0, data, 0, 4);
        for (int i = 0; i < this.getItemList().size(); i++) {
            byte[] itemEncode = this.getItemList().get(i).encode();
            System.arraycopy(itemEncode, 0, data, 4+i*32, Math.min(itemEncode.length, 32));
        }

        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_SEARCH_MSG);
    }

    public static void main(String[] args) {
        UpFilelistMsg upFilelistMsg = new UpFilelistMsg();
        upFilelistMsg.setVehicleNo("粤A88888");
        upFilelistMsg.setVehicleColor((byte) 0x01);
        upFilelistMsg.setDataType(MsgConstant.SubServiceType.UP_FILELIST_MSG);
        upFilelistMsg.setItemNum((byte) 0x01);

        Item item = new Item();
        item.setChannelId((byte) 0x01);
        item.setStartTime(1760054431);
        item.setEndTime(1760055223);
        item.setAlarmType1(0x00);
        item.setAlarmType2(0x00);
        item.setAvitemType((byte) 0x00);
        item.setStreamType((byte) 0x02);
        item.setMemType((byte) 0x01);
        item.setFileSize(87460355);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        upFilelistMsg.setItemList(itemList);

        upFilelistMsg.setDataLength(4 + (1 + 8 + 8 + 8 + 1 + 1 + 1 + 4) * upFilelistMsg.getItemNum());

        JT809Config.encrypt = true;
        JT809Config.M1 = 56107078;
        JT809Config.IA1 = 37382110;
        JT809Config.IC1 = 50361430;
        byte[] encode = upFilelistMsg.encode();
//        BytesPrintUtil.prettyPrint(encode, "222");
//        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());

        ByteBuf byteBuf = BufUtil.bytesToBuf(encode);
        ByteBuf newBuf = JT809Util.decode(byteBuf);
        System.out.println(ByteUtil.bytes2Hex(BufUtil.bufToBytes(newBuf)).toUpperCase());

        UpFilelistMsg decode = new UpFilelistMsg().decode(newBuf);
        System.out.println(decode);

    }
}
