package cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.authorize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.com.onlinetool.jt809.netty.codec.entity.jtt1078.Base1078Entity;

/**
 * 时效口令请求消息
 * <p>
 * 链路类型：主链路
 * 消息方向：跨域地政府视频监管平台向上级政府视频监管平台
 * 子业务类型标识：UP AUTHORIZE MSG STARTUP REQ。
 * 描述：跨域地政府视频监管平台向上级政府视频监管平台获取指定车辆所在企业视频监控平台的当日时效口令，数据体见表 40。
 *
 * @author syz
 * @since 2025/10/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpAuthorizeMsgStartupReq extends Base1078Entity {
}
