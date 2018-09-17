package com.cheche365.cheche.core.message;


import com.cheche365.cheche.core.model.MarketingRule;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class PartnerMarketingMessage extends QueueMessage<String,List<MarketingRule>>{

        public static final String QUEUE_NAME = "pubsub:marketing";
        public static final String QUEUE_SET = "set:marketing";
    
    public  PartnerMarketingMessage(){
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
