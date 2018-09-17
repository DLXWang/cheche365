package com.cheche365.cheche.mock.core

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.JavaType

class JsonTemplateParser extends TemplateParser {

    static ObjectMapper MAPPER = new ObjectMapper()
    JsonTemplateParser() {
        super('model_template', 'json')
    }

    @Override
    def parse(File file) {
        try{
            MAPPER.readValue(file, Map)
        }catch (Exception e){
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, Object)
            MAPPER.readValue(file, type)
        }
    }
}
