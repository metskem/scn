package nl.computerhok.scn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@EnableDiscoveryClient
//@EnableAutoConfiguration
//@ComponentScan
//@EnableGlobalMethodSecurity(securedEnabled = true)
//@EnableJpaRepositories()
public class Config {
    private static final Log log = LogFactory.getLog(Config.class);
    @Value("${prop3}")
    private String prop3;
    @Value("${spring.datasource.url}")
    private String url;

//    @Bean
//    @ExportMetricWriter
//    StatsdMetricWriter metricWriter(@Value("${statsd.prefix:demoprefix}") String prefix,
//                                    @Value("${statsd.host:192.168.99.100}") String host,
//                                    @Value("${statsd.port:8125}") int port) {
//        log.warn("value of property prop3 is " + prop3);
//        log.warn("value of property spring.datasource.url is " + url);
//        log.info("initializing StatsD metric writer...");
//        StatsdMetricWriter metricWriter = new StatsdMetricWriter(prefix, host, port);
//        return metricWriter;
//    }
}
