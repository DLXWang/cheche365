package com.cheche365.cheche.unionpay;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.util.ProfileProperties;
import com.unionpay.acp.sdk.SDKConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by wangfei on 2015/7/8.
 */
public class UnionPayConstant {
    private static Logger logger = LoggerFactory.getLogger(UnionPayConstant.class);

    private static String UNION_PAY_CALLBACK_FRONT_URL;//前台回调地址
    private static String UNION_PAY_CALLBACK_BACK_URL;//后台通知地址
    public static int UNION_PAY_CONNECTION_TIMEOUT = 10000;
    public static String UNION_PAY_SIGN_CERT_PATH_MOBILE;//移动端签名证书路径
    public static String UNION_PAY_SIGN_CERT_PATH_PC;//PC端签名证书路径
    public static String UNION_PAY_SIGN_CERT_PATH_MOBILE_KQ;//康桥移动端签名证书路径
    public static String UNION_PAY_SIGN_CERT_PATH_PC_KQ;//康桥PC端签名证书路径
    public static String UNION_PAY_SIGN_CERT_PATH_APPLEPAY;//apple pay康桥商户号证书路径
    public static String UNION_PAY_SIGN_CERT_PASSWORD;//商户签名证书密码
    public static String UNION_PAY_SIGN_CERT_PASSWORD_KQ;//康桥商户签名证书密码
    public static String UNION_PAY_SIGN_CERT_PASSWORD_APPLEPAY;//康桥apple pay商户签名证书密码
    public static String UNION_PAY_CHECHE_TEST;//是否为测试环境 测试环境支付金额1分

    public static void init() {

        ProfileProperties acpProperties = getProperties("/properties/acp_sdk.properties");
        ProfileProperties myProperties = getProperties("/properties/acp_back.properties");
        SDKConfig.getConfig().loadProperties(acpProperties);

        UNION_PAY_SIGN_CERT_PATH_MOBILE = acpProperties.getProperty("mobile.acpsdk.signCert.path");
        UNION_PAY_SIGN_CERT_PATH_PC = acpProperties.getProperty("pc.acpsdk.signCert.path");
        //康桥于2016年12月15日下线
        UNION_PAY_SIGN_CERT_PATH_MOBILE_KQ = acpProperties.getProperty("mobile.acpsdk.signCert.path.kq");
        UNION_PAY_SIGN_CERT_PATH_PC_KQ = acpProperties.getProperty("pc.acpsdk.signCert.path.kq");
        UNION_PAY_SIGN_CERT_PATH_APPLEPAY = acpProperties.getProperty("mobile.acpsdk.signCert.path.applepay");
        UNION_PAY_SIGN_CERT_PASSWORD = acpProperties.getProperty("acpsdk.signCert.pwd");
        UNION_PAY_SIGN_CERT_PASSWORD_KQ = acpProperties.getProperty("acpsdk.signCert.pwd.kq");
        UNION_PAY_SIGN_CERT_PASSWORD_APPLEPAY = acpProperties.getProperty("acpsdk.signCert.pwd.applepay");
        UNION_PAY_CALLBACK_FRONT_URL = myProperties.getProperty("callback_front_url");
        UNION_PAY_CALLBACK_BACK_URL = myProperties.getProperty("callback_back_url");
        UNION_PAY_CHECHE_TEST = myProperties.getProperty("cheche_test");
    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(UnionPayConstant.class.getResourceAsStream(path));
        } catch (IOException ex) {
            logger.error("load unionPay properties files has error", ex);
        }
        return new ProfileProperties(properties);
    }

    public static String getUnionPayCallbackFrontUrl(){
        return WebConstants.getSchemaURL()+UNION_PAY_CALLBACK_FRONT_URL;
    }
    public static String getUnionPayCallbackBackUrl() {
        return WebConstants.getSchemaURL()+UNION_PAY_CALLBACK_BACK_URL;
    }

}
