package com.cheche365.cheche.ordercenter.serializer;

import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.ordercenter.model.QuoteRecordViewExtend;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by wangfei on 2016/3/24.
 */
public class QuoteRecordViewExtendSerializer extends JsonSerializer<QuoteRecord> {

    @Override
    public void serialize(QuoteRecord value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        QuoteRecordViewExtend afterConvert = new QuoteRecordViewExtend().convert(value);

        ObjectMapper mapper = new ObjectMapper();

        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.writeValue(gen, afterConvert);
    }
}
