package com.cheche365.cheche.wechat;

/**
 * Created by liqiang on 3/19/15.
 */

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 调用微信提供的接口，定时刷新access_token
 * 最新获得的access_token存储到redis中，其他应用从redis中获取最新的access_token
 */
@Component
public class AccessTokenManager implements Runnable {

    public static final int REFRESH_WAIT_TIME = 10000; //单位：毫秒
    public static final String WECHAT_PREFIX = "wechat:cached:tokens";

    private Logger logger = LoggerFactory.getLogger(AccessTokenManager.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private MessageSender messageSender;

    private boolean running = true;
    private String uuid = UUID.randomUUID().toString();

    private AccessToken fetchAccessTokenFromWechat(String appId) {
        if (logger.isInfoEnabled()) {
            logger.info("send request to wechat to get new access token");
        }
        AccessToken accessToken = messageSender.fetchAccessToken(appId);
        accessToken.setCreatedTime(System.currentTimeMillis());
        return accessToken;
    }

    @PostConstruct
    private void init() {
        Thread thread = new Thread(this, "AccessTokenDaemonThread");
        thread.setDaemon(true);
        logger.info("starting AccessTokenDaemonThread ...");
        thread.start();
    }

    private boolean getDistributedLock(String appId) {
        boolean locked = redisTemplate.opsForValue().setIfAbsent(getDistributedLockKey(appId), uuid);
        if (locked) {
            redisTemplate.expire(getDistributedLockKey(appId), 30, TimeUnit.SECONDS);
        }
        return locked;
    }

    private void releaseDistributedLock(String appId) {
        String uuid = (String) redisTemplate.opsForValue().get(getDistributedLockKey(appId));
        if (this.uuid.equals(uuid)) {
            redisTemplate.delete(getDistributedLockKey(appId));
        }
    }

    @Override
    public void run() {

        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (InterruptedException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            e.printStackTrace();
        }

        if (logger.isDebugEnabled()) {
            List config = redisTemplate.getConnectionFactory().getConnection().getConfig("*");
            logger.debug("redis config is: " + config);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("the property wechat.accesstoken.daemon is: [" + System.getProperty("wechat.accesstoken.daemon") + "]");
        }

        String wechatIntegrationEnabled = System.getProperty("wechat.accesstoken.daemon", "disabled");
        if (!("enabled".equalsIgnoreCase(wechatIntegrationEnabled))) {
            if (logger.isInfoEnabled()) {
                logger.info("wechat.accesstoken.daemon is not enabled, access token daemon thread will exit.");
            }
            return;
        }

        List<String> appIds = WechatConstant.getAccessTokenAppIds();
        while (running) {
            long sleepInterval = 7200 + 1;
            for (String appId : appIds) {
                AccessToken accessToken = refreshAccessToken(false, appId);
                if (accessToken != null && StringUtils.isNotBlank(accessToken.getToken())) {
                    long remains = accessToken.getExpiresIn() - ((System.currentTimeMillis() - accessToken.getCreatedTime()) / 1000);
                    remains = remains > 300 ? remains - 300 : 1;
                    sleepInterval = remains > sleepInterval ? sleepInterval : remains;
                } else {
                    logger.debug("get access token from wechat server failed , appId is: [" + appId + "]");
                }
            }

            try {
                logger.debug("fetch access token thread sleepInterval : [" + sleepInterval + "]");
                Thread.sleep(sleepInterval * 1000);
            } catch (InterruptedException e) {
                //nothing to do
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("closing AccessTokenDaemonThread ...");
        }
    }


    public synchronized String getAccessToken(String appId) {
        //第一步：从缓存中读取accessToken
        AccessToken accessToken = getAccessTokenFromCache(appId);

        if (accessToken != null) {
            return accessToken.getToken();
        } else {
            //第二步：从微信服务器刷新 access token
            accessToken = refreshAccessToken(false, appId);
            if (accessToken != null) {
                return accessToken.getToken();
            } else {
                //第三步： //没有获得分布式锁，等待其他进程刷新access token
                long startTimeStamp = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTimeStamp < REFRESH_WAIT_TIME) {
                    accessToken = getAccessTokenFromCache(appId);
                    if (accessToken != null) {
                        return accessToken.getToken();
                    }
                    try {
                        Thread.sleep(1000); //sleep 1 second
                    } catch (InterruptedException e) {
                        //nothing to do
                    }
                }
                logger.warn(String.format("Have waited [%d] seconds，but didn't get wechat access token.", REFRESH_WAIT_TIME / 1000));
                return null;
            }
        }
    }

    public synchronized String getJSTicket(String appId) {
        JSTicket jsTicket = CacheUtil.getObjectFromCache(redisTemplate, WECHAT_PREFIX, getJSTicketKey(appId), JSTicket.class);
        if (jsTicket != null) {
            return jsTicket.getTicket();
        } else {
            String accessToken = getAccessToken(appId);//try to refresh access token;
            if (accessToken != null) {
                jsTicket = CacheUtil.getObjectFromCache(redisTemplate, WECHAT_PREFIX, getJSTicketKey(appId), JSTicket.class);
                if (jsTicket != null) {
                    return jsTicket.getTicket();
                }
            }
        }

        //can't get js ticket, return null and log error
        logger.error("Can't get js ticket!!!");
        return null;
    }

    /**
     * this method will try to get a new access token from wechat if it can get distributed lock
     * even forceRefresh is true, it still doens't get new access token if the distributed lock can't be gotten.
     *
     * @param forceRefresh
     * @param appId
     * @return
     */
    public synchronized AccessToken refreshAccessToken(boolean forceRefresh, String appId) {
        boolean locked = getDistributedLock(appId);
        if (locked) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("lock [%s] successfully，will send request to wechat to get access token.", uuid));
                }

                if (forceRefresh) {
                    return getNewAccessToken(appId);
                }

                AccessToken accessToken = getAccessTokenFromCache(appId);
                if (accessToken != null) {
                    long now = System.currentTimeMillis();
                    long remains = accessToken.getExpiresIn() - ((now - accessToken.getCreatedTime()) / 1000);
                    if (remains <= 300) { //remain time less than 5 minutes;
                        return getNewAccessToken(appId);
                    } else {
                        logger.info(String.format("access token will be expired in [%d] seconds, " +
                            "don't need to get new access token now", remains));
                        return accessToken;
                    }
                } else {
                    return getNewAccessToken(appId);
                }
            } finally {
                releaseDistributedLock(appId);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("release lock[%s]", uuid));
                }
            }

        }
        return null;
    }

    private AccessToken getNewAccessToken(String appId) {
        AccessToken accessToken = fetchAccessTokenFromWechat(appId);
        saveAccessTokenToCache(accessToken, appId);

        //get new jsticket too
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "jsapi");
        JSTicket jsTicket = messageSender.getMessageForObject(WechatConstant.JS_TICKET_PATH, parameters, JSTicket.class, true, appId);
        CacheUtil.putObjectToCache(redisTemplate, WECHAT_PREFIX, getJSTicketKey(appId), jsTicket);

        return accessToken;
    }

    private AccessToken getAccessTokenFromCache(String appId) {
        return CacheUtil.getObjectFromCache(redisTemplate, WECHAT_PREFIX, getAccessTokenKey(appId), AccessToken.class);
    }

    private void saveAccessTokenToCache(AccessToken accessToken, String appId) {
        CacheUtil.putObjectToCache(redisTemplate, WECHAT_PREFIX, getAccessTokenKey(appId), accessToken);
    }

    private String getAccessTokenKey(String appId) {
        return "accessToken:" + appId;
    }

    private String getJSTicketKey(String appId) {
        return "jsticket:" + appId;
    }

    private String getDistributedLockKey(String appId) {
        return "wechat:accesstoken:lock:" + appId;
    }
}

