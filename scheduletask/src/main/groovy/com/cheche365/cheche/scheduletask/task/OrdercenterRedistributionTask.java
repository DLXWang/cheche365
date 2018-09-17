package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.scheduletask.service.task.OrdercenterRedistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenxiangyin on 2017/3/9.
 */
@Service
public class OrdercenterRedistributionTask extends BaseTask{
    private static final String CACHE_KEY="schedules.task.ordercenter.redistribution";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrdercenterRedistributionService ordercenterRedistributionTaskService;

    @Override
    protected void doProcess() throws Exception {
        Object obj=stringRedisTemplate.opsForList().rightPop(CACHE_KEY);
        while(obj!=null){
            Map map= CacheUtil.doJacksonDeserialize(String.valueOf(obj),HashMap.class);
            ordercenterRedistributionTaskService.redistributeByOperator(map);
            obj=stringRedisTemplate.opsForList().rightPop(CACHE_KEY);
        }
    }
}
