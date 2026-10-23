package cn.com.onlinetool.jt809.rest;

import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.com.onlinetool.jt809.server.JT809Server;
import cn.com.onlinetool.jt809.util.ByteArrayUtil;
import cn.com.onlinetool.jt809.util.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.jt809.netty.codec.entity.linkManager.SubDisConnectReqEntity;
import org.dromara.common.jt809.netty.codec.util.BufUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/slave/downLine")
public class DownSlaveLineRest {

    @Resource
    TcpChannelMsgManage channelMsgManage;

    @Autowired
    JT809Server jt809Server;

    private static final String clentKey = "clientChannel";


    /**
     * 测试下行消息
     * 启动项目后调用 JT809TcpClient模拟登录
     * 然后浏览器访问 http://localhost:8888/downLine/test/123456 触发下行消息
     * @param key
     * @return
     */
    @GetMapping("/test/{key}")
    public String testDownLine(@PathVariable String key){
        Channel channel = channelMsgManage.getChannel(clentKey);
        if(channel == null){
            return "The key is wrong";
        }
        if(channel.isActive()){
            byte[] testLogin = ByteArrayUtil.hexStr2Bytes("5B0000001A0000257A10050001E24001000100000000001C525D");
            Message message = PacketUtil.bytes2Message(testLogin);
            channel.writeAndFlush(message);
            return "success";
        }
        return "fail";
    }

    /**
     * 9001 从链路--从链路连接请求消息从链路
     * @param request 连接请求消息
     * @return 连接响应
     * todo 暂时自动连接
     */

    /**
     * 9003 从链路--从链路注销请求消息
     * @return 注销响应
     */
    @PostMapping("/disconnect")
    public ResponseEntity<SubDisConnectReqEntity> handleDisconnect() {
        // 1. 参数校验
        SubDisConnectReqEntity response = SubDisConnectReqEntity.create(0);

        // 2. 执行注销逻辑
        try {
            // 获取对应TCP通道
            Channel channel = channelMsgManage.getChannel(clentKey);
            if (channel != null && channel.isActive()) {
                // 发送协议要求的断开指令
                byte[] disconnectCmd = response.encode();

                ByteBuf copiedBuf = Unpooled.copiedBuffer(disconnectCmd);
                log.info("从链路注销报文:{}", BufUtil.buf2HexStr(copiedBuf));

                Message message = PacketUtil.bytes2Message(disconnectCmd);
                channel.writeAndFlush(message);

                // 更新通道状态
                log.info("从链路注销成功");

                return ResponseEntity.ok(new SubDisConnectReqEntity());
            }
            return ResponseEntity.internalServerError().body(new SubDisConnectReqEntity());
        } catch (Exception e) {
            log.error("从链路注销异常：{}",  e);
            return ResponseEntity.internalServerError().body(new SubDisConnectReqEntity());
        }
    }

    //9103
    //9102
    //1006
    //9005
    //1004
    //4007

    //9008


}
