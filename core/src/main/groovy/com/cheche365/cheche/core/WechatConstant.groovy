package com.cheche365.cheche.core

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.util.ProfileProperties
import org.apache.commons.lang3.StringUtils
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.properties.EncryptableProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.cheche365.cheche.core.model.Channel.Enum.*
import static com.cheche365.cheche.core.util.MockUrlUtil.findMockSessionAttribute

/**
 * Created by liqiang on 3/19/15.
 */
class WechatConstant {

    private static Logger logger = LoggerFactory.getLogger(WechatConstant.class)

    public static final String APP_ID
    public static final String APP_SECRET
    public static final String APP_ID_FOR_PAYMENT
    public static final String API_SECRET
    public static final String MCH_ID

    public static final String FANHUA_APP_ID
    public static final String FANHUA_APP_SECRET
    public static final String FANHUA_APP_ID_FOR_PAYMENT
    public static final String FANHUA_API_SECRET
    public static final String FANHUA_MCH_ID

    public static final String IOS_APP_ID
    public static final String IOS_MCH_ID

    public static final String ANDROID_APP_ID
    public static final String ANDROID_MCH_ID

    public static final String WECHAT_APP_ID
    public static final String WECHAT_APP_SECRET
    public static final String WECHAT_APP_MCH_ID

    public static final String CLAIM_APP_ID
    public static final String CLAIM_APP_SECRET

    public static final String CHEBAOYI_APP_ID
    public static final String CHEBAOYI_APP_SECRET
    public static final String CHEBAOYI_UNIQUE_ID

    public static final String BASE_URL
    public static final String PAY_BASE_URL
    public static final String CERTIFICATE_PATH = "/cert/"
    public static final String CERTIFICATE_FILENAME = "/apiclient_cert.p12"
    public static final String TOKEN
    public static final String LANG

    public static final String ACCESS_TOKEN_PATH = "/cgi-bin/token"
    public static final String JS_TICKET_PATH = "/cgi-bin/ticket/getticket"
    public static final String WECHAT_CONF_PASSWORD = "wechat.conf.password"
    public static final String WECHAT_SUCCESS = "SUCCESS"//成功

    private static Map APP_CONFIG

    static {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor()
        encryptor.setPassword(System.getProperty(WECHAT_CONF_PASSWORD, "password")) // could be got from web, env variable...
        Properties properties = new EncryptableProperties(encryptor)
        try {
            properties.load(WechatConstant.class.getResourceAsStream("/wechat.properties"))
        } catch (IOException e) {
            logger.error("load wechat configuration file failed,", e)
        }
        ProfileProperties profileProperties = new ProfileProperties(properties)

        APP_ID = profileProperties.getProperty("appid")
        APP_SECRET = profileProperties.getProperty("appsecret")

        IOS_APP_ID = profileProperties.getProperty("ios.appid")
        IOS_MCH_ID = profileProperties.getProperty("ios.mch_id")

        ANDROID_APP_ID = profileProperties.getProperty("android.appid")
        ANDROID_MCH_ID = profileProperties.getProperty("android.mch_id")

        WECHAT_APP_ID = profileProperties.getProperty("wechatApp.appid")
        WECHAT_APP_SECRET = profileProperties.getProperty("wechatApp.appsecret")
        WECHAT_APP_MCH_ID = profileProperties.getProperty("wechatApp.mchid")

        CLAIM_APP_ID = profileProperties.getProperty("claimApp.appid")
        CLAIM_APP_SECRET = profileProperties.getProperty("claimApp.appsecret")

        CHEBAOYI_APP_ID = profileProperties.getProperty("chebaoyi.appid")
        CHEBAOYI_APP_SECRET = profileProperties.getProperty("chebaoyi.appsecret")
        CHEBAOYI_UNIQUE_ID = profileProperties.getProperty("chebaoyi.uniqueid")

        BASE_URL = profileProperties.getProperty("base_url")
        PAY_BASE_URL = profileProperties.getProperty("pay_base_url")
        TOKEN = profileProperties.getProperty("token")

        APP_ID_FOR_PAYMENT = profileProperties.getProperty("production.appid")
        API_SECRET = profileProperties.getProperty("production.apisecret")
        MCH_ID = profileProperties.getProperty("production.mch_id")

        FANHUA_APP_ID = profileProperties.getProperty("production.fanhua.appid")
        FANHUA_APP_SECRET = profileProperties.getProperty("production.fanhua.appsecret")
        FANHUA_APP_ID_FOR_PAYMENT = profileProperties.getProperty("production.fanhua.appid")
        FANHUA_API_SECRET = profileProperties.getProperty("production.fanhua.apisecret")
        FANHUA_MCH_ID = profileProperties.getProperty("production.fanhua.mch_id")

//        if (StringUtils.isNotBlank(System.getProperty(WECHAT_CONF_PASSWORD))) {
//            //in order to support test payment in test environment, hard-code to read some properties in production profile
//            APP_ID_FOR_PAYMENT = profileProperties.getProperty("production.appid")
//            MCH_ID = profileProperties.getProperty("production.mch_id")
//            API_SECRET = profileProperties.getProperty("production.apisecret")
//        } else {
//            APP_ID_FOR_PAYMENT = APP_ID
//            MCH_ID = profileProperties.getProperty("mch_id")
//            API_SECRET = profileProperties.getProperty("apisecret")
//        }

        LANG = profileProperties.getProperty("lang")

    }

    static Map getAppConfig() {
        if (!APP_CONFIG) {
            APP_CONFIG = [
                (WE_CHAT_3)        : [
                    'appId'    : APP_ID,
                    'appSecret': APP_SECRET
                ],
                (IOS_4)              : [
                    'appId': IOS_APP_ID,
                    'mchId': IOS_MCH_ID
                ],
                (ANDROID_6)          : [
                    'appId': ANDROID_APP_ID,
                    'mchId': ANDROID_MCH_ID
                ],
                (WE_CHAT_APP_39)     : [
                    'appId'    : WECHAT_APP_ID,
                    'appSecret': WECHAT_APP_SECRET,
                    'mchId'    : WECHAT_APP_MCH_ID
                ],
                (CLAIM_APP_214)     : [
                    'appId'    : CLAIM_APP_ID,
                    'appSecret': CLAIM_APP_SECRET
                ],
                (PARTNER_CHEBAOYI_67): [
                    'appId'    : CHEBAOYI_APP_ID,
                    'appSecret': CHEBAOYI_APP_SECRET
                ]
            ]
        }
        APP_CONFIG
    }

    static String getAppId(Channel channel) {
        getAppConfig().get(channel)?.appId
    }

    static String getAppSecret(Channel channel) {
        getAppConfig().get(channel)?.appSecret
    }

    static String getAppSecretByAppId(String appId) {
        getAppConfig().entrySet().find { it.value.appId == appId }?.value.appSecret
    }

    static List<String> getAccessTokenAppIds() {
        getAppConfig().entrySet().findAll { !Channel.selfApp().contains(it.key) }?.value.collect { it.appId }
    }

    static String getPayCallBackUrl() {
        return WebConstants.getDomainURL() + "/web/wechat/payment/callback"
    }

    static String findAppId(Channel channel) {
        if (IOS_4 == channel) {
            return IOS_APP_ID
        }
        if (ANDROID_6 == channel) {
            return ANDROID_APP_ID
        }
        if (WE_CHAT_APP_39 == channel) {
            return WECHAT_APP_ID
        }
        return APP_ID_FOR_PAYMENT

    }

    static String findMchId(Channel channel) {
        if (IOS_4 == channel) {
            return IOS_MCH_ID
        }
        if (ANDROID_6 == channel) {
            return ANDROID_MCH_ID
        }
        if (WE_CHAT_APP_39 == channel) {
            return WECHAT_APP_MCH_ID
        }
        return MCH_ID
    }

    static Channel findPaymentClient(String mchId){
        if(mchId == IOS_MCH_ID){
            return IOS_4
        }
        if(mchId == ANDROID_MCH_ID){
            return ANDROID_6
        }
        if(mchId == WECHAT_APP_MCH_ID){
            return WE_CHAT_APP_39
        }
        return WE_CHAT_3
    }

    static String baseUrl(String path) {
        Boolean threadRun = [ACCESS_TOKEN_PATH, JS_TICKET_PATH].contains(path)
        threadRun ? BASE_URL : (findMockSessionAttribute('mock_wechat_base_url') ?: BASE_URL)
    }

    static String payBaseUrl() {
        findMockSessionAttribute('mock_wechat_pay_base_url') ?: PAY_BASE_URL
    }

}
