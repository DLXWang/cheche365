package com.cheche365.cheche.rest.jsonfilter;

import com.cheche365.cheche.rest.jsonfilter.mapper.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhengwei on 3/24/15.
 * 过滤HTTP Response JSON中多余的字段。另外其他序列化JSON时的通用操作也在本类中实现，比如格式化日期字段。
 */
public class FilteringJackson2HttpMessageConverter extends AFilteringJackson2HttpMessageConverter {

    private ObjectMapper defaultObjectMapper;
    public FilteringJackson2HttpMessageConverter() {
        defaultObjectMapper = this.getObjectMapper();
    }

    @Override
    public ObjectMapper getObjectMapperByRequestVersion(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        ObjectMapper objectMapper = ObjectMapperFactory.build(request, defaultObjectMapper);
        ObjectMapper originalObjectMapper = objectMapper;
        this.setObjectMapper(objectMapper);
        return originalObjectMapper;
    }

    @Override
    public ResponseHandler getResponseHandler() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return new ResponseHandlerFactory().getHandler(request);
    }

}
