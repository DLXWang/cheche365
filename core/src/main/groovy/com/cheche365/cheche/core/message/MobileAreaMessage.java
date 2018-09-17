package com.cheche365.cheche.core.message;


/**
 * 手机所在地区消息同步
 * Created by xu.yelong on 2016/7/16.
 */
public class MobileAreaMessage extends QueueMessage<String, String> {
    public static final String QUEUE_NAME = "pubsub:syncarea";
    public static final String QUEUE_SET = "set:syncarea";

    public MobileAreaMessage() {
        super();
    }

    @Override
    public String getQueueName() {
        return QUEUE_NAME;
    }

    @Override
    public String getQueueSet() {
        return QUEUE_SET;
    }
}
