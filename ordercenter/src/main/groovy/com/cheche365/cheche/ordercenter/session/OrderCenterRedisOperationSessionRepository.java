package com.cheche365.cheche.ordercenter.session;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

/**
 * Created by yinJianBin on 2017/9/12.
 */
public class OrderCenterRedisOperationSessionRepository extends RedisOperationsSessionRepository {
    public OrderCenterRedisOperationSessionRepository(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory);
    }

    public OrderCenterRedisOperationSessionRepository(RedisOperations<Object, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

    @Override
    public void cleanupExpiredSessions() {
        //do nothing for now
    }
}
