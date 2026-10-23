package cn.com.onlinetool.jt809.rest;


import cn.com.onlinetool.jt809.bean.Message;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.com.onlinetool.jt809.util.ByteArrayUtil;
import cn.com.onlinetool.jt809.util.PacketUtil;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/master/downLine")
public class DownMasterLineRest {
    @Resource
    TcpChannelMsgManage channelMsgManage;


    /**
     * 测试下行消息
     * 启动项目后调用 JT809TcpClient模拟登录
     * 然后浏览器访问 http://localhost:8888/downLine/test/123456 触发下行消息
     * @param key
     * @return
     */
    @GetMapping("/test/{key}")
    public String testDownLine(@PathVariable String key){
        Channel channel = channelMsgManage.getChannel(key);
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



}
