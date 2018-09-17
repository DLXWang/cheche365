package com.cheche365.cheche.alipay.util;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.util.ProfileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by chenxiaozhe on 15-8-17.
 */
public class AliPayConstant {
    private static Logger logger = LoggerFactory.getLogger(AliPayConstant.class);
    public static String LOG_TAG = "ALIPAY_LOG_TAG: ";
    // 字符编码格式
    public static String INPUT_CHARSET = "utf-8";
    //log path
    public static String PARTNER;
    public static String WAP_PARTNER;
    public static String SELLER_EMAIL;
    public static String KEY;
    public static String DIRECTPAY_VERIFY_URL;

    private static String DIRECTPAY_RETURN_URL;
    private static String WAPPAY_NOTIFY_URL;
    private static String IOSPAY_NOTIFY_URL;

    public static String ALIPAY_GATEWAY_URL;
    public static String WAP_ALIPAY_GATEWAY_URL;
    public static String ROOT_URL;
    public static String SIGN_MD5 = "MD5";
    public static String SIGN_RSA = "RSA";
    public static String PAY_TIME = "24h";
    //默认为1,商品购买
    public static String PAYMENT_TYPE = "1";
    //退款地址
    public static String WAPREFUND_SERVICE = "refund_fastpay_by_platform_nopwd";
    //移动端支付
    public static String MOBILE_SERVICE = "mobile.securitypay.pay";
    //商户私钥
    public static String RSA_PRI_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMgp3en7STzBBEqpm5odTXzamb/S+NQclFrg9Ura6TB+mqOKmK56kFV6iyIQGU9MkZNPW6wBCuW904hRBxJ7iHfkyj+R7wspYL755ru+ZFj2OqWNnljSiLnedc9ao9jRr+udA2vAobyUrC5k7vVVd+d9G+mY7otCHn1fdBzJt2M5AgMBAAECgYA+NHyprZVKEzYROfqIf5jIN2tWqOr5iFQ2lfjFJZkYoA/QyU6/0okud2Hr2RL0iPgozp3Pq5dGZKLrlzrabJexFYK8AlJkop7N+V0n+cpyeL7HTz31+sTr2ipLm56A0yvA5vV49U3r7Wh4B+5ehRfqoDbqLOZ/0nV+vlRQiClmsQJBAPmi5tO4GKJkWUp2Rh8DDq96/TV1Y1Y4A+5IcOx8dF0UiD/+Oqvx+RXkWwPyOZ3zzbMKgdI5EG7z05XlplHulR0CQQDNRByCjjEY7FN7ls3l1qjSE6hrZnh2LsQAkNh3msk6ZBtx9MO+KSFiV/2bWrIyL/pDhNMok/X3o5C2s6UIB/fNAkBmq5MX/J1VHMSElYRdeNpvXbwKYo9KhJtJQ03+VWleZvVqrFrhIRH66QJt6w+7YOx2+JlQJtwVZf7dpaf0BpYZAkEAoVxcySj8YapOCkbHf1mbBuIbMKqyIb2rqQI0mPleHP/bL18JjXCJ5ORk4f6PPLLImtFMqYZ/AnhbZ7SbFFAnNQJAaaV563TcdcoNCQ6DnWo5BWu+dAv5hkrhmiTfQkYl07B57QJXGRI8ZbHahhjv1pEpTzokjyldxKJNPGFAx7E0rQ==";
    //支付宝公钥
    public static String RSA_PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    static {
        ProfileProperties alipayProperties = getProperties("/properties/alipay.properties");
        //支付类型(商品购买)
        PAYMENT_TYPE = "1";
        // 合作身份者ID，以2088开头由16位纯数字组成的字符串
        PARTNER = "2088911040184910";
        WAP_PARTNER = alipayProperties.getProperty("wappay.partner");
        // 收款支付宝账号
        SELLER_EMAIL = "zfb@cheche365.com";
        // 商户的私钥key
        KEY = "nrt5j2fgeckzam534l5uvtfh1ycbruxl";
        //支付宝消息验证地址
        DIRECTPAY_VERIFY_URL = alipayProperties.getProperty("directpay.verify.url");
        //页面跳转同步通知页面路径http
        DIRECTPAY_RETURN_URL = alipayProperties.getProperty("directpay.return.url");
        //wap服务器异步通知页面路径http
        WAPPAY_NOTIFY_URL = alipayProperties.getProperty("wappay.notify.url");
        //ios异步通知http
        IOSPAY_NOTIFY_URL = alipayProperties.getProperty("iospay.notify.url");
        //支付宝提供给商户的服务接入网关URL
        ALIPAY_GATEWAY_URL = alipayProperties.getProperty("directpay.alipay.geteway.url");
        WAP_ALIPAY_GATEWAY_URL = alipayProperties.getProperty("wappay.alipay.geteway.url");

        ROOT_URL = alipayProperties.getProperty("root.url");
    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(AliPayConstant.class.getResourceAsStream(path));
        } catch (IOException ex) {
            logger.error("load alipay properties files error", ex);
        }
        return new ProfileProperties(properties);
    }

    public static String getIosPayNotifyUrl(){
        return WebConstants.getSchemaURL()+IOSPAY_NOTIFY_URL;
    }

    public static String getWapPayNotifyUrl(){
        return WebConstants.getSchemaURL()+WAPPAY_NOTIFY_URL;
    }

    public static String getDirectPayReturnUrl(){
        return WebConstants.getSchemaURL()+DIRECTPAY_RETURN_URL;
    }

}
