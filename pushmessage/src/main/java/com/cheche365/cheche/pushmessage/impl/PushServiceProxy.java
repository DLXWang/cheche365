package com.cheche365.cheche.pushmessage.impl;


import com.cheche365.cheche.core.model.PushMessage;
import com.cheche365.cheche.microservice.MicroServiceServer;
import com.cheche365.cheche.pushmessages.model.Platform;
import com.cheche365.cheche.pushmessages.spi.Destination;
import com.cheche365.cheche.pushmessages.spi.PushService;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by liqiang on 7/23/15.
 */
@Component
@Qualifier(value = "proxy")
public class PushServiceProxy implements PushService {

    @Autowired
    @Qualifier(value = "ios")
    private PushService iosPushService;

    private Logger logger = LoggerFactory.getLogger(PushServiceProxy.class);

    @Autowired
    private MicroServiceServer microServiceServer;

    public void push(String messageNo, Destination destination, String appId, String cerFile, String pwd) {
        //Vertx.vertx().executeBlocking(future -> {
            if (destination.isSupportedPlatform(Platform.IOS)) {
                iosPushService.push(messageNo, destination, appId, cerFile, pwd);
            } else {
                //TODO push message to android devices
            }
        //}, res -> {
        //});

    }

    @Override
    public void push(PushMessage pushMessage, Destination destination, String appId, String cerFile, String pwd) {
        //Vertx.vertx().executeBlocking(future -> {
            if (destination.isSupportedPlatform(Platform.IOS)) {
                iosPushService.push(pushMessage, destination, appId, cerFile, pwd);
            } else {
                //TODO push message to android devices
            }
        //}, res -> {
        //});
    }

    @Override
    public void push(int type, Destination destination, String appId, String cerFile, String pwd) {
        //Vertx.vertx().executeBlocking(future -> {
            if (destination.isSupportedPlatform(Platform.IOS)) {
                iosPushService.push(type, destination, appId, cerFile, pwd);
            } else {
                //TODO push message to android devices
            }
        //}, res -> {
        //});

    }

    @PostConstruct
    private void registerServiceToMicroServiceServer() {
        logger.debug("register push service to micro service server");
        microServiceServer.registerService(PushService.class, this);

    }


}
