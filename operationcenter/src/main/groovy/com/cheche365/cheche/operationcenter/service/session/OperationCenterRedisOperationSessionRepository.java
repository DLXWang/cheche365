package com.cheche365.cheche.operationcenter.service.session;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

/**
 * Created by yinJianBin on 2017/9/12.
 */
public class OperationCenterRedisOperationSessionRepository extends RedisOperationsSessionRepository {
    public OperationCenterRedisOperationSessionRepository(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory);
    }

    public OperationCenterRedisOperationSessionRepository(RedisOperations<Object, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

    @Override
    public void cleanupExpiredSessions() {
        //do nothing for now
//        super.cleanupExpiredSessions();
    }
}
