package com.cheche365.cheche.unionpay.payment;

import java.text.SimpleDateFormat;

/**
 * Created by wangfei on 2015/7/8.
 */
public interface IUnionPayHandler {
    String UNION_PAY_ENCODING = "UTF-8";// 字符集编码 默认"UTF-8"
    String UNION_PAY_VERSION = "5.0.0";// 版本号
    String UNION_PAY_MERCHANT_ID_MOBILE = "898111475380111";//移动端商户号
    String UNION_PAY_MERCHANT_ID_PC = "898111475380110";//PC端商户号
    String UNION_PAY_MERCHANT_ID_PC_KQ = "898110263000062";//康桥PC端商户号
    String UNION_PAY_MERCHANT_ID_MOBILE_KQ = "898110263000069";//康桥移动端商户号
    String UNION_PAY_MERCHANT_ID_APPLEPAY = "898110263000064";//APPLE PAY康桥商户号
    String UNION_PAY_SIGN_METHOD = "01";// 签名方法 01 RSA
    String UNION_PAY_TXN_TYPE_01 = "01";// 交易类型 01-消费
    String UNION_PAY_TXN_TYPE_00 = "00";// 交易类型 00-查询
    String UNION_PAY_TXN_TYPE_04 = "04";// 交易类型 04-退货
    String UNION_PAY_TXN_TYPE_31 = "31";// 交易类型 31-撤销
    String UNION_PAY_TXN_SUBTYPE_01 = "01";// 交易子类型 00默认 01:自助消费 02:订购 03:分期付款
    String UNION_PAY_TXN_SUBTYPE_00 = "00";// 交易子类型 00默认 01:自助消费 02:订购 03:分期付款
    String UNION_PAY_BIZ_TYPE_000201 = "000201";//产品类型 b2c网关支付
    String UNION_PAY_BIZ_TYPE_000000 = "000000";//产品类型 默认值 查询用
    String UNION_PAY_ACCESS_TYPE = "0";// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
    String UNION_PAY_CURRENCY_CODE = "156";// 交易币种 156 人民币
    String UNION_PAY_CHANNEL_TYPE_08 = "08";//渠道类型，07-WEB，08-手机
   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
}
