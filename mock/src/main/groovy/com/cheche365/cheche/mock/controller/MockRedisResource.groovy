package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.web.counter.annotation.NonProduction
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.*

import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.core.constants.SmsConstants.get_VALIDATION_CODE_CACHING_PREFIX
import static com.cheche365.cheche.core.constants.WebConstants.*
import static java.lang.System.currentTimeMillis

/**
 * Created by zhengwei on 8/14/17.
 */

@RestController
@RequestMapping("/v1.6/mock/redis")
@Slf4j
class MockRedisResource {

    @Autowired
    RedisTemplate redisTemplate

    @Autowired
    private StringRedisTemplate stringRedisTemplate

    private StringRedisTemplate bihuRedisTemplate

    MockRedisResource(@Qualifier('bihuJedisConnectionFactory') RedisConnectionFactory redisConnFactory) {
        this.bihuRedisTemplate = new StringRedisTemplate(redisConnFactory)
    }

    @NonProduction
    @RequestMapping(value = "",method = RequestMethod.GET)
    putValue(@RequestParam String key){

        stringRedisTemplate.opsForValue().get(key)
    }

    @NonProduction
    @RequestMapping(value = "message",method = RequestMethod.POST)
    publish(@RequestBody Map body){

        stringRedisTemplate.convertAndSend(body.topic, body.message)
    }


    @NonProduction
    @RequestMapping(value = "vc",method = RequestMethod.POST)
    persistApplicant(@RequestBody Map vc){

        def key = _VALIDATION_CODE_CACHING_PREFIX + "cheche_" + vc.mobile
        def timestamp = currentTimeMillis()
        def value = vc.validationCode + '_' + timestamp

        redisTemplate.opsForHash().put key, vc.mobile, value
        redisTemplate.expire key, 20, TimeUnit.MINUTES

        [vc: vc]

    }

    @NonProduction
    @RequestMapping(value="",method= RequestMethod.DELETE)
    delByKey(@RequestParam String key){
        def redisConnection = redisTemplate.connectionFactory.connection
        def result = del(redisConnection, key)
        redisConnection.close()
        return result
    }

    @NonProduction
    @RequestMapping(value="/bihu",method= RequestMethod.DELETE)
    delByKeyBiHu(@RequestParam String key){
        def redisConnection = bihuRedisTemplate.connectionFactory.connection
        def result = del(redisConnection, key)
        redisConnection.close()
        return result
    }

    @NonProduction
    @RequestMapping(value = "allowPay", method = RequestMethod.GET)
    allowPay(@RequestParam(value = 'orderNo') String orderNo) {
        CacheUtil.putToSetWithDayExpire(stringRedisTemplate, ALLOW_ORDER_PAY, orderNo, 1)
    }

    @NonProduction
    @RequestMapping(value = "qrKey", method = RequestMethod.POST)
    cacheAndGetQrKey(@RequestBody QuoteRecord qr){
        String quoteRecordKey=UUID.randomUUID()
        redisTemplate.opsForValue().set("quote_record:hashcode:" + quoteRecordKey, quoteRecordKey);
        redisTemplate.opsForValue().set( quoteRecordKey, CacheUtil.doJacksonSerialize(qr));
        quoteRecordKey
    }

    @NonProduction
    @RequestMapping(value = "notCheckQuote", method = RequestMethod.POST)
    NotCheckQuote(@RequestParam(value = 'status', required = true) Boolean status){
        def currentStatus = stringRedisTemplate.opsForValue().get(NOT_CHECK_ALLOW_QUOTE)
        stringRedisTemplate.opsForValue().set(NOT_CHECK_ALLOW_QUOTE, String.valueOf(status))
        [changeInfo: "状态由" + currentStatus + "改变为" + status]
    }

    @NonProduction
    @RequestMapping(value = "notCheckPay", method = RequestMethod.POST)
    NotCheckPay(@RequestParam(value = 'status', required = true) Boolean status){
        def currentStatus = stringRedisTemplate.opsForValue().get(NOT_CHECK_ALLOW_PAY)
        stringRedisTemplate.opsForValue().set(NOT_CHECK_ALLOW_PAY, String.valueOf(status))
        [changeInfo: "状态由" + currentStatus + "改变为" + status]

    }

    @NonProduction
    @RequestMapping(value = "periodNotAllow", method = RequestMethod.POST)
    periodNotAllowedPay(@RequestParam(value = 'status', required = true) Boolean status){
        def currentStatus = stringRedisTemplate.opsForValue().get(PERIOD_NOT_ALLOWED_PAY)
        stringRedisTemplate.opsForValue().set(PERIOD_NOT_ALLOWED_PAY, String.valueOf(status))
        [changeInfo: "状态由" + currentStatus + "改变为" + status]
    }

    @NonProduction
    @RequestMapping(value = "notInterceptSms", method = RequestMethod.POST)
    notInterceptSms(@RequestParam(value = 'status', required = true) Boolean status){
        def currentStatus = stringRedisTemplate.opsForValue().get(NOT_INTERCEPT_SMS)
        stringRedisTemplate.opsForValue().set(NOT_INTERCEPT_SMS, String.valueOf(status))
        [changeInfo: "状态由" + currentStatus + "改变为" + status]
    }

    @NonProduction
    @RequestMapping(value = "set/{key}", method = RequestMethod.POST)
    setPost(@PathVariable(value = 'key') String key, @RequestParam String value){
        stringRedisTemplate.opsForSet().add(key, value)
    }

    def del(redisConnection, key){
        redisConnection.keys("*$key*".getBytes()).collectEntries {
            [new String(it), redisConnection.del(it, [] as byte[])]
        }
    }


}
