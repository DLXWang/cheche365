package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.security.throttle.QuoteLimitChecker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by zhengwei on 09/07/2018.
 */

@RestController
@RequestMapping("/v1.6/mock/quote/limits")
class MockQuoteLimitResource {

    @Autowired
    StringRedisTemplate redisTemplate

    @Autowired
    QuoteLimitChecker limitChecker

    @NonProduction
    @RequestMapping(value = "",method = RequestMethod.GET)
    def allLimits() {
        ['10000', '20000', '25000'].collectEntries {
            [
                it,
                limitChecker.findLimit(it)
            ]
        }
    }

    @NonProduction
    @RequestMapping(value = "/times",method = RequestMethod.GET)
    def currentTimes() {

        String firstLevel = [
            QuoteLimitChecker.QUOTE_CALL_TIMES,
            new Date()[Calendar.DAY_OF_MONTH]
        ].join('_')

        redisTemplate
            .opsForHash()
            .keys(firstLevel)?.findAll {it.contains('_')}?.
            collectEntries{[it, redisTemplate.opsForHash().get(firstLevel, it)]}?.
            sort { a,b -> FORMAT_SECOND_LEVEL.call(b.key)[-5..-1] <=> FORMAT_SECOND_LEVEL.call(a.key)[-5..-1] }
    }

    @NonProduction
    @RequestMapping(value = "/times", method = RequestMethod.POST)
    def updateCurrentTimes(@RequestBody Map param) {

        String firstLevel = [
            QuoteLimitChecker.QUOTE_CALL_TIMES,
            new Date()[Calendar.DAY_OF_MONTH]
        ].join('_')

        redisTemplate.opsForHash().put(firstLevel, param.secondLevel, param.times)

        'success'
    }

    def static FORMAT_SECOND_LEVEL = { String key ->
        def values = key.split('_')
        [values.length - 1, values.length - 2].each {
            values[it] = (values[it] as Integer) >= 10 ? values[it] : "0" + values[it]
        }
        values.join("_")
    }

}
