package com.cheche365.cheche.core.serializer

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.util.AutoUtils
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider

import java.text.SimpleDateFormat

/**
 * Created by mahong on 2015/9/10.
 */
class FormattedVehicleLicenseSerializer extends JsonSerializer<VehicleLicense> {

    @Override
    void serialize(VehicleLicense value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {

        AutoUtils.encrypt(AutoUtils.VEHICLE_LICENSE_ENCRYPT_PROPS, value, VehicleLicense.PROPERTIES)

        ObjectMapper mapper = new ObjectMapper();

        mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN));

        mapper.writeValue(gen, SerializerUtil.toMap(value, 'id,identity'));
    }
}
