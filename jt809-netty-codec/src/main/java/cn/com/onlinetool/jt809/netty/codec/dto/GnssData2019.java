package cn.com.onlinetool.jt809.netty.codec.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

/**
 * 车辆定位信息数据体 - JT809-2019版本
 * 根据JT/T 809-2019协议规范实现
 */
@Setter
@Getter
@Accessors(chain = true)
public class GnssData2019 {
    
    /**
     * 该字段标识传输的定位信息是否使用国家测绘局批准的地图保密插件进行加密。
     * 加密标识：1-已加密，0-未加密
     * byte[1]
     */
    private byte encrypt;
    
    /**
     * 车辆定位信息数据长度
     * byte[4] - uint32_t
     */
    private long dataLength;
    
    /**
     * 车辆定位信息内容，包括车辆位置基本信息和位置附加信息，
     * 其数据格式按照 JT/T 808-2019 中 8.12 的要求
     * byte[GNSS_DATA_LENGTH]
     */
    private byte[] gnssData;
    
    /**
     * 监控平台唯一编码，由平台所在地行政区域代码和平台编号组成
     * byte[11]
     */
    private byte[] platformId1;
    
    /**
     * 报警状态，二进制表示，0 表示正常，1 表示报警，具体定义
     * 按照 JT/T 808-2011 中表 18 的规定
     * byte[4] - uint32_t
     */
    private long alarm1;
    
    /**
     * 市级监管平台唯一编码，由平台所在地行政区域代码和平台编号组成；
     * 未写时，全填 0；无市级平台时省级平台全填 1
     * byte[11]
     */
    private byte[] platformId2;
    
    /**
     * 报警状态，二进制表示，0 表示正常，1 表示报警，
     * B31B30B29......B2B1B0，具体定义按照 JT/T 808-2019 中表 18 的规定
     * byte[4] - uint32_t
     */
    private long alarm2;
    
    /**
     * 省级监管平台唯一编码，由平台所在地行政区域代码和平台编号组成；未写时，全填 0
     * byte[11]
     */
    private byte[] platformId3;
    
    /**
     * 报警状态，二进制表示，0 表示正常，1 表示报警，
     * B31B30B29......B2B1B0，具体定义按照 JT/T 808-2019 中表 18 的规定
     * byte[4] - uint32_t
     */
    private long alarm3;
    
    /**
     * 编码方法，将对象转换为字节数组
     * @return 编码后的字节数组
     */
    public byte[] encode() {
        // 计算总长度：1 + 4 + gnssData.length + 11 + 4 + 11 + 4 + 11 + 4 = 50 + gnssData.length
        int totalLength = 1 + 4 + (gnssData != null ? gnssData.length : 0) + 11 + 4 + 11 + 4 + 11 + 4;
        byte[] result = new byte[totalLength];
        
        int offset = 0;
        
        // ENCRYPT - 1字节
        result[offset] = this.encrypt;
        offset += 1;
        
        // DATA_LENGTH - 4字节
        System.arraycopy(ByteUtil.int2Byte((int) this.dataLength), 0, result, offset, 4);
        offset += 4;
        
        // GNSS_DATA - 变长
        if (gnssData != null && gnssData.length > 0) {
            System.arraycopy(gnssData, 0, result, offset, gnssData.length);
            offset += gnssData.length;
        }
        
        // PLATFORM_ID1 - 11字节
        if (platformId1 != null && platformId1.length >= 11) {
            System.arraycopy(platformId1, 0, result, offset, 11);
        } else {
            // 如果为空，填充0
            for (int i = 0; i < 11; i++) {
                result[offset + i] = 0;
            }
        }
        offset += 11;
        
        // ALARM1 - 4字节
        System.arraycopy(ByteUtil.int2Byte((int) this.alarm1), 0, result, offset, 4);
        offset += 4;
        
        // PLATFORM_ID2 - 11字节
        if (platformId2 != null && platformId2.length >= 11) {
            System.arraycopy(platformId2, 0, result, offset, 11);
        } else {
            // 如果为空，填充0
            for (int i = 0; i < 11; i++) {
                result[offset + i] = 0;
            }
        }
        offset += 11;
        
        // ALARM2 - 4字节
        System.arraycopy(ByteUtil.int2Byte((int) this.alarm2), 0, result, offset, 4);
        offset += 4;
        
        // PLATFORM_ID3 - 11字节
        if (platformId3 != null && platformId3.length >= 11) {
            System.arraycopy(platformId3, 0, result, offset, 11);
        } else {
            // 如果为空，填充0
            for (int i = 0; i < 11; i++) {
                result[offset + i] = 0;
            }
        }
        offset += 11;
        
        // ALARM3 - 4字节
        System.arraycopy(ByteUtil.int2Byte((int) this.alarm3), 0, result, offset, 4);
        
        return result;
    }
    
    /**
     * 解码方法，从字节数组中解析对象
     * @param data 待解码的字节数组
     * @return 解码后的GnssData2019对象
     */
    public static GnssData2019 decode(byte[] data) {
        if (data == null || data.length < 50) {
            throw new IllegalArgumentException("数据长度不足，无法解码");
        }
        
        GnssData2019 gnssData2019 = new GnssData2019();
        int offset = 0;
        
        // ENCRYPT - 1字节
        gnssData2019.encrypt = data[offset];
        offset += 1;
        
        // DATA_LENGTH - 4字节
        gnssData2019.dataLength = ByteUtil.byte2Int(data, offset);
        offset += 4;
        
        // GNSS_DATA - 变长
        if (gnssData2019.dataLength > 0 && offset + gnssData2019.dataLength <= data.length) {
            gnssData2019.gnssData = new byte[(int) gnssData2019.dataLength];
            System.arraycopy(data, offset, gnssData2019.gnssData, 0, (int) gnssData2019.dataLength);
            offset += gnssData2019.dataLength;
        }
        
        // PLATFORM_ID1 - 11字节
        gnssData2019.platformId1 = new byte[11];
        System.arraycopy(data, offset, gnssData2019.platformId1, 0, 11);
        offset += 11;
        
        // ALARM1 - 4字节
        gnssData2019.alarm1 = ByteUtil.byte2Int(data, offset);
        offset += 4;
        
        // PLATFORM_ID2 - 11字节
        gnssData2019.platformId2 = new byte[11];
        System.arraycopy(data, offset, gnssData2019.platformId2, 0, 11);
        offset += 11;
        
        // ALARM2 - 4字节
        gnssData2019.alarm2 = ByteUtil.byte2Int(data, offset);
        offset += 4;
        
        // PLATFORM_ID3 - 11字节
        gnssData2019.platformId3 = new byte[11];
        System.arraycopy(data, offset, gnssData2019.platformId3, 0, 11);
        offset += 11;
        
        // ALARM3 - 4字节
        gnssData2019.alarm3 = ByteUtil.byte2Int(data, offset);
        
        return gnssData2019;
    }
    
    /**
     * 获取平台ID的字符串表示
     * @param platformIdBytes 平台ID字节数组
     * @return 平台ID字符串
     */
    public static String getPlatformIdString(byte[] platformIdBytes) {
        if (platformIdBytes == null || platformIdBytes.length != 11) {
            return "";
        }
        return new String(platformIdBytes).trim();
    }
    
    /**
     * 设置平台ID
     * @param platformIdString 平台ID字符串
     * @return 平台ID字节数组
     */
    public static byte[] setPlatformId(String platformIdString) {
        byte[] platformId = new byte[11];
        if (platformIdString != null && !platformIdString.isEmpty()) {
            byte[] stringBytes = platformIdString.getBytes();
            int copyLength = Math.min(stringBytes.length, 11);
            System.arraycopy(stringBytes, 0, platformId, 0, copyLength);
        }
        return platformId;
    }
}