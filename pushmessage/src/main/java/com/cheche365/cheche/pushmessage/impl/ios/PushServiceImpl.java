package com.cheche365.cheche.pushmessage.impl.ios;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.Device;
import com.cheche365.cheche.core.model.PushMessage;
import com.cheche365.cheche.core.repository.PushMessageRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.pushmessages.model.Platform;
import com.cheche365.cheche.pushmessages.spi.Destination;
import com.cheche365.cheche.pushmessages.spi.PushService;
import com.cheche365.cheche.pushmessages.spi.impl.UserDeviceDestination;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by liqiang on 7/17/15.
 */
@Component
@Qualifier("ios")
public class PushServiceImpl implements PushService {

    private ApnsService apnsService;
    private Logger logger = LoggerFactory.getLogger(PushServiceImpl.class);

    @Autowired
    private Environment env;

    @Autowired
    private PushMessageRepository pushMessageRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private void initializedApns(String appId, String cerFile, String pwd) {
        logger.debug("initialize ios push service");
        if (Arrays.asList(env.getActiveProfiles()).contains("production")) {
            apnsService = APNS.newService()
                .withCert(
                    this.getClass().getResourceAsStream(
                        "/certificate/" + appId + "/" + cerFile)
                    , pwd)
                .withProductionDestination()
                .build();
        } else {
            apnsService = APNS.newService()
                .withCert(
                    this.getClass().getResourceAsStream(
                        "/certificate/" + appId + "/" + cerFile)
                    , pwd)
                .withSandboxDestination()
                .build();
        }
    }

    @Override
    public void push(String messageNo, Destination destination, String appId, String cerFile, String pwd) {

        PushMessage message = pushMessageRepository.findByMessageNoAndDisable(messageNo, false);

        if (message == null) {
            logger.warn("can't find enabled message with message no [" + messageNo + "]");
            return;
        }

        push(message, destination, appId, cerFile, pwd);
    }

    @Override
    public void push(int type, Destination destination, String appId, String cerFile, String pwd) {
        List<PushMessage> pushMessages = pushMessageRepository.findByTypeAndDisable(type, false);
        pushMessage(pushMessages, destination, appId, cerFile, pwd);
    }

    @Override
    public void push(PushMessage message, Destination destination, String appId, String cerFile, String pwd) {
        if (message == null) {
            logger.warn("message is null, will return");
            return;
        }

        List<PushMessage> messages = new ArrayList<>();
        messages.add(message);
        pushMessage(messages, destination, appId, cerFile, pwd);
    }

    private int increaseBadge(String deviceToken) {
        return redisTemplate.opsForHash().increment(WebConstants.PUSH_MESSAGE_BADGE_KEY, deviceToken, 1).intValue();
    }

    private void pushMessage(List<PushMessage> messages, Destination destination, String appId, String cerFile, String pwd) {
        //logger.debug(String.format(" send message from ip : %s ", TopologyHelper.getLocalInterface()));
        if (apnsService == null) {
            initializedApns(appId, cerFile, pwd);
        }
        if (messages == null) {
            logger.warn("messages in null, will return.");
            return;
        }
        logger.debug("push message list size:" + messages.size());
        List<String> destinationDevices = destination.getDestinationDevices(Platform.IOS);
        if (CollectionUtils.isEmpty(destinationDevices)) {
            logger.warn("destination devices are empty, will return.");
            return;
        }

        logger.debug("destination devices size:" + destinationDevices.size());


        messages.forEach(pushMessage -> {
            logger.debug(String.format("push message body [%s]", pushMessage.getBody()));
            if (!pushMessage.isAutoIncrementBadge()) {
                String payload = APNS.newPayload()
                    .alertTitle(pushMessage.getTitle())
                    .alertBody(pushMessage.getBody())
                    .sound(pushMessage.getSound())
                    .customField("operation", pushMessage.getOperation())
                    .customField("url", pushMessage.getData())
                    .build();
                List<String> deviceTokenList = destination.getDestinationDevices(Platform.IOS);
                apnsService.push(deviceTokenList, payload);

                //记录PushMessage信息
                recordPushMessageToRedis(deviceTokenList, destination, pushMessage);
            } else {
                destination.getDestinationDevices(Platform.IOS).forEach(
                    deviceToken -> {
                        String payload = APNS.newPayload()
                            .alertTitle(pushMessage.getTitle())
                            .alertBody(pushMessage.getBody())
                            .badge(increaseBadge(deviceToken))
                            .sound(pushMessage.getSound())
                            .customField("operation", pushMessage.getOperation())
                            .customField("data", pushMessage.getData())
                            .build();
                        apnsService.push(deviceToken, payload);

                        //记录PushMessage信息
                        recordPushMessageToRedis(Arrays.asList(deviceToken), destination, pushMessage);
                    }
                );
            }

        });
    }

    private void recordPushMessageToRedis(List<String> deviceTokenList, Destination destination, PushMessage pushMessage) {
        try {
            deviceTokenList.forEach(deviceToken -> {
                if (destination instanceof UserDeviceDestination) {
                    Device device = ((UserDeviceDestination) destination).getDeviceMap().get(deviceToken);
                    if (null == device.getUser()) {
                        return;
                    }
                    String redisKey = WebConstants.PUSHMESSAGE_REDIS_KEY_PREFIX + "_" + device.getUser().getId();
                    pushMessage.setCreateTime(Calendar.getInstance().getTime());
                    redisTemplate.opsForList().leftPush(redisKey, CacheUtil.doJacksonSerialize(pushMessage));
                    if (maxRecordLength < redisTemplate.opsForList().size(redisKey)) {
                        redisTemplate.opsForList().rightPop(redisKey);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Log PushMessage ERROR", e);
        }
    }

    public static final int maxRecordLength = 10;


}
