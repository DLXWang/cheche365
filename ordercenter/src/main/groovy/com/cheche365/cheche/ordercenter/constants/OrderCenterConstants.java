package com.cheche365.cheche.ordercenter.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by sunhuazhong on 2015/4/28.
 */
public class OrderCenterConstants {

    private static Logger logger = LoggerFactory.getLogger(OrderCenterConstants.class);
    /**
     * 异常
     */
    // 没有对应的消息发送器
    public static final String EXCEPTION_NO_MESSAGE_SEND = "201";
    public static final String EXCEPTION_NO_MESSAGE_SEND_MESSAGE = "没有对应的消息发送器.";

    // 初始化Velocity错误
    public static final String EXCEPTION_INIT_VELOCITY = "202";
    public static final String EXCEPTION_INIT_VELOCITY_MESSAGE = "初始化Velocity错误.";

    // 解析模板错误
    public static final String EXCEPTION_PARSE_TEMPLATE = "203";
    public static final String EXCEPTION_PARSE_TEMPLATE_MESSAGE = "解析模板错误.";

    // 订单状态转换失败
    public static final String EXCEPTION_RESET_ORDER_STATUS = "302";
    public static final String EXCEPTION_RESET_ORDER_STATUS_MESSAGE = "订单状态转换失败.";


    /**
     * 常量
     */
    // 邮件操作状态：001-未付款新订单
    public static final String OPERATE_STATUS_EMAIL_NEW_ORDER_WITHOUT_PAYMENT = "001";

    // 邮件操作状态：002-已付款新订单
    public static final String OPERATE_STATUS_EMAIL_NEW_ORDER_WITH_PAYMENT = "002";

    // 邮件操作状态：003-出单提醒
    public static final String OPERATE_STATUS_EMAIL_INSURE_REMIND = "003";

    // 邮件操作状态：004-核保完成提醒
    public static final String OPERATE_STATUS_EMAIL_UNDERWRITING_COMPLETE_REMIND = "004";

    // 邮件操作状态：005-录入保单
    public static final String OPERATE_STATUS_EMAIL_INPUT_INSURANCE = "005";

    // 邮件操作状态：006-订单取消
    public static final String OPERATE_STATUS_EMAIL_ORDER_CANCEL = "006";

    // 邮件操作状态：007-派送完成
    public static final String OPERATE_STATUS_EMAIL_DELIVERY_COMPLETE = "007";

    // 邮件操作状态：008-订单打回
    public static final String OPERATE_STATUS_EMAIL_RETURN = "008";

    // 邮件操作状态：010-订单完成
    public static final String OPERATE_STATUS_EMAIL_ORDER_COMPLETE = "010";

    // 邮件操作状态：011-线上收款
    public static final String OPERATE_STATUS_EMAIL_PAYMENT = "011";

    // 短信操作状态：101-线下收款
    public static final String OPERATE_STATUS_SMS_PAYMENT = "101";

    // 发送类型：邮件
    public static final String SEND_TYPE_EMAIL = "1";

    // 发送类型：短信
    public static final String SEND_TYPE_SMS = "2";

    //已付款文本
    public final static String PAID_TEXT = "已付款";

    //未付款文本
    public final static String NO_PAID_TEXT = "未付款";

    // 保单：生效小时
    public static final Integer INSURANCE_EFFECTIVE_HOUR = 0;

    // 保单：失效小时
    public static final Integer INSURANCE_EXPIRE_HOUR = 24;

    // 默认邮箱后缀格式
    public static final String DEFAULT_EMAIL_SUFFIX = "@139.com";

    // 消息发送结果：不发送
    public static final String MESSAGE_SEND_RESULT_NO_SEND = "1";

    public static final String ROLE_PREFIX = "ROLE_";

    // 用户取消发送邮件
    public static final String USER_CANCEL_EMAIL;

    // 线上收款发送邮件
    public static final String ONLINE_PAYMENT_EMAIL;

    public static final String OP_REQUEST_URL;

    public static final String CLINK_ON_LINE_URL;
    public static final String CLINK_OFF_LINE_URL;
    public static final String CLINK_CALL_URL;
    public static final String CLINK_USER;
    public static final String CLINK_PWD;
    public static final String CLINK_ENTERPRISE_ID;
    static {
        Properties properties = new Properties();
        try {
            properties.load(OrderCenterConstants.class.getResourceAsStream("/META-INF/spring/orderCenter.properties"));
        } catch (Exception ex) {
            logger.error("load orderCenter properties file error.");
        }
        String profile = System.getProperty("spring.profiles.active");
        USER_CANCEL_EMAIL = getProperty(properties, profile, "user.cancel.email");
        ONLINE_PAYMENT_EMAIL = getProperty(properties, profile, "online.payment.email");
        OP_REQUEST_URL = getProperty(properties, profile, "op.request.url");

        CLINK_ON_LINE_URL= OrderCenterConstants.getProperty(properties, profile,"clink.online.url");
        CLINK_OFF_LINE_URL= OrderCenterConstants.getProperty(properties, profile,"clink.offline.url");
        CLINK_CALL_URL= OrderCenterConstants.getProperty(properties, profile,"clink.call.url");
        CLINK_USER= OrderCenterConstants.getProperty(properties, profile,"clink.user");
        CLINK_PWD= OrderCenterConstants.getProperty(properties, profile,"clink.pwd");
        CLINK_ENTERPRISE_ID= OrderCenterConstants.getProperty(properties, profile,"clink.enterpriseId");
    }

    /**
     *
     * @param properties
     * @param profile
     * @param key
     * @return
     */
    public static String getProperty(Properties properties, String profile, String key){
        String profileKey = profile + "." + key;
        if (properties.containsKey(profileKey)){
            return properties.getProperty(profileKey);
        }else{
            return properties.getProperty(key);
        }
    }
}
