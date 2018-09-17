package com.cheche365.cheche.manage.common.model
/**
 * Created by wangshaobin on 2016/8/25.
 */
class TelMarketingCenterTaskType {
    private Integer id
    private String code
    private String name

    TelMarketingCenterTaskType(Integer id, String code, String name) {
        this.id = id
        this.code = code
        this.name = name
    }

    void setId(Integer id) {
        this.id = id
    }

    void setCode(String code) {
        this.code = code
    }

    void setName(String name) {
        this.name = name
    }

    Integer getId() {
        return id
    }

    String getCode() {
        return code
    }

    String getName() {
        return name
    }

    static class Enum {
        public static List<TelMarketingCenterTaskType> ALL
        public static TelMarketingCenterTaskType INSURANCE
        public static TelMarketingCenterTaskType COMPULSORY_INSURANCE
        public static TelMarketingCenterTaskType REGISTER_NO_OPERATION
        public static TelMarketingCenterTaskType QUOTE_PHOTO
        public static TelMarketingCenterTaskType APPOINTMENT_INSURANCE
        public static TelMarketingCenterTaskType MARKETING_SUCCESS
        public static TelMarketingCenterTaskType LAST_YEAR_UNPAY_ORDER
        public static TelMarketingCenterTaskType UNPAY_ORDER
        public static TelMarketingCenterTaskType REFUND
        public static TelMarketingCenterTaskType QUOTELOG
        public static TelMarketingCenterTaskType LOGIN
        public static TelMarketingCenterTaskType TOAQUOTELOG
        public static TelMarketingCenterTaskType ORDER_PAYMENT_SMS_TASK
        public static TelMarketingCenterTaskType ORDER_PAY_REMIND_SEND_MESSAGE_TASK

        static {
            INSURANCE = new TelMarketingCenterTaskType(1, "insurance", "商业险即将到期")
            COMPULSORY_INSURANCE = new TelMarketingCenterTaskType(2, "compulsoryInsurance", "交强险即将到期")
            REGISTER_NO_OPERATION = new TelMarketingCenterTaskType(3, "registerNoOperation", "注册但无行为用户")
            QUOTE_PHOTO = new TelMarketingCenterTaskType(4, "quotePhoto", "拍照报价")
            APPOINTMENT_INSURANCE = new TelMarketingCenterTaskType(5, "appointmentInsurance", "主动预约")
            MARKETING_SUCCESS = new TelMarketingCenterTaskType(6, "marketingSuccess", "活动")
            LAST_YEAR_UNPAY_ORDER = new TelMarketingCenterTaskType(7, "lastYearUnpayOrder", "上年未成单订单")
            UNPAY_ORDER = new TelMarketingCenterTaskType(8, "unPayOrder", "未支付订单")
            REFUND = new TelMarketingCenterTaskType(9, "refund", "申请退款")
            QUOTELOG = new TelMarketingCenterTaskType(10, "quoteLog", "报价日志")
            LOGIN = new TelMarketingCenterTaskType(11, "login", "登录用户")
            TOAQUOTELOG = new TelMarketingCenterTaskType(12, "toAQuoteLog", "toA渠道报价日志")
            ORDER_PAYMENT_SMS_TASK = new TelMarketingCenterTaskType(13, "OrderPaymentSMSTask", "未支付订单的第二次短信推送")
            ORDER_PAY_REMIND_SEND_MESSAGE_TASK = new TelMarketingCenterTaskType(14, "OrderPaymentSMSTask", "30分钟未支付短信")

            ALL = [
                INSURANCE, COMPULSORY_INSURANCE, REGISTER_NO_OPERATION, QUOTE_PHOTO,
                APPOINTMENT_INSURANCE, MARKETING_SUCCESS, LAST_YEAR_UNPAY_ORDER, UNPAY_ORDER,
                REFUND, QUOTELOG, LOGIN, TOAQUOTELOG, ORDER_PAYMENT_SMS_TASK, ORDER_PAY_REMIND_SEND_MESSAGE_TASK
            ]
        }

        static TelMarketingCenterTaskType findById(Integer id) {
            ALL.find {
                it.id == id
            }
        }
    }
}
