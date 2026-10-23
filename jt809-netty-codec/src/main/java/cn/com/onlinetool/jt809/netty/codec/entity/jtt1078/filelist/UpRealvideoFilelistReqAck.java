package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.filelist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;
import cn.com.onlinetool.jt809.netty.codec.util.BytesPrintUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询音视频资源目录应答消息
 * <p>
 * 链路类型：主链路
 * 消息方向：下级平台向上级平台。
 * 子业务类型标识：UP REALVIDEO FILELIST REQ ACK。
 * 描述：企业视频监控平台应答政府视频监管平台或下级政府平台向上级政府平台应答音视频资源
 * 目录消息。 数据体见表 50。
 *
 * @author syz
 * @since 2025/10/5
 */
@JsonIgnoreProperties(value = { "subDataType", "tailFlag", "crcCode", "header", "headFlag" })
@EqualsAndHashCode(callSuper = true)
@Data
public class UpRealvideoFilelistReqAck extends Base1078Entity {
    /**
     * 应答结果，定义如下：
     * 0x00：成功；
     * 0x01：失败；
     * 0x02：不支持；
     * 0x03：会话结束；
     * 0x04：时效口令错误；
     * 0x05：不满足跨域条件
     */
    private byte result;
    /**
     * 资源目录项总数
     */
    private long itemNum;
    /**
     * 资源目录项列表，见表 47
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Item> itemList;

    @Override
    public byte[] encode() {
        byte[] data = null;
        if(this.result == 0) {
            if(itemList == null || itemList.isEmpty()) {
                data = new byte[1 + 4];
                data[0] = result;
                System.arraycopy(ByteUtil.int2Byte((int) this.itemNum), 0, data, 1, 4);
            } else {
                int dataLength = (int) (1 + 4 + (1 + 8 + 8 + 8 + 1 + 1 + 1 + 4) * this.itemNum);
                data = new byte[dataLength];
                data[0] = result;
                System.arraycopy(ByteUtil.int2Byte((int) this.itemNum), 0, data, 1, 4);
                for (int i = 0; i < this.getItemList().size(); i++) {
                    byte[] itemEncode = this.getItemList().get(i).encode();
                    System.arraycopy(itemEncode, 0, data, 5+i*32, Math.min(itemEncode.length, 32));
                }
            }
        } else {
            data = new byte[1];
            data[0] = result;
        }
        this.setData(BufUtil.bytesToBuf(data));
        return super.encode(MsgConstant.PrimaryServiceType.UP_SEARCH_MSG);
    }

    public static void main(String[] args) throws JsonProcessingException {
        UpRealvideoFilelistReqAck upRealvideoFilelistReqAck = new UpRealvideoFilelistReqAck();
        upRealvideoFilelistReqAck.setVehicleNo("粤A12340");
        upRealvideoFilelistReqAck.setVehicleColor((byte) 0x01);
        upRealvideoFilelistReqAck.setDataType(MsgConstant.SubServiceType.UP_REALVIDEO_FILELIST_REQ_ACK);
        upRealvideoFilelistReqAck.setResult((byte) 0x00);
        upRealvideoFilelistReqAck.setItemNum((byte) 0x01);

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
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        upRealvideoFilelistReqAck.setItemList(itemList);

        upRealvideoFilelistReqAck.setDataLength(1 + 4 + (1 + 8 + 8 + 8 + 1 + 1 + 1 + 4) * upRealvideoFilelistReqAck.getItemNum());
        System.out.println(new ObjectMapper().writeValueAsString(upRealvideoFilelistReqAck));
        byte[] encode = upRealvideoFilelistReqAck.encode();
        BytesPrintUtil.prettyPrint(encode, "msg");
        System.out.println(ByteUtil.bytes2Hex(encode).toUpperCase());
    }
}
