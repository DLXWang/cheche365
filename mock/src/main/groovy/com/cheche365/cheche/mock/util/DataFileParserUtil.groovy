package com.cheche365.cheche.mock.util

import org.apache.commons.lang3.SerializationUtils
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.JavaType

class DataFileParserUtil {

    static final String BOTOY_PAYMENT_STATUS_CALLBACK = 'botpy/model_template_payment_status_callback.json'
    static final String BOTPY_PAYMENT_INFO_CALLBACK = 'botpy/model_template_payment_info_callback.json'
    static final String BOTPY_STATUS_CHANGE_CALLBACK = 'botpy/model_template_status_change_callback.json'
    static final String BOTPY_PROPOSALS_STATUS = 'botpy/model_template_proposals_status.json'


    static ObjectMapper MAPPER = new ObjectMapper()

    static model(String filePath) {
        def content = DataFileParserUtil.getResourceAsStream("/mock/template/" + filePath).text
        def model = parse(content)
        model instanceof Serializable ? SerializationUtils.clone(model) : model
    }

    private static parse(String content){
        try{
            MAPPER.readValue(content, Map)
        } catch (Exception e){
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, Object)
            MAPPER.readValue(content, type)
        }
    }

}
