package com.cheche365.cheche.operationcenter.service.sms.tesk;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.AdhocMessage;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.util.SMSMessageUtil;
import com.cheche365.cheche.operationcenter.service.sms.ImmediateSendAdhocMessageService;
import com.cheche365.cheche.operationcenter.util.ScheduleUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sunhuazhong on 2015/10/8.
 */
@Component
public class ScheduleMessageQueueProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ImmediateSendAdhocMessageService immediateSendAdhocMessageService;

    @PostConstruct
    public void init() throws Exception {
        // init不能卡住，影响Spring的启动，需要在新的线程处理。
//        ExecutorService threadPool = Executors.newSingleThreadExecutor();
//        threadPool.submit(new SendScheduleMessageThread());
    }


    /**
     * Runnable that performs looped.
     */
    public class SendScheduleMessageThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                logger.error("send schedule message queue process thread sheep 1 minute error.", ex);
            }
            while (stringRedisTemplate.opsForList().size(SMSMessageUtil.getTimerTaskQueueKey()) > 0) {
                try {
                    process();
                    Thread.sleep(100);
                } catch (Exception ex) {
                    logger.error("send schedule message process error.", ex);
                }
            }
        }
    }

    private void process() {
        // 获取定时发送短信对象
        String adhocMessageInString = stringRedisTemplate.opsForList().rightPopAndLeftPush(
                SMSMessageUtil.getTimerTaskQueueKey(), SMSMessageUtil.getTimerTaskBackupQueueKey(),
                SMSMessageUtil.getTimeout(), TimeUnit.MILLISECONDS);
        if (StringUtils.isNotBlank(adhocMessageInString)) {
            try {
                // 判断该定时发送短信是否会执行，发送时间早于当前时间，则不创建，改为立即发送
                boolean blnCreate = checkCreateSchedule(adhocMessageInString);
                logger.debug("check create schedule task,result:{}(true：create schedule task，false：send message now)", blnCreate);
                if (blnCreate) {
                    // 创建任务
                    ScheduleUtil.createSchedule(adhocMessageInString);
                    logger.debug("create schedule task is finished,value;{}", adhocMessageInString);
                } else {
                    AdhocMessage adhocMessage = CacheUtil.doJacksonDeserialize(adhocMessageInString, AdhocMessage.class);
                    int result = immediateSendAdhocMessageService.sendMessage(adhocMessage);
                    logger.debug("send schedule message now is finished,value;{},result:{}", adhocMessageInString, result);
                }
            } catch (Exception ex) {
                logger.error("send schedule message queue error.", ex);
            } finally {
                stringRedisTemplate.opsForList().remove(SMSMessageUtil.getTimerTaskBackupQueueKey(), 0, adhocMessageInString);
            }
        }
    }

    private boolean checkCreateSchedule(String adhocMessageInString) {
        AdhocMessage adhocMessage = CacheUtil.doJacksonDeserialize(adhocMessageInString, AdhocMessage.class);
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        if (adhocMessage.getSendTime().after(currentTime)) {
            return true;
        }
        return false;
    }
}
