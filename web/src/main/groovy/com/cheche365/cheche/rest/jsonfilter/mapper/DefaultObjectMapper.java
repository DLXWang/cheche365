package com.cheche365.cheche.rest.jsonfilter.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by chenxiaozhe on 15-10-12.
 */
public class DefaultObjectMapper {

    private ObjectMapper defaultMapper;

    public DefaultObjectMapper(ObjectMapper defaultMapper) {
        this.defaultMapper = defaultMapper;
    }

    public ObjectMapper getMapper() {
        return this.defaultMapper;
    }

}
