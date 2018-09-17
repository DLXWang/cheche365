package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.scheduletask.service.task.CustomerRedistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-06-03.
 */
@Service
public class CustomerRedistributionTask extends BaseTask{
    private static final String CACHE_KEY="schedules.task.customer.redistribution";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("customerRedistributionTaskService")
    private CustomerRedistributionService customerRedistributionService;

    @Override
    protected void doProcess() throws Exception {
        Object obj=stringRedisTemplate.opsForList().rightPop(CACHE_KEY);
        while(obj!=null){
            Map map= CacheUtil.doJacksonDeserialize(String.valueOf(obj),HashMap.class);
            customerRedistributionService.redistributeByOperator(map);
            obj=stringRedisTemplate.opsForList().rightPop(CACHE_KEY);
        }
    }
}
