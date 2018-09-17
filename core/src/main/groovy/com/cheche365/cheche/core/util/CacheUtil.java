package com.cheche365.cheche.core.util;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liqiang on 3/8/15.
 */
public class CacheUtil {

    private static Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    public static void putValueWithExpire(RedisTemplate redisTemplate, String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        int DEFAULT_EXPIRE_HOURS = 2;
        redisTemplate.expire(key, DEFAULT_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public static void putValueWithExpire(RedisTemplate redisTemplate, String key, String value){
        redisTemplate.opsForValue().set(key, value);
        int DEFAULT_EXPIRE_HOURS = 2;
        redisTemplate.expire(key, DEFAULT_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public static void putValueWithExpire(RedisTemplate redisTemplate, String key, Object value, int timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public static void putValue(RedisTemplate redisTemplate, String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }

    public static void putValue(RedisTemplate redisTemplate, String key, int value){
        redisTemplate.opsForValue().set(key, value);
    }

    public static void putDoubleValue(RedisTemplate redisTemplate, String key, Double value){
        redisTemplate.opsForValue().set(key, value);
    }

    public static String getValue(RedisTemplate redisTemplate, String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public static Object getValueToObject(RedisTemplate redisTemplate, String key){
        return redisTemplate.opsForValue().get(key);
    }

    public static <T> T getValue(RedisTemplate redisTemplate, String key, Class<T> clazz) {
        logger.debug("will try to get value from redis by key : {} ", key);

        if (StringUtils.isBlank(key)) {
            return null;
        }

        String value = CacheUtil.getValue(redisTemplate, key);
        if (StringUtils.isBlank(value)) {
            logger.debug("not found the target value form redis by key : {} ", key);
            return null;
        }

        return CacheUtil.doJacksonDeserialize(value, clazz);
    }

    public static void putInHash(RedisTemplate redisTemplate, String hashKey, String key, Object value){
        redisTemplate.opsForHash().put(hashKey, key, value);
    }

    public static boolean hasKey(RedisTemplate redisTemplate, String key){
        return redisTemplate.hasKey(key);
    }

    public static <T> T getObjectFromCache(RedisTemplate redisTemplate,String key, String hashKey, Class<T> clazz){
        String cachedResult = (String)redisTemplate.opsForHash().get(key, hashKey);
        if (cachedResult != null) {
            Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(clazz);
            try {
                return serializer.deserialize(cachedResult.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("deserialize cached object failed.",e);
            }
        }
        return null;
    }

    public static <T> void putObjectToCache(RedisTemplate redisTemplate,String key, String hashKey, T object){
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>((Class<T>) object.getClass());
        try{
            byte[] bytes= serializer.serialize(object);
            redisTemplate.opsForHash().put(key, hashKey, new String(bytes,"UTF-8"));
        }catch(UnsupportedEncodingException e) {
            logger.error("serialize object failed.",e);
        }
    }


    public static String doJacksonSerialize(Object pojo){
        return doJacksonSerialize(pojo, false);
    }

    public static String toJSONPretty(Object pojo){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            logger.debug(ExceptionUtils.getStackTrace(e));
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when serializing object");
        }
    }

    public static String doJacksonSerialize(Object pojo, boolean formatDate) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        if(formatDate){
            mapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_SHORTDATE_PATTERN));
        }

        try {
            return mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            logger.error("Fail to serialize the Object with Jackson: "+pojo.getClass().getName(),e);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when serializing object");
        }
    }

    public static String doJacksonSerialize(Object pojo, JsonSerializer serializer){

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(pojo.getClass(),serializer);
        mapper.registerModule(module);

        try {
            return mapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            logger.error("Fail to serialize the Object with Jackson: "+pojo.getClass().getName(),e);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when serializing object");
        }
    }

    public static <T> T doJacksonDeserialize(String objectInJSON, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(objectInJSON, clazz);
        } catch (IOException e) {
            logger.error("Fail to deserialize the JSON to " + clazz.getName()+" : "+objectInJSON,e);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when deserializing JSON");
        }
    }

    public static <T> List<T> doListJacksonDeserialize(String objectInJSON, Class<T> pojo) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, pojo);

        try {
            return mapper.readValue(objectInJSON, type);
        } catch (IOException e) {
            logger.error("Fail to deserialize the JSON to " + List.class.getName() +"_"+pojo.getName() + " : " + objectInJSON,e);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Errors when deserializing JSON");
        }
    }

    //set的
    public static Long putToSetWithDayExpire(RedisTemplate redisTemplate, String key, String value, int days) {
        return putToSetWithExpire(redisTemplate, key, value, days, TimeUnit.DAYS);
    }

    public static Long putToSetWithExpire(RedisTemplate redisTemplate, String key, String value, int timeout, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, timeout, timeUnit);
        return count;
    }

    public static Long putToSet(RedisTemplate redisTemplate, String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public static Long getSetSize(RedisTemplate redisTemplate, String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public static Boolean isMemberSet(RedisTemplate redisTemplate, String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public static void cacheUser(HttpSession session, User user) {
        session.setAttribute(WebConstants.SESSION_KEY_USER, CacheUtil.doJacksonSerialize(user));
        session.setMaxInactiveInterval(WebConstants.DEFAULT_REDIS_TIME_OUT);
    }

    public static void cacheUserCallback(HttpSession session, Object userCallbackInfo){
        session.setAttribute(WebConstants.SESSION_KEY_USER_CALLBACK, CacheUtil.doJacksonSerialize(userCallbackInfo));
        session.setMaxInactiveInterval(WebConstants.DEFAULT_REDIS_TIME_OUT);
    }

    public static void cacheChannelAgent(HttpSession session, ChannelAgent channelAgent){
        session.setAttribute(WebConstants.SESSION_KEY_CHANNEL_AGENT, CacheUtil.doJacksonSerialize(channelAgent));
        session.setMaxInactiveInterval(WebConstants.DEFAULT_REDIS_TIME_OUT);
        logger.info("session id :{},channelAgent:{},",session.getId(),CacheUtil.doJacksonSerialize(channelAgent));
    }

    //用于拦截恶意调用短信服务
    public static void cacheSmsFlag(HttpSession session){
        session.setAttribute(WebConstants.SESSION_KEY_ALLOW_SEND_SMS, Boolean.TRUE);
    }
    public static Boolean getSmsFlag(HttpSession session){
        return (Boolean) session.getAttribute(WebConstants.SESSION_KEY_ALLOW_SEND_SMS);
    }
}
