package com.cheche365.cheche.ordercenter.app.config;

import com.cheche365.cheche.ordercenter.service.promtest.ExporterRegister;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
@ConditionalOnClass(CollectorRegistry.class)
public class PrometheusConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CollectorRegistry metricRegistry() {
        return CollectorRegistry.defaultRegistry;
    }

    @Bean
    ServletRegistrationBean registerPrometheusExporterServlet(CollectorRegistry metricRegistry) {
         return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/pro_order_center");
    }

    @Bean
    ExporterRegister exporterRegister() {
        List<Collector> collectors = new ArrayList<>();
        collectors.add(new StandardExports());
        collectors.add(new MemoryPoolsExports());
        collectors.add(new ThreadExports());
        collectors.add(new GarbageCollectorExports());
        collectors.add(new BufferPoolsExports());
        collectors.add(new VersionInfoExports());
        collectors.add(new ClassLoadingExports());
        ExporterRegister register = new ExporterRegister(collectors);
        return register;
    }


}
