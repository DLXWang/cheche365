package com.cheche365.cheche.web.util

import com.cheche365.cheche.core.constants.WebConstants
import groovy.util.logging.Slf4j
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

/**
 * Created by shanxf on 2017/5/5.
 * 处理Http  转成Https
 */
@Slf4j
class UrlUtil {

    static String toFullUrl(HttpServletRequest httpServletRequest, String path) {
        log.info("转换前的path :{}",path)
        StringBuilder redirectUrl = new StringBuilder(WebConstants.getSchemaURL())
        redirectUrl.append(httpServletRequest.getServerName())
        if (path?.startsWith("/")) {
            redirectUrl.append(path)
        } else {
            redirectUrl.append("/").append(path)
        }
        log.info("转成后的path :{}",redirectUrl.toString())
        redirectUrl.toString()
    }

    static String toFullUrl(String page){
        WebConstants.getDomainURL() + page
    }

    static String getSchema(){
        HttpServletRequest request = ((ServletRequestAttributes)
            RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-Http-scheme") ?: 'http'
    }
}
