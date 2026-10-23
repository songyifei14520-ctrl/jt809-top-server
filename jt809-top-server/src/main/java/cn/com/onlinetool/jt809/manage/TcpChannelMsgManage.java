package cn.com.onlinetool.jt809.manage;

import cn.com.onlinetool.jt809.config.BusinessConfig;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author choice
 * @description: Tcp服务 计数器 缓存等 管理
 * @date 2019-01-10 11:38
 *
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class TcpChannelMsgManage {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    BusinessConfig businessConfig;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        RedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializer);
        redisTemplate.afterPropertiesSet();
        this.redisTemplate = redisTemplate;
    }

    /**
     * 缓存本地ip地址信息，避免每次都重新调用系统函数
     */
    private static final Map<String,String> HOST_MAP = new ConcurrentHashMap<String,String>();

    /**
     * 解析后的设备上报的原始数据输出topic
     */
    private static final String BASE_DATA_INOUT_TOPIC = "base_data_input";

    /**
     * 缓存客户端通道所在节点 ip 和 tid 的对应关系
     */
    private static final String TCP_CHANNEL_PREFIX_REDIS_KEY = "ausiaev_iot_tcp_channel_";

    /**
     * 客户端通道缓存 用于下发消息
     */
    private static final Map<String,Channel> CHANNEL_MAP = new ConcurrentHashMap<String,Channel>();


    /**
     * 客户端连接计数器 单点
     */
    private static final AtomicInteger TCP_CLIENT_CONNECT_NUM = new AtomicInteger(0);


    /**
     * 下发消息缓存 key
     */
    private static final String TCP_CHANNEL_DOWNLINK_MSG_REDIS_KEY = "ausiaev_iot_tcp_downlink_msg_";
    private String getTcpChannelDownlinkMsgRedisKey(String tid){
//        String hKey = null;
//        try {
//            hKey = new String((TCP_CHANNEL_DOWNLINK_MSG_REDIS_KEY + tid).getBytes(),"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return TCP_CHANNEL_DOWNLINK_MSG_REDIS_KEY + tid;
    }

    /**
     * 查询一个终端所有缓存的下发消息
     * @param tid
     * @return
     */
    public List getAllCacheMsg(String tid){
        return redisTemplate.opsForHash().values(getTcpChannelDownlinkMsgRedisKey(tid));
    }

    /**
     * 删除一个终端所有缓存的下发消息
     * @param tid
     */
    public void delAllCacheMsg(String tid){
        redisTemplate.delete(getTcpChannelDownlinkMsgRedisKey(tid));
    }

    /**
     * 获取本机ip信息
     * @return
     */
    private String getLocalhostInfo(){
        String cachedIp = HOST_MAP.get("ip");
        if(cachedIp != null){
            return cachedIp;
        }
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            HOST_MAP.put("ip", ip);
            return ip;
        } catch (UnknownHostException e) {
            log.error("获取本机ip失败",e);
        }
        return null;
    }

    /**
     * 获取本机ip + 服务端口
     * @return
     */
    public String getIpAndPort(){
        String cachedIpPort = HOST_MAP.get("ip_port");
        if(cachedIpPort != null){
            return cachedIpPort;
        }
        String ip = getLocalhostInfo();
        if(ip == null){
            return null;
        }
        String ipPort = ip + ":" + serverPort;
        HOST_MAP.put("ip_port", ipPort);
        return ipPort;
    }


    /**
     * 获取缓存
     * @param tid
     * @return
     */
    public Channel getChannel(String tid){
        Channel channel = CHANNEL_MAP.get(tid);

        if(null != channel && channel.isActive()){
            log.info("channel is active {} ",channel.isActive());
            return channel;
        }
        return null;
    }

    /**
     * 缓存本机与客户端的连接通道 并 缓存 tid 与 通道所在节点的关系
     * @param tid
     * @param channel
     */
    public void addChannel(String tid,Channel channel){
        if (!CHANNEL_MAP.containsKey(tid) && channel.isActive()) {
//            redisTemplate.opsForValue().set(TCP_CHANNEL_PREFIX_REDIS_KEY + tid,this.getIpAndPort());
            CHANNEL_MAP.put(tid,channel);
        }
    }

    /**
     * 移除缓存的通道
     * @param tid
     */
    public void removeChannel(String tid){
        if (CHANNEL_MAP.containsKey(tid)) {
            CHANNEL_MAP.remove(tid);
            log.info("移除通道缓存，tid: {}", tid);
        }
    }

    /**
     * 根据通道移除缓存
     * @param channel
     */
    public void removeChannelByChannel(Channel channel){
        if (channel != null) {
            CHANNEL_MAP.entrySet().removeIf(entry -> entry.getValue().equals(channel));
            log.info("移除通道缓存，channel: {}", channel.remoteAddress());
        }
    }

    /**
     * 获取当前节点的 TCP 连接数
     * @return
     */
    public int getTcpClientConnectNum(){
        return TCP_CLIENT_CONNECT_NUM.get();
    }
    /**
     * 当前节点 TCP 连接数 递增
     * @return
     */
    public void incrementTcpClientConnectNum(){
        TCP_CLIENT_CONNECT_NUM.getAndIncrement();
    }

    /**
     * 当前节点 TCP 连接数 递减
     * @return
     */
    public void decrementTcpClientConnectNum(){
        TCP_CLIENT_CONNECT_NUM.getAndDecrement();
    }

    /**
     * 获取原始数据输出topic
     * @return
     */
    public String getBaseDataInoutTopic(){
        return BASE_DATA_INOUT_TOPIC;
    }


}
