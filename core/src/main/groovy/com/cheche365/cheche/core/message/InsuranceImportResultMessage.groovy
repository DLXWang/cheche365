package com.cheche365.cheche.core.message

import groovy.util.logging.Slf4j
import org.springframework.data.redis.core.BoundValueOperations
import org.springframework.data.redis.core.RedisTemplate

import java.util.concurrent.TimeUnit

/**
 * Created by yinJianBin on 2017/9/22.
 */
@Slf4j
class InsuranceImportResultMessage extends QueueMessage<String, Object> {

    static final String QUEUE_NAME = "insurance_import_callback_result"
    static final String QUEUE_LIST = "insurance_import_callback_result_list"
    static final String RUNNING_FLAG = "insurance_import_task_running"
    static final String MATCH_RUNNING_FLAG = "insurance_import_data_match_running"


    public InsuranceImportResultMessage(String notifyMsg) {
        super.setMessage(notifyMsg);
    }

    @Override
    String getQueueName() {
        return QUEUE_NAME
    }

    @Override
    String getQueueSet() {
        return QUEUE_LIST
    }

    /**
     * 查看线下数据导入的任务是否正在运行
     * @param redisTemplate
     * @return
     */
    static String getRunningFlag(RedisTemplate redisTemplate) {
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(RUNNING_FLAG);
        String runningFlag = boundValueOperations.get();
        return runningFlag
    }

    /**
     * 线下数据导入的任务在redis中增加运行标志
     * @param redisTemplate
     * @return
     */
    static boolean setRunningFlag(RedisTemplate redisTemplate, String message) {
        log.debug('Offline data import task start , set the running_flag of this task')
        redisTemplate.opsForValue().set(RUNNING_FLAG, message)
        redisTemplate.expire(RUNNING_FLAG, 20, TimeUnit.MINUTES);
    }

    static def resetProcessFlag(RedisTemplate redisTemplate) {
        log.debug('Clear the running_flag of this task')
        redisTemplate.delete(RUNNING_FLAG)
    }


    static String getMatchRunningFlag(RedisTemplate redisTemplate) {
        BoundValueOperations boundValueOperations = redisTemplate.boundValueOps(MATCH_RUNNING_FLAG);
        return boundValueOperations.get();
    }

    static boolean setMatchRunningFlag(RedisTemplate redisTemplate, String message) {
        log.debug('Offline data import task start to match, set the match_running_flag of this task')
        redisTemplate.opsForValue().set(MATCH_RUNNING_FLAG, message)
        redisTemplate.expire(MATCH_RUNNING_FLAG, 30, TimeUnit.MINUTES);
    }

    static def resetMatchProcessFlag(RedisTemplate redisTemplate) {
        log.debug('Clear the match_running_flag of this task')
        redisTemplate.delete(MATCH_RUNNING_FLAG)
    }
}
