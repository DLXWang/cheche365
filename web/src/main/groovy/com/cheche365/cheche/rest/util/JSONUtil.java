package com.cheche365.cheche.rest.util;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.rest.jsonfilter.mapper.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by zhengwei on 10/20/15.
 */
public class JSONUtil {

    public static String doSerialize(Channel channel, Object pojo) {
        ObjectMapper mapper = ObjectMapperFactory.build(channel);
        try {
            return mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when serializing object");
        }
    }

}
