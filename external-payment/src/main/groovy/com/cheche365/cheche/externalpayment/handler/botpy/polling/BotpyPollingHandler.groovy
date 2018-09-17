package com.cheche365.cheche.externalpayment.handler.botpy.polling

import groovy.util.logging.Log4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.POLLING_NOTIFICATIONS_REDIS_KEY

/**
 * Created by zhengwei on 22/03/2018.
 * 轮询金斗云异步回调结果
 */

@Service
@Log4j
class BotpyPollingHandler {

    @Autowired
    StringRedisTemplate redisTemplate


    String polling(String key) {

        int maxPollingTimes = 30
        String result
        while((maxPollingTimes-- >= 0)){
            result = redisTemplate.opsForHash().get(POLLING_NOTIFICATIONS_REDIS_KEY, key)
            if(result) {
                log.debug("第${maxPollingTimes}次轮询得到结果: ${result}")
                break
            }
            Thread.sleep(2000)
        }
        return result
    }
}
