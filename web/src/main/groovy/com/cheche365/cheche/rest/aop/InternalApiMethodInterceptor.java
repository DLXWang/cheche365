package com.cheche365.cheche.rest.aop;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.web.counter.annotation.InternalApi;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by mahong on 2016/5/9.
 * 使用 @InternalApi 标注的方法只供内部调用
 * 如 ： @InternalApi({WebConstants.CHECHE_INTERNAL_IP})
 */
@Aspect
@Component
public class InternalApiMethodInterceptor{

    Logger logger = LoggerFactory.getLogger(InternalApiMethodInterceptor.class);

    @Around(value = "@annotation(com.cheche365.cheche.web.counter.annotation.InternalApi)")
    public Object invoke(ProceedingJoinPoint jointPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) jointPoint.getSignature();
        Method method = signature.getMethod();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String[] value = method.getAnnotation(InternalApi.class).value();
        String remoteAddress = getRemoteIpAddress(request);
        if (RuntimeUtil.isProductionEnv() && !Arrays.asList(value).contains(remoteAddress)) {
            logger.error("非法操作:{} 方法只能供内部IP {} 调用,当前请求IP为 {} !", method, Arrays.asList(value).toString(), remoteAddress);
            throw new BusinessException(BusinessException.Code.NON_FATAL, "非法操作");
        }
        return jointPoint.proceed();
    }

    /**
     * 获取请求客户端的真实IP地址(考虑多级反向代理情况)
     */
    public String getRemoteIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        logger.info("request请求中x-forwarded-for信息: {} !", ipAddress);
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error("获取本机IP异常， {} !", e);
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
