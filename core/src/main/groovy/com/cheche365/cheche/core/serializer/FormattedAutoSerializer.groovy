package com.cheche365.cheche.core.serializer

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.util.AutoUtils
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider

import java.text.SimpleDateFormat

/**
 * Created by mahong on 2015/9/9.
 */
class FormattedAutoSerializer extends JsonSerializer<Auto> {

    @Override
    void serialize(Auto value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {

        AutoUtils.encrypt(AutoUtils.AUTO_ENCRYPT_PROPS, value, Auto.PROPERTIES)

        ObjectMapper mapper = new ObjectMapper();

        mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN));
        mapper.writeValue(gen, value);
    }
}
