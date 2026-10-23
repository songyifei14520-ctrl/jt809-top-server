package cn.com.onlinetool.jt809.handler;

import cn.com.onlinetool.jt809.server.JT809Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author choice
 * @description:
 * @date 2019-07-19 15:06
 *
 */
@Slf4j
@Component
public class DownConnectHandler{

    @Autowired
    JT809Server jt809Server;
    public void connect() {
        if (jt809Server == null) {
            log.error("JT809Server is not initialized");
            return;
        }
        jt809Server.runClient();
    }
}
