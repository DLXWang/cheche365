package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.repository.WechatUserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by liqiang on 4/4/15.
 */
@Component
public class PaymentManager {

    protected final static int RETRY_TIMES = 5;
    private Logger logger = LoggerFactory.getLogger(PaymentManager.class);
    protected static final String PROCESSED_ORDER_KEY = "wechat:processed_order";

    protected String ip;

    @Autowired
    protected RedisTemplate redisTemplate;

    @Autowired
    protected WechatUserInfoRepository wechatUserInfoRepository;

    @Autowired
    protected MessageSender messageSender;

    public PaymentManager(){
        ip = getPublicIP();
    }

    private String getPublicIP() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
            return in.readLine();

        } catch (MalformedURLException e) {
            logger.warn("can't find public ip by access amazon service",e);
        } catch (IOException e) {
            logger.warn("can't find public ip by access amazon service", e);
        }

        try {
            return InetAddress.getByName(WebConstants.getDomain()).getHostAddress();
        } catch (UnknownHostException e) {
            try {
                logger.warn("can't get ip address for " + WebConstants.getDomain() + "will try to get local ip address.");
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e1) {
                logger.warn("can't get ip address of current server, will return empty String");
            }
        }

        return "";
    }

}
