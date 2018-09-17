package com.cheche365.cheche.web.app.config;

import org.atmosphere.plugin.redis.RedisBroadcaster;

/**
 * Created by Administrator on 2017/2/16.
 */
public class WebBroadcaster extends RedisBroadcaster {

    @Override
    public void outgoingBroadcast(Object message) {
        if (null != message) {
            super.outgoingBroadcast(message);
        }
    }

}
