package cn.com.onlinetool.jt809.netty.codec.util;

import cn.com.onlinetool.jt809.netty.codec.dto.GnssData2019;

/**
 * JT809-2019协议GNSS数据工具类
 * 用于处理车辆定位信息数据体相关操作
 */
public class GnssDataUtil2019 {

    /**
     * 创建一个空的GnssData2019对象
     * 所有字段使用默认值
     * @return GnssData2019对象
     */
    public static GnssData2019 createEmptyGnssData() {
        GnssData2019 gnssData = new GnssData2019();
        gnssData.setEncrypt((byte) 0); // 未加密
        gnssData.setDataLength(0); // 数据长度为0
        gnssData.setGnssData(new byte[0]); // 空的GNSS数据
        gnssData.setPlatformId1(new byte[11]); // 空的平台ID1
        gnssData.setAlarm1(0); // 无报警
        gnssData.setPlatformId2(new byte[11]); // 空的平台ID2
        gnssData.setAlarm2(0); // 无报警
        gnssData.setPlatformId3(new byte[11]); // 空的平台ID3
        gnssData.setAlarm3(0); // 无报警
        return gnssData;
    }
    
    /**
     * 设置监控平台唯一编码
     * @param gnssData GnssData2019对象
     * @param platformId 平台ID字符串
     * @param platformType 平台类型：1-主平台，2-市级平台，3-省级平台
     */
    public static void setPlatformId(GnssData2019 gnssData, String platformId, int platformType) {
        if (gnssData == null) {
            return;
        }
        
        byte[] platformIdBytes = GnssData2019.setPlatformId(platformId);
        
        switch (platformType) {
            case 1:
                gnssData.setPlatformId1(platformIdBytes);
                break;
            case 2:
                gnssData.setPlatformId2(platformIdBytes);
                break;
            case 3:
                gnssData.setPlatformId3(platformIdBytes);
                break;
            default:
                // 默认设置为主平台
                gnssData.setPlatformId1(platformIdBytes);
        }
    }
    
    /**
     * 设置报警状态
     * @param gnssData GnssData2019对象
     * @param alarm 报警状态值
     * @param alarmType 报警类型：1-主平台报警，2-市级平台报警，3-省级平台报警
     */
    public static void setAlarm(GnssData2019 gnssData, long alarm, int alarmType) {
        if (gnssData == null) {
            return;
        }
        
        switch (alarmType) {
            case 1:
                gnssData.setAlarm1(alarm);
                break;
            case 2:
                gnssData.setAlarm2(alarm);
                break;
            case 3:
                gnssData.setAlarm3(alarm);
                break;
            default:
                // 默认设置为主平台报警
                gnssData.setAlarm1(alarm);
        }
    }
    
    /**
     * 设置市级平台为无市级平台（全填1）
     * @param gnssData GnssData2019对象
     */
    public static void setNoMunicipalPlatform(GnssData2019 gnssData) {
        if (gnssData == null) {
            return;
        }
        
        byte[] platformId2 = new byte[11];
        for (int i = 0; i < 11; i++) {
            platformId2[i] = 1;
        }
        gnssData.setPlatformId2(platformId2);
    }
    
    /**
     * 设置省级平台为未写（全填0）
     * @param gnssData GnssData2019对象
     */
    public static void setEmptyProvincialPlatform(GnssData2019 gnssData) {
        if (gnssData == null) {
            return;
        }
        
        byte[] platformId3 = new byte[11];
        gnssData.setPlatformId3(platformId3);
    }
    
    /**
     * 检查GnssData2019对象是否有效
     * @param gnssData GnssData2019对象
     * @return 是否有效
     */
    public static boolean isValid(GnssData2019 gnssData) {
        return gnssData != null && 
               gnssData.getGnssData() != null && 
               gnssData.getDataLength() > 0 &&
               gnssData.getPlatformId1() != null &&
               gnssData.getPlatformId1().length == 11;
    }
}