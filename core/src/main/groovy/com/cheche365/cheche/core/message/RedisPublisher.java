package com.cheche365.cheche.core.message;

import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by mahong on 2016/2/22.
 * 将需要同步的订单发布到redis
 */
@Component
public class RedisPublisher {

    private Logger logger = LoggerFactory.getLogger(RedisPublisher.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void publish(QueueMessage message) {
        String messageInString = this.getStringMessage(message);
        logger.debug("publish message by redis , message type is  {}, message is {}", message.getMessage().getClass().getSimpleName(), messageInString);
        redisTemplate.opsForSet().add(message.getQueueSet(), (message.getKey() == null) ? messageInString : String.valueOf(message.getKey()));
        redisTemplate.convertAndSend(message.getQueueName(), messageInString);
    }

    public void publish(Payment payment) {
        logger.debug("调用payment publish方法流水号为:{}",payment.getOutTradeNo());
        logger.debug("publish message by redis , message type is  {}, message is {}", payment.getPaymentType().getDescription(), payment.getStatus().getDescription());
        redisTemplate.opsForList().leftPush("payment_call_back",CacheUtil.doJacksonSerialize(payment));
    }

    private String getStringMessage(QueueMessage message) {
        return message.getMessage() instanceof String ?
            message.getMessage().toString() :
            CacheUtil.doJacksonSerialize(message.getMessage());
    }

}
