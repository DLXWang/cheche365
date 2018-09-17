package com.cheche365.cheche.developer.jsonfilter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

/**
 * Created by zhengwei on 3/24/15.
 */
@Component
public class FilteringJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private boolean prefixJson = false;

    public FilteringJackson2HttpMessageConverter(){
        this.getObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));  //format the datetime field
    }

    @Override
    public void setPrefixJson(boolean prefixJson) {
        this.prefixJson = prefixJson;
        super.setPrefixJson(prefixJson);
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        ObjectMapper objectMapper = getObjectMapper();
        JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputMessage.getBody());

        try {
            if (this.prefixJson) {
                jsonGenerator.writeRaw("{} && ");
            }

            objectMapper.writeValue(jsonGenerator, object);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            throw new HttpMessageNotWritableException("Could not write JSON: " + e.getMessage());
        }

    }


}
