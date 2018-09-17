package com.cheche365.cheche.rest.jsonfilter.mapper;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.serializer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Created by chenxiaozhe on 15-10-12.
 */
public class ObjectMapperVersionGreaterThan13 {

    protected SimpleModule module;
    protected ObjectMapper objectMapper;

    public ObjectMapperVersionGreaterThan13() {
        objectMapper = new MappingJackson2HttpMessageConverter().getObjectMapper();
    }

    public ObjectMapper getMapper() {
        return this.objectMapper;
    }

    public ObjectMapperVersionGreaterThan13 setModules(Channel channel) {
        module = new SimpleModule();
        if (channel != null && !channel.isOrderCenterChannel()) {
            module.addSerializer(Auto.class, new FormattedAutoSerializer());
            //module.addSerializer(VehicleLicense.class, new FormattedVehicleLicenseSerializer());
        }
        module.addSerializer(QuoteRecord.class, new ArrayQRSerializer());
        module.addSerializer(InsuranceBills.class, new ArrayBillsSerializer());
        objectMapper.registerModule(module);

        return this;
    }

}
