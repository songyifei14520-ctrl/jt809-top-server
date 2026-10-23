package cn.com.onlinetool.jt809.netty.codec.enums;

import lombok.Getter;

/**
 * 报警类型
 */
@Getter
public enum AlarmTypeEnum {

	// 位置相关报警类型
	ALARM_OVERSPEED(0x0001, "超速报警", false),// AlarmBit 1
	ALARM_DRIVER_FATIGUE(0x0002, "疲劳驾驶报警", false), //AlarmBit 2
	ALARM_EMERGENCY(0x0003, "紧急报警", false), //AlarmBit 0
	ALARM_AREA_ENTER(0x0004, "进入指定区域报警", false),//AlarmBit 20
	ALARM_AREA_LEAVE(0x0005, "离开指定区域报警", false),//AlarmBit 21
	ALARM_OUT_OF_BOUND(0x0008, "越界报警", false),
	ALARM_STEAL(0x0009, "盗警", false),//AlarmBit 26
	ALARM_ROB(0x000A, "劫警", false),
	ALARM_LINE_OFFSET(0x000B, "偏离路线报警", false),//AlarmBit 23
	ALARM_CAR_MOVE(0x000C, "车辆移动报警", false),// AlarmBit 28
	ALARM_DRIVER_OVERTIME(0x000D, "超时驾驶报警", true),// AlarmBit 18 - 非位置相关
	ALARM_ILLEGAL_DRIVING(0x0010, "违规行驶报警", false),// AlarmBit 15
	ALARM_FRONT_COLLISION(0x0011, "前撞报警", false),
	ALARM_LANE_DEPARTURE(0x0012, "车道偏离报警", false),
	ALARM_TIRE_PRESSURE(0x0013, "胎压异常报警", true),// AlarmBit 16 - 非位置相关
	ALARM_DYNAMIC_INFO(0x0014, "动态信息异常报警", true),
	ALARM_OTHER(0x00FF, "其他报警", false),

	// 非位置相关报警类型
	ALARM_OVERTIME_PARKING(0xA001, "超时停车", true),
	ALARM_VEHICLE_POSITION_UPLOAD_TIMEOUT(0xA002, "车辆定位信息上报时间间隔异常", true),
	ALARM_VEHICLE_POSITION_UPLOAD_DISTANCE_TIMEOUT(0xA003, "车辆定位信息上报距离间隔异常", true),
	ALARM_SUBORDINATE_PLATFORM_ABNORMAL_OFFLINE(0xA004, "下级平台异常断线", true),
	ALARM_SUBORDINATE_PLATFORM_DATA_TRANSMISSION_ABNORMAL(0xA005, "下级平台数据传输异常", true),
	ALARM_ROAD_CONGESTION(0xA006, "路段堵塞报警", true),
	ALARM_DANGEROUS_ROAD(0xA007, "危险路段报警", true),
	ALARM_RAIN_SNOW_WEATHER(0xA008, "雨雪天气报警", true),
	ALARM_DRIVER_IDENTITY_RECOGNITION_ABNORMAL(0xA009, "驾驶员身份识别异常", true),
	ALARM_TERMINAL_ABNORMAL(0xA00A, "终端异常（含线路连接异常）", true), // AlarmBit 4，5，6，7，8，9，10，11，12
	ALARM_PLATFORM_ACCESS_ABNORMAL(0xA00B, "平台接入异常", true),
	ALARM_CORE_DATA_ABNORMAL(0xA00C, "核心数据异常", true),
	ALARM_NON_LOCATION_OTHER(0xA0FF, "其他报警", true);

	private int value;
	private String desc;
	private boolean isNonLocationAlarm;

	AlarmTypeEnum(int value, String desc, boolean isNonLocationAlarm) {
		this.value = value;
		this.desc = desc;
		this.isNonLocationAlarm = isNonLocationAlarm;
	}

    /**
     * 根据AlarmBit位索引值获取对应的AlarmTypeEnum枚举
     * @param alarmBitIndex AlarmBit的位索引值
     * @return 对应的AlarmTypeEnum枚举，如果没有匹配则返回null
     */
    public static AlarmTypeEnum getByAlarmBitIndex(int alarmBitIndex) {
        switch (alarmBitIndex) {
            case 0: // EMERGENCY_ALARM
                return ALARM_EMERGENCY;
            case 1: // OVERSPEED_ALARM
                return ALARM_OVERSPEED;
            case 2: // FATIGUE_DRIVING
                return ALARM_DRIVER_FATIGUE;
            case 15: // ILLEGAL_DRIVING_ALARM
                return ALARM_ILLEGAL_DRIVING;
            case 16: // ENGINE_FAULT (胎压报警)
                return ALARM_TIRE_PRESSURE;
            case 18: // DAY_OVERDRIVE
                return ALARM_DRIVER_OVERTIME;
            case 20: // ZONE_ENTER
                return ALARM_AREA_ENTER;
            case 21: // ROUTE_ENTER
                return ALARM_AREA_LEAVE;
            case 23: // ROUTE_DEVIATION
                return ALARM_LINE_OFFSET;
            case 26: // THEFT_ALARM
                return ALARM_STEAL;
            case 28: // ILLEGAL_MOVEMENT
                return ALARM_CAR_MOVE;
            case 4: // GNSS_MODULE_FAILURE
            case 5: // GNSS_ANTENNA_DISCONNECTED
            case 6: // GNSS_ANTENNA_SHORT_CIRCUIT
            case 7: // POWER_UNDERVOLTAGE
            case 8: // POWER_FAILURE
            case 9: // LCD_FAILURE
            case 10: // TTS_FAILURE
            case 11: // CAMERA_FAILURE
            case 12: // ICCARD_FAILURE
                return ALARM_TERMINAL_ABNORMAL;
            default:
                return null;
        }
    }

    /**
     * 根据报警位值获取触发的第一个AlarmTypeEnum枚举
     * @param alarmValue 报警位值（32位整数）
     * @return 对应的AlarmTypeEnum枚举，如果没有匹配则返回ALARM_OTHER
     */
    public static AlarmTypeEnum getByAlarmValue(int alarmValue) {
        // 检查每个位是否被触发，从低位到高位
        for (int i = 0; i < 32; i++) {
            if (((alarmValue >> i) & 0x1) == 1) {
                AlarmTypeEnum alarmType = getByAlarmBitIndex(i);
                if (alarmType != null) {
                    return alarmType;
                }
            }
        }
        return ALARM_OTHER;
    }

    /**
     * 根据AlarmBit位索引值判断是否为非位置相关报警
     * @param alarmBitIndex AlarmBit的位索引值
     * @return true-非位置相关报警，false-位置相关报警
     */
    public static boolean isNonLocationAlarmByBitIndex(int alarmBitIndex) {
        AlarmTypeEnum alarmType = getByAlarmBitIndex(alarmBitIndex);
        if (alarmType == null) {
            return false;
        }
        return alarmType.isNonLocationAlarm();
    }
    
    /**
     * 根据报警类型值判断是否为非位置相关报警
     * @param alarmTypeValue 报警类型值
     * @return true-非位置相关报警，false-位置相关报警
     */
    public static boolean isNonLocationAlarmByValue(int alarmTypeValue) {
        for (AlarmTypeEnum alarmType : values()) {
            if (alarmType.getValue() == alarmTypeValue) {
                return alarmType.isNonLocationAlarm();
            }
        }
        return false;
    }

    /**
     * 根据报警类型值查找对应的枚举
     * @param alarmTypeValue 报警类型值
     * @return 对应的AlarmTypeEnum枚举，如果没有匹配则返回null
     */
    public static AlarmTypeEnum getByValue(int alarmTypeValue) {
        for (AlarmTypeEnum alarmType : values()) {
            if (alarmType.getValue() == alarmTypeValue) {
                return alarmType;
            }
        }
        return null;
    }

}
