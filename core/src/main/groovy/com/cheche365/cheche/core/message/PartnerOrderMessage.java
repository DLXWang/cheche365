package com.cheche365.cheche.core.message;

/**
 * Created by mahong on 2016/3/29.
 */
public class PartnerOrderMessage extends QueueMessage<String, Object> {

    public static final String QUEUE_NAME = "pubsub:syncorder";
    public static final String QUEUE_SET = "set:syncorder";

    public PartnerOrderMessage() {
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
