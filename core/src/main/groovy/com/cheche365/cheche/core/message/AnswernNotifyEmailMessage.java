package com.cheche365.cheche.core.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahong on 2016/5/18.
 */
public class AnswernNotifyEmailMessage extends QueueMessage<String, Map<String, String>> {

    public static final String QUEUE_NAME = "pubsub:answernnotifyemail";
    public static final String QUEUE_SET = "set:answernnotifyemail";
    public static final String NOTIFY_UNIQUE_NAME = "unique_name";
    public static final String NOTIFY_MSG = "msg";
    public static final String TEMPLATE_CODE = "'template_code'";

    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }

    @Override
    public String getQueueSet() {
        return QUEUE_SET;
    }

    public AnswernNotifyEmailMessage(String notifyMsg) {
        Map<String, Object> paramMap = new HashMap<>();
        String uniqueName = String.valueOf(System.currentTimeMillis());
        paramMap.put(AnswernNotifyEmailMessage.NOTIFY_UNIQUE_NAME, uniqueName);
        paramMap.put(AnswernNotifyEmailMessage.NOTIFY_MSG, notifyMsg);
        super.setKey(uniqueName).setMessage(paramMap);
    }


    public AnswernNotifyEmailMessage(String notifyMsg, String templateCode) {
        Map<String, Object> paramMap = new HashMap<>();
        String uniqueName = String.valueOf(System.currentTimeMillis());
        paramMap.put(AnswernNotifyEmailMessage.NOTIFY_UNIQUE_NAME, uniqueName);
        paramMap.put(AnswernNotifyEmailMessage.NOTIFY_MSG, notifyMsg);
        paramMap.put(AnswernNotifyEmailMessage.TEMPLATE_CODE,templateCode);
        super.setKey(uniqueName).setMessage(paramMap);
    }

}
