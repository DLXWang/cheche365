package com.cheche365.cheche.soopay

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.util.ProfileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

/**
 * Created by mjg on 2017/6/17.
 */
public class SoopayConstant {
    private static Logger logger = LoggerFactory.getLogger(SoopayConstant.class);

    public static String _SOOPAY_CALLBACK_REFUND_URL;//退款回调地址
    private static String _SOOPAY_CALLBACK_FRONT_URL;//前台回调地址
    private static String _SOOPAY_CALLBACK_BACK_URL;//后台通知地址
    public static String _SOOPAY_REQUEST_PATH;//联动优势接口请求地址
    private static String _SOOPAY_WITHDRAW_CALLBACK_NOTICE_URL;  //提现异步通过回调地址

    public static void init() {

        ProfileProperties myProperties = getProperties("/properties/soopay.properties");

        _SOOPAY_REQUEST_PATH = myProperties.getProperty("umsdk.frontRequestUrl");
        _SOOPAY_CALLBACK_FRONT_URL = myProperties.getProperty("callback_front_url");
        _SOOPAY_CALLBACK_BACK_URL = myProperties.getProperty("callback_back_url");
        _SOOPAY_CALLBACK_REFUND_URL = myProperties.getProperty("callback_refund_url");
        _SOOPAY_WITHDRAW_CALLBACK_NOTICE_URL = myProperties.getProperty("withdraw_callback_notice_url");
    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(SoopayConstant.class.getResourceAsStream(path));
        } catch (IOException ex) {
            logger.error("load soopay properties files has error", ex);
        }
        return new ProfileProperties(properties);
    }

    public static String getSoopayCallbackBackUrl() {
        return WebConstants.getSchemaURL()+_SOOPAY_CALLBACK_BACK_URL;
    }
    public static String getSoopayWithdrawCallbackUrl() {
        return WebConstants.getSchemaURL()+ _SOOPAY_WITHDRAW_CALLBACK_NOTICE_URL;
    }


}
