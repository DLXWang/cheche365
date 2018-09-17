package com.cheche365.cheche.web.app;


import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * Web App Main Class
 */
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class WebApplicationLauncher implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebApplicationLauncher.class, args);
    }

    public void run(String... strings) throws Exception {
        DefaultExports.initialize();
    }
}
