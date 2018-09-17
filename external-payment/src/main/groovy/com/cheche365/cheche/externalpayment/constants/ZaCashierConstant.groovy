package com.cheche365.cheche.externalpayment.constants

import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.util.ProfileProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 众安承保结果可能包括以下状态：
 *   3已投保、待支付
     4已支付待出单
     5已支付出单失败
     6已支付已出单
     8已承保已生效
     11终止
     0注销
     98预收退费中
     99其他
   其中6、8、11映射为车车出单完成状态
   5、99映射为车车出单失败，对于出单失败的订单，只添加sub_status, status字段保持不变

 */
public class ZaCashierConstant {
    private static Logger logger = LoggerFactory.getLogger(ZaCashierConstant.class);

    public static final String MERCHANT; // 商户id 需要去众安开放平台申请
    public static final String APP_KEY ;// 商户对应的私钥
    public static final String CHARSET = "UTF-8";// 提交 接受 编码
    public static final String CASHIER_URL ; //众安收银台网关地址
    public static final String NOTIFY_URL ; //异步通知地址
    public static final String FRONT_URL ; //同步通知地址

    static final String ZA_ORDER_STATUS_PAID = '4' //已支付待出单
    static final String ZA_ORDER_STATUS_FAILED = '5' //已支付出单失败
    static final String ZA_ORDER_STATUS_FINISHED = '6' //已支付已出单
    static final String ZA_ORDER_STATUS_EXECUTED = '8' //已承保已生效
    static final String ZA_ORDER_STATUS_TERMINATION = '11' //终止
    static final String ZA_ORDER_STATUS_OTHER = '99' //其他
    static final String ZA_ORDER_STATUS_PAYMENT = '3' //已投保、待支付
    static final String ZA_ORDER_STATUS_CANCELED = '0' //注销
    static final String ZA_ORDER_STATUS_REFUNDING = '98' //预收退费中

    static int APPLY_SIGN_FOR_INSURED = '1'   // 核保后签名
    static int  APPLY_IMAGE_FOR_INSURED = '1' // 核保后上传

    static {
        ProfileProperties prop = getProperties("/zapay.properties");
        MERCHANT = prop.getProperty("za_pay_merchant");
        APP_KEY = prop.getProperty("za_pay_app_key");
        CASHIER_URL = prop.getProperty("za_pay_gateway_url");
        NOTIFY_URL = prop.getProperty("za_notify_url");
        FRONT_URL = prop.getProperty("za_front_url");
    }

    static ProfileProperties getProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(ZaCashierConstant.class.getResourceAsStream(path));
        } catch (IOException ex) {
            logger.error("load ceb pay properties files has error", ex);
        }
        return new ProfileProperties(properties);
    }


    static ZA_FAILED_STATUS =[ZA_ORDER_STATUS_FAILED, ZA_ORDER_STATUS_OTHER]
    static ZA_SUCCESS_STATUS =[ZA_ORDER_STATUS_FINISHED]


    static ZA_TO_CHECHE_ORDER_STATUS =[
        (ZA_ORDER_STATUS_CANCELED) : OrderStatus.Enum.CANCELED_6,
        (ZA_ORDER_STATUS_REFUNDING) : OrderStatus.Enum.REFUNDING_10
    ]

    static ORDER_CALLBACK_IGNORE_STATUS = [  //承保回调可忽略状态
         ZA_ORDER_STATUS_PAID,ZA_ORDER_STATUS_PAYMENT,ZA_ORDER_STATUS_EXECUTED,ZA_ORDER_STATUS_TERMINATION
    ]

    static ORDER_STATUS_MAPPING=[
        (OrderStatus.Enum.PAID_3.id as String):OrderTransmissionStatus.Enum.CONFIRM_TO_ORDER,
        (OrderStatus.Enum.CANCELED_6.id as String):OrderTransmissionStatus.Enum.CANCELED,
        (OrderStatus.Enum.REFUNDING_10.id as String):OrderTransmissionStatus.Enum.APPLY_FOR_REFUND,
        (OrderStatus.Enum.REFUNDED_9.id as String):OrderTransmissionStatus.Enum.REFUNDED,
    ]

}
