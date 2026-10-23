package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.authorize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;

/**
 * 时效口令请求应答消息
 * <p>
 * 链路类型：从链路
 * 消息方向：上级政府视频监管平台向跨域地政府视频监管平台
 * 子业务类型标识：DOWN AUTHORIZE MSG STARTUP REQ ACK 。
 * 描述：上级政府视频监管平台应答跨域地政府视频监管平台发送的时效口令请求消息，上级政府视
 * 频监管平台根据请求车辆 5min 之内的地理位置确定应答的内容。 数据体见表 41。
 *
 * @author syz
 * @since 2025/10/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DownAuthorizeMsgStartupReqAck extends Base1078Entity {
    private byte[] code1;
    private byte[] code2;
}
