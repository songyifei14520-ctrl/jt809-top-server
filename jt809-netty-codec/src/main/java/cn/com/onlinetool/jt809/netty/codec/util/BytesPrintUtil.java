package cn.com.onlinetool.jt809.netty.codec.util;

import io.netty.util.internal.StringUtil;

import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * Description:
 * Author:
 * Create: 2025/1/1 14:13
 * Version: 1.0
 */
public class BytesPrintUtil {
    private static final char[] BYTE2CHAR = new char[256];
    private static final String[] HEX_PADDING = new String[16];
    private static final String[] HEXDUMP_ROW_PREFIXES = new String[65536 >>> 4];
    private static final String[] BYTE2HEX = new String[256];
    private static final String[] BYTE_PADDING = new String[16];


    static {
        int i;
        // Generate the lookup table for hex dump paddings
        for (i = 0; i < HEX_PADDING.length; i++) {
            int padding = HEX_PADDING.length - i;
            HEX_PADDING[i] = "   ".repeat(Math.max(0, padding));
        }

        // Generate the lookup table for the start-offset header in each row (up to 64KiB).
        for (i = 0; i < HEXDUMP_ROW_PREFIXES.length; i++) {
            StringBuilder buf = new StringBuilder(12);
            buf.append(NEWLINE);
            buf.append(Long.toHexString((long) i << 4 & 0xFFFFFFFFL | 0x100000000L));
            buf.setCharAt(buf.length() - 9, '|');
            buf.append('|');
            HEXDUMP_ROW_PREFIXES[i] = buf.toString();
        }

        // Generate the lookup table for byte-to-hex-dump conversion
        for (i = 0; i < BYTE2HEX.length; i++) {
            BYTE2HEX[i] = ' ' + StringUtil.byteToHexStringPadded(i);
        }

        // Generate the lookup table for byte dump paddings
        for (i = 0; i < BYTE_PADDING.length; i++) {
            int padding = BYTE_PADDING.length - i;
            BYTE_PADDING[i] = " " .repeat(Math.max(0, padding));
        }

        // Generate the lookup table for byte-to-char conversion
        for (i = 0; i < BYTE2CHAR.length; i++) {
            if (i <= 0x1f || i >= 0x7f) {
                BYTE2CHAR[i] = '.';
            } else {
                BYTE2CHAR[i] = (char) i;
            }
        }
    }

    public static void prettyPrint(byte[] bytes) {
        prettyPrint(bytes, null);
    }

    public static void prettyPrint(byte[] bytes, String printBy) {
        System.out.println(getPrettyString(bytes, printBy));
    }

    public static String getPrettyString(byte[] bytes) {
        return getPrettyString(bytes, null);
    }

    public static String getPrettyString(byte[] bytes, String printBy) {
        if(bytes == null) {
            System.out.println("bytes is null");
            return "";
        }
        if (bytes.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(256);
        appendPrettyHexDump(builder, bytes, printBy);
        return builder.toString();
    }

    private static void appendPrettyHexDump(StringBuilder dump, byte[] bytes, String printBy) {
        String bytesSize = "total: " + Formatter.formatBytes(bytes.length);
        String bytesSizeFormat = String.format("%" + ((16-bytesSize.length())/2 + bytesSize.length())  + "s", bytesSize);
        String printByFormat = "";
        if(printBy != null && !printBy.isEmpty()) {
            printBy = "by: " + printBy;
            printByFormat = String.format("%" + ((16-printBy.length())/2 + printBy.length())  + "s", printBy);
        }
        dump.append("         +-------------------------------------------------+").append(printByFormat).append(NEWLINE)
            .append("         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |").append(bytesSizeFormat).append(NEWLINE)
            .append("+--------+-------------------------------------------------+----------------+");

        final int fullRows = bytes.length >>> 4;
        final int remainder = bytes.length & 0xF;

        // Dump the rows which have 16 bytes.
        for (int row = 0; row < fullRows; row++) {
            int rowStartIndex = (row << 4);

            // Per-row prefix.
            appendHexDumpRowPrefix(dump, row, rowStartIndex);

            // Hex dump
            int rowEndIndex = rowStartIndex + 16;
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2HEX[getUnsignedByte(bytes, j)]);
            }
            dump.append(" |");

            // ASCII dump
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2CHAR[getUnsignedByte(bytes, j)]);
            }
            dump.append('|');
        }

        // Dump the last row which has less than 16 bytes.
        if (remainder != 0) {
            int rowStartIndex = (fullRows << 4);
            appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);

            // Hex dump
            int rowEndIndex = rowStartIndex + remainder;
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2HEX[getUnsignedByte(bytes, j)]);
            }
            dump.append(HEX_PADDING[remainder]);
            dump.append(" |");

            // Ascii dump
            for (int j = rowStartIndex; j < rowEndIndex; j++) {
                dump.append(BYTE2CHAR[getUnsignedByte(bytes, j)]);
            }
            dump.append(BYTE_PADDING[remainder]);
            dump.append('|');
        }
        dump.append(NEWLINE)
                .append("+--------+-------------------------------------------------+----------------+");
    }

    private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
        if (row < HEXDUMP_ROW_PREFIXES.length) {
            dump.append(HEXDUMP_ROW_PREFIXES[row]);
        } else {
            dump.append(NEWLINE);
            dump.append(Long.toHexString(rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
            dump.setCharAt(dump.length() - 9, '|');
            dump.append('|');
        }
    }
    private static short getUnsignedByte(byte[] bytes, int index) {
        return (short) (bytes[index] & 0xFF);
    }
}
