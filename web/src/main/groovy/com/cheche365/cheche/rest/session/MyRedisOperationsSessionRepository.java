package com.cheche365.cheche.rest.session;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

/**
 * Created by liqiang on 10/31/15.
 */
public class MyRedisOperationsSessionRepository extends RedisOperationsSessionRepository {
    public MyRedisOperationsSessionRepository(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory);
    }

    public MyRedisOperationsSessionRepository(RedisOperations<Object, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

    @Override
    public void cleanupExpiredSessions() {
        //do nothing for now
    }
}
