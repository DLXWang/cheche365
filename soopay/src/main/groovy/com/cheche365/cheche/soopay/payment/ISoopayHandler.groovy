package com.cheche365.cheche.soopay.payment;

import java.text.SimpleDateFormat;

/**
 * Created by mjg on 2017/6/19.
 */
interface ISoopayHandler {
    public static final String SOOPAY_ENCODING = "UTF-8";// 字符集编码 默认"UTF-8"
    public static final String SOOPAY_VERSION = "4.0";// 版本号
    public static final String SOOPAY_MERCHANT_ID = "50024";//H5商户号
    public static final String SOOPAY_SIGN_METHOD = "RSA";// 签名方法  RSA
    public static final String SOOPAY_TXN_TYPE_01 = "pay_req_h5_frontpage";// 交易类型 消费支付
    public static final String SOOPAY_TXN_TYPE_03 = "mer_refund";// 交易类型 退款
    public static final String SOOPAY_TXN_TYPE_04 = "mer_cancel";// 交易类型 撤销
    public static final String SOOPAY_TXN_TYPE_05 = "transfer_direct_req";// 交易类型 提现
    public static final String SOOPAY_RES_FORMAT = "HTML";//响应数据格式
    public static final String SOOPAY_AMT_TYPE = "RMB";// 交易币种 RMB 人民币
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
}
