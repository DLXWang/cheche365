package com.cheche365.cheche.core.message;

import com.cheche365.cheche.core.model.QuoteRecord;

/**
 * Created by zhengwei on 3/18/16.
 */
public class TMQuoteMessage extends QueueMessage<String, QuoteRecord> {

    public static final String QUEUE_NAME = "pubsub:tmquote";
    public static final String QUEUE_SET = "set:tmquote";

    public TMQuoteMessage() {
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
