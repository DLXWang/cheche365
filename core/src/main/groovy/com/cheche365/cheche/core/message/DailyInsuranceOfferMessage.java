package com.cheche365.cheche.core.message;

/**
 * Created by chenqiuchang on 2017/2/24.
 */
public class DailyInsuranceOfferMessage extends QueueMessage<String, Object> {

    public static final String QUEUE_NAME = "pubsub:syncoffer";
    public static final String QUEUE_SET = "set:syncoffer";

    public DailyInsuranceOfferMessage() {
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
