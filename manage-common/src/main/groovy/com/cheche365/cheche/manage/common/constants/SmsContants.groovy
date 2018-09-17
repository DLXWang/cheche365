package com.cheche365.cheche.manage.common.constants

import org.slf4j.LoggerFactory

/**
 * Created by yinJianBin on 2017/6/2.
 */
class SmsContants {

    static def logger = LoggerFactory.getLogger(SmsContants.class)

    // 短信发送失败接收提醒邮件
    public static final String ERROR_SMS_REMIND_EMAIL;


    static {
        Properties properties = new Properties();
        try {
            properties.load(SmsContants.class.getResourceAsStream("/META-INF/spring/sms.properties"));
        } catch (Exception ex) {
            logger.error("load operation center properties file error.");
        }
        String profile = System.getProperty("spring.profiles.active");
        ERROR_SMS_REMIND_EMAIL = getProperty(properties, profile, "error.sms.remind.emails");
    }

    /**
     *
     * @param properties
     * @param profile
     * @param key
     * @return
     */
    static String getProperty(Properties properties, String profile, String key) {
        String profileKey = profile + "." + key;
        if (properties.containsKey(profileKey)) {
            return properties.getProperty(profileKey);
        } else {
            return properties.getProperty(key);
        }
    }


}
