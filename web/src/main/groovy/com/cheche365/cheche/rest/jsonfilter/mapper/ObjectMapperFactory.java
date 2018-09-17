package com.cheche365.cheche.rest.jsonfilter.mapper;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.Version;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

/**
 * Created by chenxiaozhe on 15-10-12.
 */
public class ObjectMapperFactory {

    public static ObjectMapper build(HttpServletRequest request, ObjectMapper objectMapper) {
        Version version = Version.getVersion(request);
        String uri = request.getRequestURI();
        Channel channel = ClientTypeUtil.getChannel(request);
        //我的车辆中相关接口不需要加密，使用默认的ObjectMapper
        boolean autoSearch = (uri.endsWith("users/auto")
                || uri.endsWith("/quotes/history")
                || (uri.endsWith("/autos") && !uri.endsWith("users/current/autos")));

        //根据api版本信息返回相应的ObjectMapper,如果没有匹配的mapper则返回defaultObjectMapper
        ObjectMapper defaultMapper = (autoSearch || version == null) ?
            new DefaultObjectMapper(objectMapper).getMapper() :
            new ObjectMapperVersionGreaterThan13().setModules(channel).getMapper();
        defaultMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        return defaultMapper;
    }

    public static ObjectMapper build(Channel channel) {
        ObjectMapper defaultMapper = new ObjectMapperVersionGreaterThan13().setModules(channel).getMapper();
        defaultMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return defaultMapper;
    }
}
