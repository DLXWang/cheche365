package com.cheche365.cheche.core.serializer;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.serializer.converter.APIArrayQuoteRecord;
import com.cheche365.cheche.core.serializer.converter.ArrayQuoteRecord;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by zhengwei on 4/26/16.
 */
public class ArrayQRSerializer extends JsonSerializer<QuoteRecord> {

    @Override
    public void serialize(QuoteRecord value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN));

        mapper.writeValue(gen, (null != value.getChannel() && value.getChannel().isPartnerAPIChannel() ? new APIArrayQuoteRecord() : new ArrayQuoteRecord()).convert(value));

    }
}
