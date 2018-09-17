package com.cheche365.cheche.core.serializer;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.Payment;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by Administrator on 2016/9/26 0026.
 */
public class PaymentSerializer extends JsonSerializer<List<Payment>> {

    @Override
    public void serialize(List<Payment> payments,JsonGenerator gen, SerializerProvider serializers) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN));

        payments.forEach(Payment::toDisplayText);
        mapper.writeValue(gen, payments);
    }
}
