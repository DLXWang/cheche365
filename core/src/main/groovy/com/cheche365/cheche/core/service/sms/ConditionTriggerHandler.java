package com.cheche365.cheche.core.service.sms;

import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.core.util.RuntimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by sunhuazhong on 2015/10/13.
 */
@Component
public class ConditionTriggerHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String conditionQueueKey = "sms:send:message:queue";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SmsGenerator smsGenerator;


    //由于该方法被调用的地方包含事务,导致此方法中的log序列化事务未提交,短信发送查询id获取不到,因此加该注解屏蔽该方法的事务
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void process(Map<String, String> parameterMap) {
        if (StringUtils.isEmpty(parameterMap.get(SmsCodeConstant.MOBILE))) {
            return;
        }
        if(RuntimeUtil.isDevEnv()){
            logger.debug("dev profile, 忽略短信发送");
            return;
        }
        logger.info("开始生成smsInfo,参数列表-->{}", parameterMap.toString());
        SmsInfo smsInfo = smsGenerator.generateSmsInfo(parameterMap);
        if (smsInfo != null) {
            send(smsInfo);
        }
    }

    public void send(SmsInfo smsInfo) {
        stringRedisTemplate.opsForList().leftPush(conditionQueueKey, CacheUtil.doJacksonSerialize(smsInfo));
    }
}
