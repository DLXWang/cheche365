package com.cheche365.cheche.marketing.message.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created by zhengwei on 3/18/16.
 * 为营销活动同步电销报价到quote record中，暂时未使用
 */
@Component
public class TMQuoteListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
       throw new IllegalStateException("暂不支持方法");
    }

}
