package com.cheche365.cheche.manage.common.service.sms.queue;

import com.cheche365.cheche.core.model.ScheduleMessageLog;
import com.cheche365.cheche.core.repository.ScheduleMessageLogRepository;
import com.cheche365.cheche.core.service.sms.SmsInfo;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.app.config.ManageCommonConfig;
import com.cheche365.cheche.manage.common.service.sms.SendMessageErrorHandler;
import com.cheche365.cheche.manage.common.util.SMSMessageUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.cheche365.cheche.common.util.AopUtils._FLOW_ID;

/**
 * 处理从队列中获取消息调用业务MessageHandler的骨架逻辑
 * <p>
 * Created by sunhuazhong on 2015/10/8.
 */
@Component
@Transactional
public class ConditionMessageQueueProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    // redis中保存发送短信的key
    public static final String REDIS_SMS_QUEUE_SEND_KEY = "sms:send:message:queue";

    // redis中保存发送短信的备份的key
    public static final String REDIS_SMS_QUEUE_BACKUP_KEY = "backup:sms:send:message:queue";

    @Autowired
    private SendScheduleMessageHandler sendScheduleMessageHandler;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ScheduleMessageLogRepository scheduleMessageLogRepository;

    @Autowired
    private SendMessageErrorHandler sendMessageErrorHandler;

    @Autowired
    ManageCommonConfig.ChecheSmsSendSwitch checheSmsSendSwitch;

    @PostConstruct
    public void init() throws Exception {
        if (checheSmsSendSwitch.isSmsSendEanble()) {
            ExecutorService threadPool = Executors.newSingleThreadExecutor();
            threadPool.submit(new SendConditionMessageThread());
        }
    }


    class sendConditionMessageObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            //重启线程
            logger.debug("5秒后重启线程[SendConditionMessageThread]");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("thread sleep over by exception", e);
            }
            new SendConditionMessageThread().run();
        }
    }

    /**
     * Runnable that performs looped.
     */
    private class SendConditionMessageThread extends Observable implements Runnable {

        public void callRestart() {
            super.setChanged();
            super.notifyObservers();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                logger.error("send condition message queue process thread sheep 1 minute error.", ex);
            }
            this.addObserver(new sendConditionMessageObserver());
            while (true) {
                try {
                    process();
                    Thread.sleep(100);
                } catch (Exception ex) {
                    logger.error("SendConditionMessageThread run exception.", ex);
                    this.callRestart();
                    break;
                }
            }
        }
    }

    private void process() {

        String payload = stringRedisTemplate.opsForList().rightPopAndLeftPush(
                REDIS_SMS_QUEUE_SEND_KEY, REDIS_SMS_QUEUE_BACKUP_KEY,
                SMSMessageUtil.getTimeout(), TimeUnit.MILLISECONDS);
        if (StringUtils.isNotBlank(payload)) {
            Integer result = null;
            SmsInfo smsInfo = CacheUtil.doJacksonDeserialize(payload, SmsInfo.class);
            String mobile = smsInfo.getMobile();
            String content = smsInfo.getContent();
            Long scheduleMessageLogId = smsInfo.getScheduleMessageLogId();
            ScheduleMessageLog scheduleMessageLog = scheduleMessageLogRepository.findOne(scheduleMessageLogId);
            try {
                MDC.put(_FLOW_ID, UUID.randomUUID().toString());
                result = sendScheduleMessageHandler.execute(smsInfo);
                if (result != 0) {
                    scheduleMessageLog.setStatus(2);
                    sendMessageErrorHandler.conditionHandler(result, scheduleMessageLog.getScheduleMessage(), mobile, content);
                } else {
                    scheduleMessageLog.setStatus(1);//发送成功
                }
                scheduleMessageLogRepository.save(scheduleMessageLog);
            } catch (Exception ex) {
                logger.debug("send condition message exception, payload:({}), mobile:({}), scheduleMessageLogId:({})", payload, mobile, scheduleMessageLog, ex);
                scheduleMessageLog.setStatus(2);
                scheduleMessageLogRepository.save(scheduleMessageLog);
                sendMessageErrorHandler.conditionHandler(result, scheduleMessageLog.getScheduleMessage(), mobile, content);
            } finally {
                logger.debug("send condition message is finished, mobile:{}, content:{}, result:{}", mobile, content, result);
                stringRedisTemplate.opsForList().remove(REDIS_SMS_QUEUE_BACKUP_KEY, 0, payload);
            }
        }
    }
}
