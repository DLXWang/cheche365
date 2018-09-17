package com.cheche365.cheche.core.serializer;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.InsuranceBills;
import com.cheche365.cheche.core.serializer.converter.ArrayInsuranceBills;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by zhengwei on 4/29/16.
 */
public class ArrayBillsSerializer extends JsonSerializer<InsuranceBills> {

    @Override
    public void serialize(InsuranceBills value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayInsuranceBills afterConvert = new ArrayInsuranceBills().convert(value);
        mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN));
        mapper.writeValue(gen, afterConvert);

    }
}
