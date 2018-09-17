package com.cheche365.cheche.core.constants

/**
 * 模型常量
 */
class ModelConstants {

    static final _INSURANCE_KINDCODE_COMMERIAL      = 1 //商业险险别编号
    static final _INSURANCE_KINDCODE_COMPULSORY     = 2 //交强险

    static final _CHARTERED_CITIES_OF_CHINA         = ['京', '津', '沪', '渝']  // 中国直辖市

    public static final _FLOW_TYPE_GENERAL         = 1 // 通用流程
    public static final _FLOW_TYPE_RENEWAL_CHANNEL = 2 // 续保通道流程

    public static final _RENEWAL_AUTO_NO_TRANSFERED = 1 //续保车辆未过户
    public static final _RENEWAL_AUTO_TRANSFERED    = 2 //续保车辆已过户

    /**
     * 支付链接格式
     */
    public static final _PAYMENT_URL_FORMAT_IMAGE_BASE64 = 1 //图片base64

    /**
     * 支付渠道类型
     */
    public static final _PAYMENT_CHANNEL_LIST = 0
    public static final _PAYMENY_CHANNEL_ONE = 1

    /**
     * 支付状态类型
     */
    public static final _PAYMENT_STATUS_SUCCESS = 0
    public static final _PAYMENT_STATUS_PROCESSING = 1
    public static final _PAYMENT_STATUS_FAIL = 2

    /**
     * 报价单状态
     */
    public static final _QUOTE_RECORD_STATE_ALLOWED_INSURE = 0 // 核保成功
    public static final _QUOTE_RECORD_STATE_WAITING = 1 // 待核保
    public static final _QUOTE_RECORD_STATE_NEED_ALTER = 2 // 需要修改
    public static final _QUOTE_RECORD_STATE_NOT_ALLOWED_INSURE = 3 // 拒绝核保
    public static final _QUOTE_RECORD_STATE_FAIL = 4 // 失败


}
