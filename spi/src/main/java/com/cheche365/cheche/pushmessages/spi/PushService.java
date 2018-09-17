package com.cheche365.cheche.pushmessages.spi;

import com.cheche365.cheche.core.model.PushMessage;

/**
 * Created by liqiang on 7/17/15.
 */
public interface PushService {

    void push(String messageNo, Destination destination, String appId, String cerFile, String pwd);

    void push(int type, Destination destination, String appId, String cerFile, String pwd);

    void push(PushMessage pushMessage, Destination destination, String appId, String cerFile, String pwd);


}
