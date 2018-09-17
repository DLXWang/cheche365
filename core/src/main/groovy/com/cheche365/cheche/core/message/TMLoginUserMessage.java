package com.cheche365.cheche.core.message;

import com.cheche365.cheche.core.model.UserLoginInfo;

/**
 * Created by chenxiaozhe on 16-3-24.
 */
public class TMLoginUserMessage extends QueueMessage<String, UserLoginInfo> {

    public static final String QUEUE_NAME = "pubsub:userlogin";
    public static final String QUEUE_SET = "set:userlogin";

    public TMLoginUserMessage() {
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
