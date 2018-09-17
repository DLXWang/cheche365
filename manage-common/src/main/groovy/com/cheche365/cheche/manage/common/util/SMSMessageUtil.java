package com.cheche365.cheche.manage.common.util;

/**
 * Created by sunhuazhong on 2015/10/21.
 */
public class SMSMessageUtil {

    // 定时发送短信任务
    public static final String JOB_DETAIL_ADHOC_MESSAGE_TIMER_TASK = "sms.timer.task";

    // redis中保存定时发送短信的key
    public static final String REDIS_ADHOC_MESSAGE_TASK_QUEUE_KEY = "adhoc:message:task:queue";

    // redis中保存定时发送短信的key
    public static final String REDIS_ADHOC_MESSAGE_TASK_QUEUE_BACKUP_KEY = "backup:adhoc:message:task:queue";

    // redis中保存的主动发送短信中用户群发送失败的用户信息
    public static final String REDIS_ADHOC_MESSAGE_FILTER_USER_FAIL_KEY = "adhoc.message.filter.user.fail";

    // redis中保存发送短信的key
    public static final String REDIS_SMS_QUEUE_SEND_KEY = "sms:send:message:queue";

    // redis中保存发送短信的备份的key
    public static final String REDIS_SMS_QUEUE_BACKUP_KEY = "backup:sms:send:message:queue";

    // 从redis中获取数据的超时时间，默认为1秒钟
    public static final int REDIS_TIMEOUT = 1000;

    private static int PER_SEND_USER_MOBILE_COUNT = 1000;

    public static String getJobDetailTimerTask() {
        return JOB_DETAIL_ADHOC_MESSAGE_TIMER_TASK;
    }

    public static String getTimerTaskQueueKey() {
        return REDIS_ADHOC_MESSAGE_TASK_QUEUE_KEY;
    }

    public static String getTimerTaskBackupQueueKey() {
        return REDIS_ADHOC_MESSAGE_TASK_QUEUE_BACKUP_KEY;
    }

    public static String getFilterUserFailKey() {
        return REDIS_ADHOC_MESSAGE_FILTER_USER_FAIL_KEY;
    }

    public static String getFilterUserFailContentHashKey(Long adhocMessageId) {
        return adhocMessageId + ":content";
    }

    public static String getFilterUserFailMobileHashKey(Long adhocMessageId) {
        return adhocMessageId + ":mobile";
    }

    public static String getSendMessageQueueKey() {
        return REDIS_SMS_QUEUE_SEND_KEY;
    }

    public static String getSendMessageBackupQueueKey() {
        return REDIS_SMS_QUEUE_BACKUP_KEY;
    }

    public static int getTimeout() {
        return REDIS_TIMEOUT;
    }

    public static int getPerSendMobileCount() {
        return PER_SEND_USER_MOBILE_COUNT;
    }
}
