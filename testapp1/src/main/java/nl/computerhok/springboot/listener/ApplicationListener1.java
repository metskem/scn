package nl.computerhok.springboot.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationListener1 implements ApplicationListener {
    private static final Log log = LogFactory.getLog(ApplicationListener1.class);

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationReadyEvent) {
            log.info("received ApplicationReadyEvent event " + applicationEvent);
        } else {
            log.info("received (but ignored) event " + applicationEvent);
        }
    }
}
