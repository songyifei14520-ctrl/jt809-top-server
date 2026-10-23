package cn.com.onlinetool.jt809.netty.codec.entity.warnMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.annotation.Field;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 链路类型: 主链路
 * 消息方向: 下级平台向上级平台
 * 业务类型标识: UP_WARN_MSG (0x1400)
 * 子业务类型标识: UP_WARN_MSG_FILELIST_AUTO (0x1407)
 * 描述: 下级平台向上级平台主动发送报警附件目录, 上级平台可通过报警附件文件 URL 以 HTTP 协议直接访问附件文件或者下载附件文件, 其数据体定义见表 51。
 * 依据: DB32/T 3610.3—2025 6.2.2.5 表 51 报警附件目录请求应答数据体 及 表 50 报警附件列表数据体
 */
@Setter
@Getter
@NoArgsConstructor
public class UpWarnMsgFilelistAutoEntity implements Codec<UpWarnMsgFilelistAutoEntity> {

    // ========== 车牌颜色常量 (DB32/T 3610.3—2025 表51) ==========
    /** 0x01: 蓝色 */
    public static final byte COLOR_BLUE = 0x01;
    /** 0x02: 黄色 */
    public static final byte COLOR_YELLOW = 0x02;
    /** 0x03: 黑色 */
    public static final byte COLOR_BLACK = 0x03;
    /** 0x04: 白色 */
    public static final byte COLOR_WHITE = 0x04;
    /** 0x05: 绿色 */
    public static final byte COLOR_GREEN = 0x05;
    /** 0x06: 黄绿色 */
    public static final byte COLOR_YELLOW_GREEN = 0x06;
    /** 0x07: 渐变绿 */
    public static final byte COLOR_GRADIENT_GREEN = 0x07;
    /** 0x09: 其他 */
    public static final byte COLOR_OTHER = 0x09;

    private BaseWarnMsgEntity baseMsg;

    // ========== 表 51 字段 ==========
    @Field(length = 21, desc = "车牌号码", version = {2025})
    private String vehicleNo; // 车牌号码 byte[21]

    @Field(length = 1, desc = "车牌颜色", version = {2025})
    private byte vehicleColor; // 车牌颜色 byte[1]

    // ========== 表 51 数据部分字段 ==========
    @Field(length = 16, desc = "报警标识号", version = {2025})
    private String alarmId; // 报警标识号 BYTE[16] 16字节

    @Field(length = 1, desc = "附件数量", version = {2025})
    private short fileCount; // 附件数量 BYTE 1字节

    @Field(desc = "报警附件列表", version = {2025})
    private List<AlarmFileItem> fileList = new ArrayList<>(); // 报警附件列表见表 50

    @Override
    public UpWarnMsgFilelistAutoEntity decode(ByteBuf buf) {
        this.baseMsg = new BaseWarnMsgEntity().decodeWithVehicle(buf);
        this.vehicleNo = cleanString(this.baseMsg.getVehicleNo());
        this.vehicleColor = this.baseMsg.getVehicleColor();

        ByteBuf data = this.baseMsg.getData();
        this.fileList = new ArrayList<>();
        if (data != null && data.readableBytes() >= 16) {
            // 1. ALARM_ID 16字节
            byte[] alarmIdBytes = new byte[16];
            data.readBytes(alarmIdBytes);
            this.alarmId = cleanString(new String(alarmIdBytes, Charset.forName("GBK")));

            // 2. FILE_COUNT 1字节 + FILE_LIST (表50)
            if (data.readableBytes() >= 1) {
                int count = data.readUnsignedByte();
                this.fileCount = (short) count;
                for (int i = 0; i < count && data.isReadable(); i++) {
                    AlarmFileItem item = AlarmFileItem.decode(data);
                    if (item != null) {
                        this.fileList.add(item);
                    }
                }
            }
        }
        return this;
    }

    /**
     * DB32/T 3610.3—2025 表51 报警附件主动上报消息构造方法
     *
     * @param vehicleNo    车牌号码 (21字节)
     * @param vehicleColor 车牌颜色 (0x01:蓝色, 0x02:黄色, 0x03:黑色, 0x04:白色, 0x05:绿色, 0x06:黄绿色, 0x07:渐变绿, 0x09:其他)
     * @param alarmId      报警标识号 (16字节)
     * @param fileList     报警附件列表 (表50)
     * @return UpWarnMsgFilelistAutoEntity 实例
     */
    public static UpWarnMsgFilelistAutoEntity create(
            String vehicleNo, byte vehicleColor, String alarmId,
            List<AlarmFileItem> fileList) {

        UpWarnMsgFilelistAutoEntity entity = new UpWarnMsgFilelistAutoEntity();
        entity.vehicleNo = vehicleNo;
        entity.vehicleColor = vehicleColor;
        entity.alarmId = alarmId;
        entity.fileList = fileList != null ? fileList : new ArrayList<>();
        entity.fileCount = (short) entity.fileList.size();

        byte[] alarmIdBytes = copyBytes(alarmId, 16);

        // 计算所有附件列表的总长度并组装数据
        List<byte[]> itemByteList = new ArrayList<>();
        int fileListTotalLength = 0;
        for (AlarmFileItem item : entity.fileList) {
            byte[] itemBytes = item.encode();
            itemByteList.add(itemBytes);
            fileListTotalLength += itemBytes.length;
        }

        byte[] fileBytes = new byte[fileListTotalLength];
        int fileOffset = 0;
        for (byte[] b : itemByteList) {
            System.arraycopy(b, 0, fileBytes, fileOffset, b.length);
            fileOffset += b.length;
        }

        byte[] data = ByteUtil.combineByteArray(
                alarmIdBytes,
                new byte[]{(byte) entity.fileCount},
                fileBytes
        );

        entity.baseMsg = BaseWarnMsgEntity.create(
                MsgConstant.PrimaryServiceType.UP_WARN_MSG,
                vehicleNo,
                vehicleColor,
                MsgConstant.SubServiceType.UP_WARN_MSG_FILELIST_AUTO,
                data
        );
        return entity;
    }

    private static byte[] copyBytes(String src, int targetLen) {
        byte[] result = new byte[targetLen];
        if (src != null) {
            byte[] srcBytes = src.getBytes(Charset.forName("GBK"));
            System.arraycopy(srcBytes, 0, result, 0, Math.min(srcBytes.length, targetLen));
        }
        return result;
    }

    private static String cleanString(String str) {
        if (str == null) {
            return null;
        }
        int nullIdx = str.indexOf('\0');
        if (nullIdx >= 0) {
            str = str.substring(0, nullIdx);
        }
        return str.trim();
    }

    @Override
    public byte[] encode() {
        return baseMsg.encode();
    }
}
