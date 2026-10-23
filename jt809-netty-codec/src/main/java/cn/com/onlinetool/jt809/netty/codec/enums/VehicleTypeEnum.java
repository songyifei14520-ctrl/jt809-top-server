package cn.com.onlinetool.jt809.netty.codec.enums;

import lombok.Getter;

/**
 * 车辆类型枚举
 */
@Getter
public enum VehicleTypeEnum {

    // 客车类型
    LARGE_BUS("K10", "大型客车"),
    LARGE_ORDINARY_BUS("K11", "大型普通客车"),
    LARGE_DOUBLE_DECKER_BUS("K12", "大型双层客车"),
    LARGE_BED_BUS("K13", "大型卧铺客车"),
    LARGE_ARTICULATED_BUS("K14", "大型铰接客车"),
    LARGE_OFF_ROAD_BUS("K15", "大型越野客车"),
    LARGE_SEDAN("K16", "大型轿车"),
    LARGE_SPECIAL_PURPOSE_BUS("K17", "大型专用客车"),
    MEDIUM_BUS("K20", "中型客车"),
    MEDIUM_ORDINARY_BUS("K21", "中型普通客车"),
    MEDIUM_DOUBLE_DECKER_BUS("K22", "中型双层客车"),
    MEDIUM_BED_BUS("K23", "中型卧铺客车"),
    MEDIUM_ARTICULATED_BUS("K24", "中型铰接客车"),
    MEDIUM_OFF_ROAD_BUS("K25", "中型越野客车"),
    MEDIUM_SPECIAL_PURPOSE_BUS("K27", "中型专用客车"),
    SMALL_BUS("K30", "小型客车"),
    SMALL_ORDINARY_BUS("K31", "小型普通客车"),
    SMALL_OFF_ROAD_BUS("K32", "小型越野客车"),
    SMALL_SEDAN("K33", "小型轿车"),
    SMALL_SPECIAL_PURPOSE_BUS("K34", "小型专用客车"),
    MICRO_BUS("K40", "微型客车"),
    MICRO_ORDINARY_BUS("K41", "微型普通客车"),
    MICRO_OFF_ROAD_BUS("K42", "微型越野客车"),
    MICRO_SEDAN("K43", "微型轿车"),

    // 货车类型
    HEAVY_TRUCK("H10", "重型货车"),
    HEAVY_ORDINARY_TRUCK("H11", "重型普通货车"),
    HEAVY_BOX_TRUCK("H12", "重型厢式货车"),
    HEAVY_CLOSED_TRUCK("H13", "重型封闭货车"),
    HEAVY_TANK_TRUCK("H14", "重型罐式货车"),
    HEAVY_FLAT_TRUCK("H15", "重型平板货车"),
    HEAVY_CONTAINER_BOX_TRUCK("H16", "重型集装箱车"),
    HEAVY_SELF_UNLOADING_TRUCK("H17", "重型自卸货车"),
    HEAVY_SPECIAL_STRUCTURE_TRUCK("H18", "重型特殊结构货车"),
    HEAVY_WAREHOUSE_BOX_TRUCK("H19", "重型仓栅式货车"),
    MEDIUM_TRUCK("H20", "中型货车"),
    MEDIUM_ORDINARY_TRUCK("H21", "中型普通货车"),
    MEDIUM_BOX_TRUCK("H22", "中型厢式货车"),
    MEDIUM_CLOSED_TRUCK("H23", "中型封闭货车"),
    MEDIUM_TANK_TRUCK("H24", "中型罐式货车"),
    MEDIUM_FLAT_TRUCK("H25", "中型平板货车"),
    MEDIUM_CONTAINER_BOX_TRUCK("H26", "中型集装箱车"),
    MEDIUM_SELF_UNLOADING_TRUCK("H27", "中型自卸货车"),
    MEDIUM_SPECIAL_STRUCTURE_TRUCK("H28", "中型特殊结构货车"),
    MEDIUM_WAREHOUSE_BOX_TRUCK("H29", "中型仓栅式货车"),
    LIGHT_TRUCK("H30", "轻型货车"),
    LIGHT_ORDINARY_TRUCK("H31", "轻型普通货车"),
    LIGHT_BOX_TRUCK("H32", "轻型厢式货车"),
    LIGHT_CLOSED_TRUCK("H33", "轻型封闭货车"),
    LIGHT_TANK_TRUCK("H34", "轻型罐式货车"),
    LIGHT_FLAT_TRUCK("H35", "轻型平板货车"),
    LIGHT_SELF_UNLOADING_TRUCK("H37", "轻型自卸货车"),
    LIGHT_SPECIAL_STRUCTURE_TRUCK("H38", "轻型特殊结构货车"),
    LIGHT_WAREHOUSE_BOX_TRUCK("H39", "轻型仓栅式货车"),
    MICRO_TRUCK("H40", "微型货车"),

    // 微型货车类型
    MICRO_ORDINARY_TRUCK("H41", "微型普通货车"),
    MICRO_BOX_TRUCK("H42", "微型厢式货车"),
    MICRO_CLOSED_TRUCK("H43", "微型封闭货车"),
    MICRO_TANK_TRUCK("H44", "微型罐式货车"),
    MICRO_SELF_UNLOADING_TRUCK("H45", "微型自卸货车"),
    MICRO_SPECIAL_STRUCTURE_TRUCK("H46", "微型特殊结构货车"),
    MICRO_WAREHOUSE_BOX_TRUCK("H47", "微型仓栅式货车"),

    // 低速货车类型
    LOW_SPEED_TRUCK("H50", "低速货车"),
    ORDINARY_LOW_SPEED_TRUCK("H51", "普通低速货车"),
    BOX_LOW_SPEED_TRUCK("H52", "厢式低速货车"),

    // 罐式低速货车类型
    TANK_LOW_SPEED_TRUCK("H53", "罐式低速货车"),
    SELF_UNLOADING_LOW_SPEED_TRUCK("H54", "自卸低速货车"),
    WAREHOUSE_BOX_LOW_SPEED_TRUCK("H55", "仓栅式低速货车"),

    // 重型牵引车类型
    HEAVY_DUTY_TRACTION_VEHICLE("Q10", "重型牵引车"),
    HEAVY_HALF_TRAILER_TRACTION_VEHICLE("Q11", "重型半挂牵引车"),
    HEAVY_FULL_TRAILER_TRACTION_VEHICLE("Q12", "重型全挂牵引车"),

    // 中型牵引车类型
    MEDIUM_DUTY_TRACTION_VEHICLE("Q20", "中型牵引车"),
    MEDIUM_HALF_TRAILER_TRACTION_VEHICLE("Q21", "中型半挂牵引车"),
    MEDIUM_FULL_TRAILER_TRACTION_VEHICLE("Q22", "中型全挂牵引车"),

    // 轻型牵引车类型
    LIGHT_DUTY_TRACTION_VEHICLE("Q30", "轻型牵引车"),
    LIGHT_HALF_TRAILER_TRACTION_VEHICLE("Q31", "轻型半挂牵引车"),
    LIGHT_FULL_TRAILER_TRACTION_VEHICLE("Q32", "轻型全挂牵引车"),

    // 大型专项作业车类型
    LARGE_SPECIAL_PURPOSE_WORK_VEHICLE("Z11", "大型专项作业车"),

    // 中型专项作业车类型
    MEDIUM_SPECIAL_PURPOSE_WORK_VEHICLE("Z21", "中型专项作业车"),

    // 小型专项作业车类型
    SMALL_SPECIAL_PURPOSE_WORK_VEHICLE("Z31", "小型专项作业车"),

    // 微型专项作业车类型
    MICRO_SPECIAL_PURPOSE_WORK_VEHICLE("Z41", "微型专项作业车"),

    // 重型专项作业车类型
    HEAVY_SPECIAL_PURPOSE_WORK_VEHICLE("Z51", "重型专项作业车"),

    // 轻型专项作业车类型
    LIGHT_SPECIAL_PURPOSE_WORK_VEHICLE("Z71", "轻型专项作业车"),

    // 无轨电车类型
    TRACKLESS_TROLLEY_BUS("D11", "无轨电车"),

    // 有轨电车类型
    TRAM("D12", "有轨电车"),

    // 三轮摩托车类型
    THREE_WHEEL_MOTORCYCLE("M10", "三轮摩托车"),
    ORDINARY_THREE_WHEEL_MOTORCYCLE("M11", "普通正三轮摩托车"),
    LIGHT_THREE_WHEEL_MOTORCYCLE("M12", "轻便正三轮摩托车"),
    PASSENGER_THREE_WHEEL_MOTORCYCLE("M13", "正三轮载客摩托车"),
    CARGO_THREE_WHEEL_MOTORCYCLE("M14", "正三轮载货摩托车"),
    SIDE_THREE_WHEEL_MOTORCYCLE("M15", "侧三轮摩托车"),

    // 二轮摩托车类型
    TWO_WHEEL_MOTORCYCLE("M20", "二轮摩托车"),
    ORDINARY_TWO_WHEEL_MOTORCYCLE("M21", "普通二轮摩托车"),
    LIGHT_TWO_WHEEL_MOTORCYCLE("M22", "轻便二轮摩托车"),

    // 三轮汽车类型
    THREE_WHEEL_CAR("N11", "三轮汽车"),

    // 大型轮式拖拉机类型
    LARGE_WHEELED_TRACTOR("T11", "大型轮式拖拉机"),

    // 小型拖拉机类型
    SMALL_TRACTOR("T20", "小型拖拉机"),

    // 小型轮式拖拉机类型
    SMALL_WHEELED_TRACTOR("T21", "小型轮式拖拉机"),

    // 手扶拖拉机类型
    HAND_HELD_TRACTOR("T22", "手扶拖拉机"),

    // 手扶变形运输机类型
    HAND_HELD_TRANSFORMABLE_TRANSPORT_MACHINE("T23", "手扶变形运输机"),

    // 轮式装载机械类型
    WHEELED_LOADING_MACHINE("J11", "轮式装载机械"),

    // 轮式挖掘机械类型
    WHEELED_DIGGING_MACHINE("J12", "轮式挖掘机械"),

    // 轮式平地机械类型
    WHEELED_GRADING_MACHINE("J13", "轮式平地机械"),

    // 重型全挂车类型
    HEAVY_DUTY_FULL_TRAILER("G10", "重型全挂车"),
    HEAVY_ORDINARY_FULL_TRAILER("G11", "重型普通全挂车"),
    HEAVY_BOX_FULL_TRAILER("G12", "重型厢式全挂车"),
    HEAVY_TANK_FULL_TRAILER("G13", "重型罐式全挂车"),
    HEAVY_FLAT_FULL_TRAILER("G14", "重型平板全挂车"),
    HEAVY_CONTAINER_FULL_TRAILER("G15", "重型集装箱全挂车"),
    HEAVY_SELF_UNLOADING_FULL_TRAILER("G16", "重型自卸全挂车"),
    HEAVY_WAREHOUSE_BOX_FULL_TRAILER("G17", "重型仓栅式全挂车"),
    HEAVY_LIVING_QUARTERS_FULL_TRAILER("G18", "重型旅居全挂车"),
    HEAVY_SPECIAL_PURPOSE_FULL_TRAILER("G19", "重型专项作业全挂车"),

    // 中型全挂车类型
    MEDIUM_DUTY_FULL_TRAILER("G20", "中型全挂车"),
    MEDIUM_ORDINARY_FULL_TRAILER("G21", "中型普通全挂车"),
    MEDIUM_BOX_FULL_TRAILER("G22", "中型厢式全挂车"),
    MEDIUM_TANK_FULL_TRAILER("G23", "中型罐式全挂车"),
    MEDIUM_FLAT_FULL_TRAILER("G24", "中型平板全挂车"),
    MEDIUM_CONTAINER_FULL_TRAILER("G25", "中型集装箱全挂车"),
    MEDIUM_SELF_UNLOADING_FULL_TRAILER("G26", "中型自卸全挂车"),
    MEDIUM_WAREHOUSE_BOX_FULL_TRAILER("G27", "中型仓栅式全挂车"),
    MEDIUM_LIVING_QUARTERS_FULL_TRAILER("G28", "中型旅居全挂车"),

    // 中型全挂车类型（续）
    MEDIUM_SPECIAL_PURPOSE_FULL_TRAILER("G29", "中型专项作业全挂车"),

    // 轻型全挂车类型
    LIGHT_DUTY_FULL_TRAILER("G30", "轻型全挂车"),
    LIGHT_ORDINARY_FULL_TRAILER("G31", "轻型普通全挂车"),
    LIGHT_BOX_FULL_TRAILER("G32", "轻型厢式全挂车"),
    LIGHT_TANK_FULL_TRAILER("G33", "轻型罐式全挂车"),
    LIGHT_FLAT_FULL_TRAILER("G34", "轻型平板全挂车"),
    LIGHT_SELF_UNLOADING_FULL_TRAILER("G35", "轻型自卸全挂车"),
    LIGHT_WAREHOUSE_BOX_FULL_TRAILER("G36", "轻型仓栅式全挂车"),
    LIGHT_LIVING_QUARTERS_FULL_TRAILER("G37", "轻型旅居全挂车"),
    LIGHT_SPECIAL_PURPOSE_FULL_TRAILER("G38", "轻型专项作业全挂车"),

    // 重型半挂车类型
    HEAVY_DUTY_SEMI_TRAILER("B10", "重型半挂车"),
    HEAVY_ORDINARY_SEMI_TRAILER("B11", "重型普通半挂车"),
    HEAVY_BOX_SEMI_TRAILER("B12", "重型厢式半挂车"),
    HEAVY_TANK_SEMI_TRAILER("B13", "重型罐式半挂车"),
    HEAVY_FLAT_SEMI_TRAILER("B14", "重型平板半挂车"),
    HEAVY_CONTAINER_SEMI_TRAILER("B15", "重型集装箱半挂车"),
    HEAVY_SELF_UNLOADING_SEMI_TRAILER("B16", "重型自卸半挂车"),
    HEAVY_SPECIAL_STRUCTURE_SEMI_TRAILER("B17", "重型特殊结构半挂车"),
    HEAVY_WAREHOUSE_BOX_SEMI_TRAILER("B18", "重型仓栅式半挂车"),
    HEAVY_LIVING_QUARTERS_SEMI_TRAILER("B19", "重型旅居半挂车"),

    // 重型专项作业半挂车类型
    HEAVY_SPECIAL_PURPOSE_WORK_SEMI_TRAILER("B1A", "重型专项作业半挂车"),

    // 重型低平板半挂车类型
    HEAVY_LOW_FLAT_SEMI_TRAILER("B1B", "重型低平板半挂车"),

    // 中型半挂车类型
    MEDIUM_DUTY_SEMI_TRAILER("B20", "中型半挂车"),
    MEDIUM_ORDINARY_SEMI_TRAILER("B21", "中型普通半挂车"),
    MEDIUM_BOX_SEMI_TRAILER("B22", "中型厢式半挂车"),
    MEDIUM_TANK_SEMI_TRAILER("B23", "中型罐式半挂车"),
    MEDIUM_FLAT_SEMI_TRAILER("B24", "中型平板半挂车"),
    MEDIUM_CONTAINER_SEMI_TRAILER("B25", "中型集装箱半挂车"),
    MEDIUM_SELF_UNLOADING_SEMI_TRAILER("B26", "中型自卸半挂车"),
    MEDIUM_SPECIAL_STRUCTURE_SEMI_TRAILER("B27", "中型特殊结构半挂车"),
    MEDIUM_WAREHOUSE_BOX_SEMI_TRAILER("B28", "中型仓栅式半挂车"),
    MEDIUM_LIVING_QUARTERS_SEMI_TRAILER("B29", "中型旅居半挂车"),

    // 中型专项作业半挂车类型
    MEDIUM_SPECIAL_PURPOSE_WORK_SEMI_TRAILER("B2A", "中型专项作业半挂车"),

    // 中型低平板半挂车类型
    MEDIUM_LOW_FLAT_SEMI_TRAILER("B2B", "中型低平板半挂车"),

    // 轻型半挂车类型
    LIGHT_DUTY_SEMI_TRAILER("B30", "轻型半挂车"),
    LIGHT_ORDINARY_SEMI_TRAILER("B31", "轻型普通半挂车"),
    LIGHT_BOX_SEMI_TRAILER("B32", "轻型厢式半挂车"),
    LIGHT_TANK_SEMI_TRAILER("B33", "轻型罐式半挂车"),
    LIGHT_FLAT_SEMI_TRAILER("B34", "轻型平板半挂车"),
    LIGHT_SELF_UNLOADING_SEMI_TRAILER("B35", "轻型自卸半挂车"),
    LIGHT_WAREHOUSE_BOX_SEMI_TRAILER("B36", "轻型仓栅式半挂车"),
    LIGHT_LIVING_QUARTERS_SEMI_TRAILER("B37", "轻型旅居半挂车"),
    LIGHT_SPECIAL_PURPOSE_WORK_SEMI_TRAILER("B38", "轻型专项作业半挂车"),

    // 轻型低平板半挂车类型
    LIGHT_LOW_FLAT_SEMI_TRAILER("B39", "轻型低平板半挂车"),

    // 其他类型
    OTHER("X99", "其他");



    private final String code;
    private final String name;

    VehicleTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值
     *
     * @param code 代码
     * @return 枚举值
     */
    public static VehicleTypeEnum ofCode(String code) {
        for (VehicleTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取代码
     *
     * @return 代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }
}
