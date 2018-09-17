package com.cheche365.cheche.wechat;

import com.cheche365.cheche.wechat.message.InMessage;

/**
 * Created by liqiang on 3/24/15.
 */
public interface IMessageProcessor {

    String process(InMessage inMessage);

}
