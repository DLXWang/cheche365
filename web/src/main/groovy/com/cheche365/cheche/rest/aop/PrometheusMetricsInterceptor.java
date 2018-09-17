package com.cheche365.cheche.rest.aop;

import com.cheche365.cheche.web.counter.annotation.PrometheusMetrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 *  Prometheus埋点,针对web接口访问统计
 */
@Aspect
@Component
public class PrometheusMetricsInterceptor {

    private static final Counter _REQUEST_COUNTER = Counter.build().name("prometheus_counter").labelNames("api", "result", "endpoint").help
        ("total request counter of api").register();
    private static final Histogram _HISTOGRAM = Histogram.build().name("prometheus_consuming").labelNames("api").help
        ("response consuming of api").register();

    @Pointcut("@annotation(com.cheche365.cheche.web.counter.annotation.PrometheusMetrics)")
    public void pcMethod() {
    }

    @Around(value = "pcMethod() && @annotation(annotation)")
    public Object metricsCollector(ProceedingJoinPoint joinPoint, PrometheusMetrics annotation) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        PrometheusMetrics prometheusMetrics = methodSignature.getMethod().getAnnotation(PrometheusMetrics.class);
        String endpoint = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI();
        if (prometheusMetrics != null) {
            String name;
            if (StringUtils.isNotEmpty(prometheusMetrics.name())) {
                name = prometheusMetrics.name();
            } else {
                name = endpoint;
            }
            Histogram.Timer requestTimer = _HISTOGRAM.labels(name).startTimer();
            Object object;
            try {
                object = joinPoint.proceed();
                _REQUEST_COUNTER.labels(name, "success", endpoint).inc();
            } catch (Exception e) {
                _REQUEST_COUNTER.labels(name, "error", endpoint).inc();
                throw e;
            } finally {
                requestTimer.observeDuration();
            }
            return object;
        } else {
            return joinPoint.proceed();
        }
    }
}
