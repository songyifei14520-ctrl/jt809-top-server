package cn.com.onlinetool.jt809.netty.codec;

import java.util.Objects;

public class JT809Config {
	private static final String DEFAULT_VERSIION = "1.2.15";
	public static byte[] VERSION_FLAG;

	/**
	 * 协议版本标识：true表示JT809-2019，false表示JT809-2011
	 */
	public static boolean isJT809_2019 = false;

	public static int M1;
	public static int IA1;
	public static int IC1;

	public static boolean encrypt = true;

    public static long encryptkey = 15241;

	public static long userId;
	public static String password;
	public static String localIp;
	public static Integer localPort;

    public static String serverIp;

    public static Integer serverPort;
	public static long msmsgGnssscenterId;

    private static String platformId;

    private static String rtoln;

    private static String owersName;

    static {
		initVersion(DEFAULT_VERSIION);
	}


	public static void init(long userId, String password, String localIp, Integer localPort, long msgGnssscenterId) {
		init(userId, password, localIp, localPort, msgGnssscenterId, DEFAULT_VERSIION);
	}

	public static void init(long userId, String password, String localIp, Integer localPort, long msgGnssscenterId,
			String version) {
		init(userId, password, localIp, localPort, msgGnssscenterId, version, null, null, null);
	}

	/**
	 * 初始化配置（包含协议版本标识）
	 */
	public static void init(long userId, String password, String localIp, Integer localPort, long msgGnssscenterId,
			String version, boolean isJT809_2019) {
		init(userId, password, localIp, localPort, msgGnssscenterId, version, null, null, null);
		JT809Config.isJT809_2019 = isJT809_2019;
	}

	/**
	 * 初始化配置（包含协议版本标识和加密参数）
	 */
	public static void init(long userId, String password, String localIp, Integer localPort, long msgGnssscenterId,
			String version, boolean isJT809_2019, Integer M1, Integer IA1, Integer IC1) {
		init(userId, password, localIp, localPort, msgGnssscenterId, version, M1, IA1, IC1);
		JT809Config.isJT809_2019 = isJT809_2019;
	}

	public static void init(long userId, String password, String localIp, Integer localPort, long msgGnssscenterId,
			String version, Integer M1, Integer IA1, Integer IC1) {
		JT809Config.userId = userId;
		JT809Config.password = password;
		JT809Config.localIp = localIp;
		JT809Config.localPort = localPort;
		JT809Config.msmsgGnssscenterId = msgGnssscenterId;

		initVersion(version);

		if (Objects.nonNull(M1) && Objects.nonNull(IA1) && Objects.nonNull(IC1)) {
			encrypt = true;
			JT809Config.M1 = M1;
			JT809Config.IA1 = IA1;
			JT809Config.IC1 = IC1;
		}
	}

    public static void initUpperPlatformInfo(String serverIp,Integer serverPort,long userId, String password,String localIp, Integer localPort,long msgGnssscenterId,String version
        , Integer M1, Integer IA1, Integer IC1) {
        JT809Config.userId = userId;
        JT809Config.password = password;
        JT809Config.msmsgGnssscenterId = msgGnssscenterId;
        JT809Config.serverIp = serverIp;
        JT809Config.serverPort = serverPort;
        JT809Config.localIp = localIp;
        JT809Config.localPort = localPort;

        initVersion(version);

        if (Objects.nonNull(M1) && Objects.nonNull(IA1) && Objects.nonNull(IC1)) {
            encrypt = true;
            JT809Config.M1 = M1;
            JT809Config.IA1 = IA1;
            JT809Config.IC1 = IC1;
        }
    }

    /**
     * 初始化上级平台信息（包含协议版本标识）
     */
    public static void initUpperPlatformInfo(String serverIp,Integer serverPort,long userId, String password,String localIp, Integer localPort,long msgGnssscenterId,String version
        , boolean isJT809_2019, Integer M1, Integer IA1, Integer IC1) {
        initUpperPlatformInfo(serverIp, serverPort, userId, password, localIp, localPort, msgGnssscenterId, version, M1, IA1, IC1);
        JT809Config.isJT809_2019 = isJT809_2019;
    }

    /**
     * 设置协议版本标识
     * @param isJT809_2019 true表示JT809-2019，false表示JT809-2011
     */
    public static void setProtocolVersion(boolean isJT809_2019) {
        JT809Config.isJT809_2019 = isJT809_2019;
    }

    /**
     * 获取协议版本标识
     * @return true表示JT809-2019，false表示JT809-2011
     */
    public static boolean isJT809_2019() {
        return isJT809_2019;
    }

	private static void initVersion(String version) {
		String[] tmp = version.split(".");
		if (tmp.length > 3) {
			throw new IllegalArgumentException("version参数有误 eg:1.2.15");
		}

		for (int i = 0; i < tmp.length; i++) {
			if (isNumeric(tmp[i])) {
				throw new IllegalArgumentException("version参数有误 eg:1.2.15");
			}
		}

		byte[] versionBytes = new byte[3];
		for (int i = 0; i < tmp.length; i++) {
			versionBytes[i] = Byte.parseByte(tmp[i]);
		}
		JT809Config.VERSION_FLAG = versionBytes;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

    public static void init(String platformId, String rtoln, String owersName) {
        JT809Config.platformId = platformId;
        JT809Config.rtoln = rtoln;
        JT809Config.owersName = owersName;
    }

    /**
     * 获取平台ID
     * @return 平台ID
     */
    public static String getPlatformId() {
        return platformId;
    }

    /**
     * 获取道路运输证号
     * @return 道路运输证号
     */
    public static String getRtoln() {
        return rtoln;
    }

    /**
     * 获取业户名称
     * @return 业户名称
     */
    public static String getOwersName() {
        return owersName;
    }
}
