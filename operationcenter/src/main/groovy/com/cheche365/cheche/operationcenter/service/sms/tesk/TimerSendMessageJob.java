package com.cheche365.cheche.operationcenter.service.sms.tesk;

import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.AdhocMessage;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.util.SMSMessageUtil;
import com.cheche365.cheche.operationcenter.service.sms.ImmediateSendAdhocMessageService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sunhuazhong on 2015/9/28.
 */
public class TimerSendMessageJob implements Job {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (logger.isDebugEnabled()) {
            logger.debug("schedule task to send adhoc message is starting");
        }

        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String adhocMessageInString = (String) jobDataMap.get(SMSMessageUtil.getJobDetailTimerTask());
            AdhocMessage adhocMessage = CacheUtil.doJacksonDeserialize(adhocMessageInString, AdhocMessage.class);
            ImmediateSendAdhocMessageService immediateSendAdhocMessageService =
                    ApplicationContextHolder.getApplicationContext().getBean(ImmediateSendAdhocMessageService.class);
            immediateSendAdhocMessageService.sendMessage(adhocMessage);
            context.getScheduler().shutdown();
//            StringRedisTemplate stringRedisTemplate= ApplicationContextHolder.getApplicationContext().getBean(StringRedisTemplate.class);
//            stringRedisTemplate.opsForList().remove(SMSMessageUtil.getTimerTaskQueueKey(), 0, adhocMessageInString);
        } catch (Exception ex) {
            logger.error("execute timer to send sms job error", ex);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("schedule task to send adhoc message is finished");
        }
    }
}
