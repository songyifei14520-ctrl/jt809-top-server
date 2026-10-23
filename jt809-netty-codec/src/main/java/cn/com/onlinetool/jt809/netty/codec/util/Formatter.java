package cn.com.onlinetool.jt809.netty.codec.util;

import java.util.concurrent.TimeUnit;

/**
 * TODO 概要描述
 * <p>
 * TODO 详细描述
 *
 * @author syz
 * create 2024/12/31
 * @since
 */
public class Formatter {

    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = KILOBYTE * KILOBYTE;
    private static final long GIGABYTE = MEGABYTE * KILOBYTE;
    private static final long TERABYTE = GIGABYTE * KILOBYTE;

    /**
     * 格式化字节数为人类可读的格式
     *
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    public static String formatBytes(double bytes) {
        if (bytes < KILOBYTE) {
            return bytes + " B";
        } else if (bytes < MEGABYTE) {
            return String.format("%.2f KB", bytes / KILOBYTE);
        } else if (bytes < GIGABYTE) {
            return String.format("%.2f MB", bytes / MEGABYTE);
        } else if (bytes < TERABYTE) {
            return String.format("%.2f GB", bytes / GIGABYTE);
        } else {
            return String.format("%.2f TB", bytes / TERABYTE);
        }
    }

    public static String formatTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

