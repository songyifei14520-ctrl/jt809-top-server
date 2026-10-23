package cn.com.onlinetool.jt809.netty.codec.entity.exgMsg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.com.onlinetool.jt809.netty.codec.JT809Config;
import cn.com.onlinetool.jt809.netty.codec.MsgConstant;
import cn.com.onlinetool.jt809.netty.codec.entity.Codec;
import cn.com.onlinetool.jt809.netty.codec.util.BufUtil;
import cn.com.onlinetool.jt809.netty.codec.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 链路类型:主链路 车辆定位信息自动补报请求消息 子业务类型标识:UP_EXG_MSG_HISTORY_LOCATION
 * 描述:如果平台间传输链路中断，下级平台重新登录并与上级平台建立通信链路后，下级平台应将中断期间内车载终端上传的车辆定位信息自动补报到上级平台。如果系统断线期间，
 * 该车需发送的数据包条数大于5，则以每包五条进行补发，直到补发完毕。多条数据以卫星定位时间先后顺序排列。本条消息上级平台采用定量回复，即收到一定数量的数据后，即通过从链路应答数据量
 */
@Setter
@Getter
@NoArgsConstructor
public class HistoryLocationEntity implements Codec<HistoryLocationEntity> {
	private BaseExgMsgEntity baseMsg;
	private byte gnssCnt; // 该数据包里包含的卫星定位数据个数1<=gnssCnt<=5 byte[1]
	private List<byte[]> gnssData; // 卫星定位数据 byte[36]

	@Override
	public HistoryLocationEntity decode(ByteBuf buf) {
		this.baseMsg = new BaseExgMsgEntity().decode(buf);
		this.gnssCnt = baseMsg.getData().getByte(0);
		this.gnssData = new ArrayList<>();

        if (!JT809Config.isJT809_2019()) {
            // JT809-2011版本：固定长度36字节
            for (int i = 0; i < this.gnssCnt; i++) {
                this.gnssData.add(BufUtil.bufToBytes(baseMsg.getData().slice(i * 36 + 1, 36)));
            }
        } else {
            // JT809-2019版本：动态长度解析
            ByteBuf data = baseMsg.getData();
            int offset = 1; // 跳过gnssCnt字段

            for (int i = 0; i < this.gnssCnt; i++) {
                if (offset >= data.readableBytes()) {
                    break; // 数据不足，停止解析
                }

                // 读取加密标识（1字节）
                byte encrypt = data.getByte(offset);
                offset += 1;

                // 读取数据长度（4字节）
                if (offset + 4 > data.readableBytes()) {
                    break; // 数据不足，停止解析
                }
                long dataLength = BufUtil.buf2UnsignedLong(data.slice(offset, 4));
                offset += 4;

                // 计算当前GNSS数据项的总长度：1(encrypt) + 4(dataLength) + dataLength + 11(platformId1) + 4(alarm1) + 11(platformId2) + 4(alarm2) + 11(platformId3) + 4(alarm3)
                int totalItemLength = 1 + 4 + (int)dataLength + 11 + 4 + 11 + 4 + 11 + 4;

                // 检查剩余数据是否足够
                if (offset - 5 + totalItemLength > data.readableBytes()) {
                    break; // 数据不足，停止解析
                }

                // 提取完整的GNSS数据项（从encrypt开始）
                byte[] gnssDataItem = BufUtil.bufToBytes(data.slice(offset - 5, totalItemLength));
                this.gnssData.add(gnssDataItem);

                // 移动到下一个数据项
                offset = offset - 5 + totalItemLength;
            }
        }

		return this;
	}

	public static HistoryLocationEntity create(String vehicleNo, byte vehicleColor, List<byte[]> gnssDataList) {
		HistoryLocationEntity entity = new HistoryLocationEntity();
		entity.gnssCnt = (byte) gnssDataList.size();

		List<byte[]> gnssDataBytesList = new ArrayList<>();
		for (byte[] gnssData : gnssDataList) {
            byte[] gnssDataBytes;
            if(JT809Config.isJT809_2019()){
                // JT809-2019版本：保持原始动态长度
                gnssDataBytes = new byte[gnssData.length];
                System.arraycopy(gnssData, 0, gnssDataBytes, 0, gnssData.length);
            }else{
                // JT809-2011版本：固定长度36字节
                gnssDataBytes = new byte[36];
                System.arraycopy(gnssData, 0, gnssDataBytes, 0, gnssData.length > 36 ? 36 : gnssData.length);
            }

			gnssDataBytesList.add(gnssDataBytes);
		}
		entity.gnssData = gnssDataBytesList;

		// 根据协议版本计算结果数组长度
		byte[] result;
		if(JT809Config.isJT809_2019()){
			// JT809-2019版本：动态长度，计算所有GNSS数据的总长度
			int totalLength = 0;
			for (byte[] gnssDataBytes : gnssDataBytesList) {
				totalLength += gnssDataBytes.length;
			}
			result = new byte[totalLength];

			// 拷贝所有GNSS数据
			int offset = 0;
			for (byte[] gnssDataBytes : gnssDataBytesList) {
				System.arraycopy(gnssDataBytes, 0, result, offset, gnssDataBytes.length);
				offset += gnssDataBytes.length;
			}
		} else {
			// JT809-2011版本：固定长度36字节 * 数量
			result = new byte[entity.gnssCnt * 36];
			for (int i = 0; i < gnssDataBytesList.size(); i++) {
				System.arraycopy(gnssDataBytesList.get(i), 0, result, i * 36, 36);
			}
		}

		entity.baseMsg = BaseExgMsgEntity.create(
            MsgConstant.PrimaryServiceType.UP_EXG_MSG,
            vehicleNo, vehicleColor,
				MsgConstant.SubServiceType.UP_EXG_MSG_HISTORY_LOCATION,
				ByteUtil.combineByteArray(new byte[] { entity.gnssCnt }, result));
		return entity;
	}

	@Override
	public byte[] encode() {
		return baseMsg.encode();
	}
}
