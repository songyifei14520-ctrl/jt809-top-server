package cn.com.onlinetool.jt809.netty.codec.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;

public class BufUtil {

	/**
	 * bytebuf合并
	 */
	public static ByteBuf compositeByteBufs(ByteBuf... bufs) {
		return Unpooled.wrappedBuffer(bufs);
	}

	public static byte[] bufToBytes(ByteBuf buf) {

		byte[] bytes = new byte[buf.readableBytes()];
		buf.getBytes(0, bytes);
		return bytes;
	}

	public static ByteBuf bytesToBuf(byte[] bytes) {
		return Unpooled.wrappedBuffer(bytes);
	}

	/**
	 * ByteBuf 转 十六进制字符串
	 *
	 */
	public static String buf2HexStr(ByteBuf buf) {
		if (buf == null || buf.readableBytes() <= 0) {
			return null;
		}
		return ByteBufUtil.hexDump(buf);
	}

	/**
	 * 十六进制字符串 转 ByteBuf
	 *
	 */
	public static ByteBuf hexStr2Buf(String hexStr) {

		if (hexStr.length() % 2 != 0) {
			throw new IllegalArgumentException(hexStr);
		}

		return Unpooled.wrappedBuffer(ByteUtil.hex2Bytes(hexStr));
	}

    public static long buf2UnsignedLong(ByteBuf buf) {
        if (buf.readableBytes() != 4) {
            throw new RuntimeException("只有4个字节才能转为无符号long");
        }

        // 显式指定字节序（根据协议要求选择）
        buf = buf.order(ByteOrder.BIG_ENDIAN);
        return buf.readUnsignedInt(); // 使用Netty内置方法替代手工计算
    }

	public static long buf2SignedLong(ByteBuf buf) {

		if (buf.readableBytes() != 8) {
			throw new RuntimeException("只有8个字节才能转为有符号long");
		}

		// 显式指定字节序（根据协议要求选择）
		buf = buf.order(ByteOrder.BIG_ENDIAN);
		// 使用Netty内置方法替代手动位操作
		return buf.getLong(0);
	}

	public static int buf2UnsignedInt(ByteBuf buf) {
		if (buf.readableBytes() != 2) {
			throw new RuntimeException("只有2个字节才能转为无符号int");
		}

		// 使用Netty内置方法替代手动位操作
		return buf.getUnsignedShort(0);
	}

	public static int buf2UnsignedShort(ByteBuf buf) {
		if (buf.readableBytes() != 2) {
			throw new RuntimeException("只有2个字节才能转为无符号short");
		}

		// 使用Netty内置方法替代手动位操作
		return buf.getUnsignedShort(0);
	}

}
