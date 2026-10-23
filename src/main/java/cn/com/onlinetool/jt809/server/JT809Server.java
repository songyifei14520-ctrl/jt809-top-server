package cn.com.onlinetool.jt809.server;

import cn.com.onlinetool.jt809.config.BusinessConfig;
import cn.com.onlinetool.jt809.config.NettyConfig;
import cn.com.onlinetool.jt809.init.JT809ClientChannelInit;
import cn.com.onlinetool.jt809.init.JT809ServerChannelInit;
import cn.com.onlinetool.jt809.manage.TcpChannelMsgManage;
import cn.com.onlinetool.jt809.util.ByteArrayUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.jt809.netty.codec.JT809Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author choice
 * @description: netty启动类
 * @date 2018-12-27 12:52
 *
 */
@Slf4j
@Service
public class JT809Server {
    @Autowired
    private NettyConfig.NettyServerConfig serverConfig;
    @Autowired
    private BusinessConfig businessConfig;
    @Autowired
    private JT809ServerChannelInit JT809ServerChannelInit;

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

    // 添加从链路连接状态管理
    private final AtomicBoolean subLinkStarted = new AtomicBoolean(false);
    private EventLoopGroup workGroup; // 保存工作线程组引用
    private EventLoopGroup bossGroup; // 保存主线程组引用
    private byte[] verifyCode; // 保存验证码

    /**
     * 主链路（服务端）引导入口
     * @throws Exception
     */
    public void runServer() throws Exception{
        //创建主线程池（接收线程池）
        bossGroup = new NioEventLoopGroup(serverConfig.getBossMaxThreadCount(),new DefaultThreadFactory("boosServer",true));
        //创建工作线程池
        workGroup = new NioEventLoopGroup(serverConfig.getWorkMaxThreadCount(),new DefaultThreadFactory("workServer",true));

        try {
            //创建一个服务器端的程序类进行NIO的启动，同时可以设置Channel
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置要使用的线程池以及当前的Channel 的类型
            serverBootstrap.group(bossGroup,workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            //接收到的信息处理器
            serverBootstrap.childHandler(JT809ServerChannelInit);
            serverBootstrap.option(ChannelOption.SO_BACKLOG,128);
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            //ChannelFuture描述异步回调的处理操作
            ChannelFuture future = serverBootstrap.bind(serverConfig.getTcpPort()).sync();

            log.info("nettyServer run success,TCP-PORT:{}",serverConfig.getTcpPort());
            // 移除自动启动从链路的逻辑，改为手动触发
            // if(businessConfig.getIsOpenClient()){
            //     //启动从链路
            //     this.runClient(future.channel().eventLoop());
            // }
            //等待socket被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("nettyServer run fail");
            e.printStackTrace();
        } finally {
            if (workGroup != null) workGroup.shutdownGracefully();
            if (bossGroup != null) bossGroup.shutdownGracefully();
        }

    }

    /**
     * 优雅关闭主链路和从链路线程池
     */
    public void stopServer() {
        log.info("JT809Server stopServer() called, closing Netty线程池");
        try {
            if (workGroup != null) {
                workGroup.shutdownGracefully();
                log.info("workGroup shutdownGracefully called");
            }
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
                log.info("bossGroup shutdownGracefully called");
            }
        } catch (Exception e) {
            log.error("JT809Server stopServer error", e);
        }
    }

    /**
     * 手动启动从链路（在接收到下级平台连接请求并成功建立连接后调用）
     * @param verifyCode 验证码
     */
    public void startSubLink(byte[] verifyCode) {
        if (!businessConfig.getIsOpenClient()) {
            log.warn("从链路功能未启用，无法启动从链路");
            return;
        }

        // 保存验证码
        this.verifyCode = verifyCode;
        log.info("收到验证码，长度: {} verifyCode {}", verifyCode != null ? verifyCode.length : 0,ByteArrayUtil.bytes2int(this.verifyCode));

        if (subLinkStarted.compareAndSet(false, true)) {
            log.info("开始启动从链路...");

            if (workGroup != null) {
                this.runClient(workGroup);
            } else {
                log.error("工作线程组未初始化，无法启动从链路");
            }
        } else {
            log.warn("从链路已经启动，忽略重复启动请求");
        }
    }

    /**
     * 检查从链路是否已启动
     */
    public boolean isSubLinkStarted() {
        return subLinkStarted.get();
    }

    /**
     * 重置从链路状态（用于测试或重新连接）
     */
    public void resetSubLinkStatus() {
        subLinkStarted.set(false);
        this.verifyCode = null;
        log.info("从链路状态已重置");
    }

    /**
     * 获取验证码
     * @return 验证码
     */
    public byte[] getVerifyCode() {
        return verifyCode;
    }


    @Autowired
    private NettyConfig.NettyClientConfig clientConfig;
    @Autowired
    private JT809ClientChannelInit jt809ClientChannelInit;
    public static Channel clientChannel;

    @Resource
    TcpChannelMsgManage channelMsgManage;

    /**
     * 从链路（客户端）引导入口
     * @param group
     * @throws Exception
     */
    private void runClient(EventLoopGroup group) {
        String ip = clientConfig.getTcpIp();
        Integer port = clientConfig.getTcpPort();

        int verifyCodeInt = ByteArrayUtil.bytes2int(this.verifyCode);
        JT809Config.initUpperPlatformInfo(ip, port,
            0, null,null, null,
            verifyCodeInt, "1.2.15",
            56107078, 37382110, 50361430);

        Bootstrap client = new Bootstrap();
        client.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(jt809ClientChannelInit);

        // 封装连接操作用于重试
        connectWithRetry(client, ip, port, group,0);
    }

    private void connectWithRetry(Bootstrap client, String ip, int port, EventLoopGroup group, int retryCount) {


        String closeKey = GlobalConstants.GLOBAL_REDIS_KEY +"JT809:MAIN_LINK:ACTIVE_CLOSE";
        String bol = redisTemplate.opsForValue().get(closeKey);

        final int MAX_RETRIES =  3; // 最大重试次数

        if(bol == null || !"true".equals(bol)){
            ChannelFuture channelFuture = client.connect(ip, port);
            channelFuture.addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    log.info("nettyClient run success,TCP-IP:{},TCP-PORT:{}", ip, port);
                    clientChannel = future.channel();

                    channelMsgManage.addChannel("clientChannel",clientChannel);

                    // 添加连接关闭监听（可选：连接断开后触发）
                    future.channel().closeFuture().addListener(closeFuture -> {
                        log.warn("从链路连接已断开，重置连接状态并尝试重连...");
                        // 重置连接状态，允许重新启动从链路
                        subLinkStarted.set(false);
                        group.schedule(() -> connectWithRetry(client.clone(), ip, port, group,0), 5, TimeUnit.SECONDS);
                    });
                } else {
                    if (retryCount < MAX_RETRIES) {
                        int nextRetry = retryCount + 1;
                        log.warn("第{}次连接失败，5秒后重试... 原因：{}",
                            nextRetry, future.cause().getMessage());

                        group.schedule(() -> connectWithRetry(client.clone(), ip, port,
                                group, nextRetry),
                            5, TimeUnit.SECONDS);
                    } else {
                        log.error("已达到最大重试次数({})，停止重连！最后错误：{}",
                            MAX_RETRIES, future.cause().getMessage());

                        // 重连失败时重置状态，允许后续重新启动从链路
                        subLinkStarted.set(false);
                        log.warn("从链路连接失败，已重置连接状态，等待下次下级平台登录时重新启动");

                        // todo 发送中断消息（根据实际业务实现）通过主链路 DOWN_DISCONNECT_INFORM

                    }
                }
            });
        }

    }



    public void runClient() {
        String ip = clientConfig.getTcpIp();
        Integer port = clientConfig.getTcpPort();
        try {
            Bootstrap client = new Bootstrap();
            client.group(new NioEventLoopGroup());
            client.channel(NioSocketChannel.class);
            client.option(ChannelOption.TCP_NODELAY, true);
            client.handler(jt809ClientChannelInit);
            ChannelFuture channelFuture = client.connect(ip, port).sync();
            channelFuture.addListener(new GenericFutureListener() {
                @Override
                public void operationComplete(Future future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("nettyClient run success,TCP-IP:{},TCP-PORT:{}",ip,port);
                        clientChannel = channelFuture.channel();
                    }
                }
            });
        }catch (Exception e){
            log.error("nettyClient run fail");
            e.printStackTrace();
        }
    }


}
