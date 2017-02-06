package nl.computerhok.scn;

import nl.computerhok.scn.listener.ApplicationListener1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

@Import(Config.class)
@SpringBootApplication
@EnableEurekaClient
public class SpringboottestApplication {
    private static final Log log = LogFactory.getLog(SpringboottestApplication.class);

    public static void main(String[] args) throws InterruptedException{
        log.error("starting " + SpringboottestApplication.class);
        SpringApplication springApplication = new SpringApplication(SpringboottestApplication.class);
        springApplication.addListeners(new ApplicationListener1());
//        boolean running = false;
//        while (! running) {
//            try {
                springApplication.run(args);
//                running = true;
//            } catch (Throwable t) {
//                log.error("error while starting (we will retry): " + t.getMessage());
//                Thread.sleep(10000);
//            }
//        }
    }
}
