package com.cheche365.cheche.externalpayment.constants;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.util.ProfileProperties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by chenqc on 2016/11/21.
 */
public class AnswernConstant {

    private static Logger logger = LoggerFactory.getLogger(AnswernConstant.class);

    //支付参数
    public static final String REQUEST_CODE = "requestCode";
    public static final String ORDER_NO = "orderNo";
    public static final String PAY_TYPE_USUAL = "payTypeUsual";
    public static final String PAY_AMT = "payAmt";
    public static final String CHECK_VALUE = "checkValue";

    //安心支付参数
    public static final String AX_REQUEST_CODE;
    public static final String AX_BG_RET_URL;
    public static final String AX_CODE;

    //安心支付接口
    public static final String AX_PAY_URL;

    public static final String WECHAT_WEB = "1";    // 微信-移动端(微信公众号支付）
    public static final String ALIPAY_WEB = "2";    // 支付宝-移动端
    public static final String WECHAT_PC = "3";     // 微信扫码-PC端
    public static final String ALIPAY_PC = "4";     // 支付宝-PC端
    public static final String WECHAT_APP = "5";    // 微信-APP(待开发)
    public static final String ALIPAY_APP = "6";    // 支付宝-APP(待开发)
    public static final String WECHAT_WAP = "7";    // 微信-wap支付

    static {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        Properties properties = new EncryptableProperties(encryptor);
        try {
            properties.load(WechatConstant.class.getResourceAsStream("/answernpay.properties"));
        } catch (IOException e) {
            logger.error("load wechat configuration file failed,", e);
        }
        ProfileProperties profileProperties = new ProfileProperties(properties);

        AX_CODE = profileProperties.getProperty("ax.code");
        AX_PAY_URL = profileProperties.getProperty("ax.payURL");
        AX_BG_RET_URL = "http://" + WebConstants.getDomain() + profileProperties.getProperty("ax.bgRetUrl");
        AX_REQUEST_CODE = profileProperties.getProperty("ax.requestCode");
    }
}
