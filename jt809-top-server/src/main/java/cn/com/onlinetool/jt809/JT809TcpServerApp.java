package cn.com.onlinetool.jt809;


import cn.com.onlinetool.jt809.server.JT809Server;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author choice
 */

@SpringBootApplication
@EnableScheduling
//@EnableKafka
public class JT809TcpServerApp implements DisposableBean {

    private static JT809Server staticJT809Server;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    public static void main(String[] args) throws Exception {
        ApplicationContext context = new SpringApplicationBuilder(JT809TcpServerApp.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
        JT809Server JT809Server = context.getBean(JT809Server.class);
        staticJT809Server = JT809Server;
        JT809Server.runServer();
    }

    @Override
    public void destroy() throws Exception {
        if (staticJT809Server != null) {
            staticJT809Server.stopServer();
        }
    }
}
