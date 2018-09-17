package com.cheche365.cheche.alipay.constants;

import com.cheche365.cheche.core.util.ProfileProperties;

import java.io.IOException;
import java.util.Properties;

/**
 * 支付宝服务窗环境常量（demo中常量只是参考，需要修改成自己的常量值）
 *
 * @author taixu.zqq
 * @version $Id: AlipayServiceConstants.java, v 0.1 2014年7月24日 下午4:33:49 taixu.zqq Exp $
 */
public class AlipayServiceEnvConstants {

    /**
     * 签名编码-视支付宝服务窗要求
     */
    public static final String SIGN_CHARSET = "GBK";
    /**
     * 字符编码-传递给支付宝的数据编码
     */
    public static final String CHARSET = "GBK";
    /**
     * 授权访问令牌的授权类型
     */
    public static final String GRANT_TYPE = "authorization_code";
    /**
     * 支付宝网关
     */
    public static final String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do";

    public static String APP_ID;
    public static String PRIVATE_KEY;
    public static String PUBLIC_KEY;
    public static String ALIPAY_PUBLIC_KEY;
    public static String SIGN_TYPE;

    public static Boolean USER_HTTP_CLIENT = Boolean.FALSE;

    static {
        Properties properties = new Properties();
        try {
            properties.load(AlipayServiceEnvConstants.class.getResourceAsStream("/properties/alipay.properties"));
            ProfileProperties profileProperties = new ProfileProperties(properties);
            APP_ID = profileProperties.getProperty("cheche.alipay.app_id");
            PRIVATE_KEY = profileProperties.getProperty("cheche.private_key");
            PUBLIC_KEY = profileProperties.getProperty("cheche.public_key");
            ALIPAY_PUBLIC_KEY = profileProperties.getProperty("alipay.public_key");
            SIGN_TYPE = profileProperties.getProperty("alipay.sign.type");

            USER_HTTP_CLIENT = Boolean.valueOf(profileProperties.getProperty("alipay.api.usehttpclient"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
