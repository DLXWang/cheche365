package com.cheche365.cheche.scheduletask.constants;

/**
 * Created by sunhuazhong on 2015/5/5.
 */
public class TaskConstants {

    /**
     * 常量
     */
    // 短信发送结果：成功
    public static final int SMS_SEND_STATUS_SUCCESS = 0;
    // 微信二维码渠道：扫描
    public static final String WECHAT_QRCODE_SCAN = "scan";
    // 微信二维码渠道：关注
    public static final String WECHAT_QRCODE_SUBSCRIBE = "subscribe";
    // 商业险即将到期
    public static final int COMMERCIAL_INSURANCE_EXPIRE_DATE = 1;
    // 交强险即将到期
    public static final int COMPULSORY_INSURANCE_EXPIRE_DATE = 2;
    // 拍照报价Redis Key
    public static final String QUOTE_PHOTO_DATA_CACHE = "schedules.task.tel.quote.photo.previous.time";
    // 主动预约Redis Key
    public static final String APPOINTMENT_INSURANCE_DATA_CACHE = "schedules.task.tel.appointment.insurance.previous.time";
    // 未支付订单id Redis Key
    public static final String QUOTE_UNPAY_ORDER_ID_CACHE = "schedules.task.tel.unpay.order.id";
    // 注册但无行为用户：用户id Redis key
    public static final String REGISTER_NO_OPERATION_USER_ID_CACHE = "schedules.task.register.no.operation.user.id";
    // 注册但无行为用户：主动预约id Redis key
    public static final String REGISTER_NO_OPERATION_APPOINTMENT_INSURANCE_ID_CACHE = "schedules.task.register.no.operation.appointment.insurance.id";
    // 注册但无行为用户：拍照预约id Redis key
    public static final String REGISTER_NO_OPERATION_QUOTE_PHOTO_ID_CACHE = "schedules.task.register.no.operation.quote.photo.id";
    // 注册但无行为用户：订单id Redis key
    public static final String REGISTER_NO_OPERATION_PURCHASE_ORDER_ID_CACHE = "schedules.task.register.no.operation.purchase.order.id";
    // 注册但无行为用户：礼品id Redis key
    public static final String REGISTER_NO_OPERATION_GIFT_ID_CACHE = "schedules.task.register.no.operation.gift.id";
    // 注册但无行为用户：参与活动id Redis key
    public static final String REGISTER_NO_OPERATION_MARKETING_SUCCESS_ID_CACHE = "schedules.task.register.no.operation.marketing.success.id";
    // 注册但无行为用户：电销id Redis key
    public static final String REGISTER_NO_OPERATION_TEL_MARKETING_CENTER_ID_CACHE = "schedules.task.register.no.operation.tel.marketing.center.id";
    // 财务台帐查询时间 Redis key
    public static final String FINANCIAL_ACCOUNTING_PREVIOUS_TIME_CACHE = "schedules.task.financial.accounting.previous.time";
    // 上年未成单订单定时任务上次执行时间 Redis Key
    public static final String LASY_YEAR_UNPAY_ORDER_CACHE = "schedules.task.tel.last.year.unpay.previous.time";
    //交强险定时任务上次执行时间 Redis Key
    public static final String COMPULSORY_INSURANCE_CACHE = "schedules.task.tel.compulsory.insurance.previous.time";
    //商业险定时任务上次执行时间 Redis Key
    public static final String INSURANCE_CACHE = "schedules.task.tel.insurance.previous.time";
    //百度数据同步邮件定时任务上次执行时间 Redis Key
    public static final String BAIDU_DATA_SYNC_TIME = "schedules.task.baidu.data.sync.previous.time";
    //电销/非电销业绩安心订单定时任务上次执行时间Redis Key
    public static final String PERFORMANCE_ORDER = "schedules.task.performance.order.previous.time";
    //大特保数据同步邮件定时任务上次执行时间 Redis Key
    public static final String DATEBAO_DATA_SYNC_TIME = "schedules.task.datebao.data.sync.previous.time";
    //泛华成单对账报表定时任务上次执行时间
    public static final String FANHUA_ORDER_BILL_SYNC_TIME = "schedules.task.fanhua.order.bill.previous.time";
    //泛华成单礼品报表定时任务上次执行时间
    public static final String FANHUA_ORDER_GIFT_SYNC_TIME = "schedules.task.fanhua.order.gift.previous.time";

    public static final int PAGE_NUMBER = 0;
    public static final int PAGE_SIZE = 1000;

    // 申请退款订单id Redis Key
    public static final String REFUND_ORDER_ID_CACHE = "schedules.task.tel.refund.order.id";

    //已查询需退款的amendId
    public static final String OVERTIME_REFUND_AMEND_ID_CACHE = "schedules.task.overtime.tel.refund.amend.id";

    // 报价日志id Redis Key
    public static final String QUOTE_RECORD_LOG_CREATE_TIME_CACHE = "schedules.task.tel.quote.record.log.create.time";
    public static final String TOA_QUOTE_RECORD_LOG_CREATE_TIME_CACHE = "schedules.task.tel.toA.quote.record.log.create.time";
    // 凌云注册报告id redis key
    public static final String DEVELOPER_INFO_ID_CACHE = "schedules.task.developer.info.id";

    //手机号城市同步队列
    public static final String MOBILE_AREA_SYNC_QUEUE = "schedules.task.mobile.area.sync";

    //微车数据导出email 日志id Redis Key
    public static final String WEICHE_QUOTE_REPORT_APPLICATION_LOG_CREATE_TIME_CACHE = "weiche.quote.report.application.log.create.time";

    //从mysql导入到Mongodb，上次导入的数据ID
    public static final String IMPORT_TO_MONGO_APPLICATION_LOG_ID = "import:to:mongo:application.log.id";
    public static final String IMPORT_TO_MONGO_PLATFORM_ACCESS_LOG_ID = "import:to:mongo:platform.access.log.id";
    public static final String IMPORT_TO_MONGO_ACCESS_LOG_ID = "import:to:mongo:access.log.id";
    public static final String IMPORT_TO_PARSER_MUTUAL_MESSAGE_ID = "import:to:mongo:parser.mutual.message.id";

}
