package com.cheche365.cheche.externalpayment.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.core.util.CacheUtil
import org.springframework.data.redis.core.StringRedisTemplate

import static com.cheche365.cheche.botpy.util.BusinessUtils.notNeedUploadImage
import static com.cheche365.cheche.common.util.DateUtils.DATE_SHORTDATE_PATTERN
import static com.cheche365.cheche.common.util.DateUtils.getDate
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1

/**
 * Created by zhengwei on 14/03/2018.
 * 金斗云回调报文模型。只负责简单的报文解析，复杂逻辑不要写到这个类下。
 */
class BotpyCallBackBody extends BaseCallbackBody{

    //金斗云回调类型
    static final TYPE_PROPOSE = 'Propose'
    static final TYPE_INSURE = 'Underwriting'
    static final TYPE_INSURE_MANUALLY = 'UWManually'
    static final TYPE_MODIFY_PHONE = 'ModifyPhone'
    static final TYPE_VERIFY_IDCODE = 'VerifyIDCode'
    static final TYPE_STATUS_CHANGE = 'StatusChange'
    static final TYPE_SEND_IDCODE = 'SendIDCode'
    static final TYPE_PREPAY = 'Payment'
    static final TYPE_ICPHOTO = 'ICPhoto'
    static final TYPE_SYNC = 'Sync'
    static final TYPE_PAYMENT_STATUS= 'PaymentStatus'

    //金斗云订单状态
    static final STATUS_BEFORE_INSURE_FAIL = 'FAIL'   //提交投保单到保险公司系统失败
    static final STATUS_BEFORE_INSURE_INIT = 'INIT'   //提交投保单到保险公司系统成功
    static final STATUS_INSURE_PROCESSING = 'UW'  //核保中
    static final STATUS_INSURE_FAIL = 'UW_FAIL'  //核保失败
    static final STATUS_INSURE_SUCCESS = 'UW_SUCC'  //核保成功
    static final STATUS_ORDER_REFUSED = 'REFUSED'  //拒绝承保
    static final STATUS_WAIT_PAY = 'WAIT_PAY'  //等待支付
    static final STATUS_PAID = 'PAID'  //保费已支付

    //金斗云保险公司
    static final EPICC = 'epicc'


    static final ACTION_FORWARD = 'action_forward'
    static final ACTION_HANDLE_LOCALLY = 'action_handle_locally'

    static final Map BOTPY_CALLBACK_TYPES = [
        (TYPE_PROPOSE)        : [
            action: ACTION_FORWARD,
            desc  : '投保'
        ],
        (TYPE_MODIFY_PHONE)   : [
            action: ACTION_FORWARD,
            desc  : '修改手机号'
        ],
        (TYPE_VERIFY_IDCODE)  : [
            action: ACTION_HANDLE_LOCALLY,
            desc  : '验证身份证验证码结果'
        ],
        (TYPE_SEND_IDCODE)    : [
            action: ACTION_FORWARD,
            desc  : '发送身份证验证码结果'
        ],
        (TYPE_ICPHOTO)        : [
            action: ACTION_FORWARD,
            desc  : '同步投保单影像到保险公司'
        ],
        (TYPE_SYNC)           : [
            action: ACTION_FORWARD,
            desc  : '同步结果'
        ],


        (TYPE_STATUS_CHANGE)  : [
            action: ACTION_HANDLE_LOCALLY,
            desc  : '投保单状态变更'
        ],
        (TYPE_INSURE)         : [
            action: ACTION_HANDLE_LOCALLY,
            desc  : '核保'
        ],
        (TYPE_PREPAY)         : [
            action: ACTION_HANDLE_LOCALLY,
            desc  : '支付信息结果'
        ],
        (TYPE_INSURE_MANUALLY): [
            action: ACTION_HANDLE_LOCALLY,
            desc  : '人工核保'
        ],
        (TYPE_PAYMENT_STATUS): [
            action: ACTION_HANDLE_LOCALLY,
            desc  : '支付状态结果'
        ]
    ].asImmutable()

    //TODO legalCurrentStatus狀態包含toStatus，會造成重複回調的時候校驗通過，以至重複處理回調
    static final BOTPY_ORDER_STATUS = [

        (STATUS_BEFORE_INSURE_FAIL): [
            legalCurrentStatus: [PENDING_PAYMENT_1, INSURE_FAILURE_7],
            toStatus          : INSURE_FAILURE_7,
            isSync            : false,
            desc              : '提交投保单到保险公司系统失败'
        ],
        (STATUS_BEFORE_INSURE_INIT): [
            legalCurrentStatus: [PENDING_PAYMENT_1, INSURE_FAILURE_7],
            desc              : '提交投保单到保险公司系统成功'
        ],
        (STATUS_INSURE_FAIL)       : [
            legalCurrentStatus: [PENDING_PAYMENT_1, INSURE_FAILURE_7],
            toStatus          : INSURE_FAILURE_7,
            desc              : '核保失败'
        ],
        (STATUS_INSURE_SUCCESS)    : [
            legalCurrentStatus: [PENDING_PAYMENT_1, INSURE_FAILURE_7],
            desc              : '核保成功'
        ],
        (STATUS_INSURE_PROCESSING) : [
            legalCurrentStatus: [PENDING_PAYMENT_1, INSURE_FAILURE_7],
            toStatus          : INSURE_FAILURE_7,
            desc              : '核保中'
        ],
        (STATUS_ORDER_REFUSED)     : [  //忽略
                                        legalCurrentStatus: [PENDING_PAYMENT_1, PAID_3],
                                        desc              : '拒绝承保'
        ],
        (STATUS_WAIT_PAY)          : [
            legalCurrentStatus: [INSURE_FAILURE_7, PENDING_PAYMENT_1],
            toStatus          : PENDING_PAYMENT_1,
            statusDisplay     : null,
            desc              : '等待支付'
        ],
        (STATUS_PAID)              : [
            legalCurrentStatus: [PENDING_PAYMENT_1, PAID_3],
            toStatus          : FINISHED_5,
            paymentStatus     : PaymentStatus.Enum.PAYMENTSUCCESS_2,
            desc              : '保费已支付'
        ]
    ].asImmutable()

    static StringRedisTemplate redisTemplate
    static final String TIMEOUT_NOTIFICATIONS_REDIS_KEY = 'botpay:notifications:timeout'  //set结构
    static final String POLLING_NOTIFICATIONS_REDIS_KEY = 'botpay:notifications:polling' //hash结构
    static final String IMAGES_PROPOSAL_STATUS_REDIS_KEY = 'botpy:proposalStatus:images' //hash结构
    static final String PAYMENT_CHANNEL_CO_BANK = 'botpy:payment:bank'  //hash结构
    static final String WORKED_PAYMENT_STATUS_REDIS_SET_KEY = 'botpy:paymentStatus:worked:set' //set结构

    String raw
    Map parsed

    BotpyCallBackBody(String raw) {
        this.raw = raw
        this.parsed = CacheUtil.doJacksonDeserialize(raw, Map)

    }

    BotpyCallBackBody(Map parsed) {
        this.parsed = parsed
    }

    boolean success() {
        parsed.is_success as boolean
    }

    String type() {
        parsed.type
    }

    String notificationId() {
        parsed.notification_id
    }

    String proposalId() {
        parsed.proposal_id
    }

    Map data() {
        parsed?.data
    }

    /**
     * 是否有伪同步服务在等待回调
     * @return
     */
    boolean asyncWaiting() {
        !(getRedisTemplate().opsForSet().isMember(TIMEOUT_NOTIFICATIONS_REDIS_KEY, notificationId()))
    }

    void forward() {
        getRedisTemplate().convertAndSend("botpy-out", raw)
    }

    boolean manuallyInsure() {
        data().is_manually as boolean
    }

    String paymentQRCodeUrl() {
        data().qrcode_url
    }

    String trackingNo() {
        data().tracking_no
    }

    String paymentTradeNo() {
        data().payment_trade_no
    }

    String proposalStatus() {
        data().proposal_status
    }

    String icCode(){
        data().ic_code
    }

    Map billNos() {
        data()?.ic_nos
    }

    String ciProposalNo() {
        billNos()?.force_prop
    }

    String ciPolicyNo() {
        billNos()?.force_policy
    }

    String insuranceProposalNo() {
        billNos()?.biz_prop
    }

    String insurancePolicyNo() {
        billNos()?.biz_policy
    }

    String dataComment() {
        data().comment
    }

    String dataMessage() {
        data().message
    }

    boolean waitPayStatus() {
        success() && STATUS_WAIT_PAY == proposalStatus()
    }

    boolean paySuccessStatus() {
        success() && (data().paid as boolean)
    }

    //核保专用
    List auditRecords() {
        data()?.audit_records
    }

    List<BotpyBodyRecord> records() {
        data().records.collect {
            new BotpyBodyRecord(it)
        }
    }

    //验证身份证验证码结果专用
    Map insurance() {
        data()?.biz_info
    }

    //商业险种类
    List<BotpyBodyInsurance> insures() {
        data()?.detail.collect {
            new BotpyBodyInsurance(it)
        }
    }

    Map ci() {
        data()?.force_info
    }

    Double premium() {
        insurance().total as Double
    }

    Double discount() {
        (ci().discount ? ci().discount : insurance().discount) as Double
    }

    Date startDate() {
        ci().start_date ? parse(ci().start_date as String) : parse(insurance().start_date as String)
    }

    Date parse(String date) {
        Date.parse("yyyy-MM-dd", date)
    }

    Date endDate() {
        ci().end_date ? parse(ci().end_date as String) : parse(insurance().end_date as String)
    }

    Double ciPremium() {
        ci().premium as Double
    }

    Double ciAutoTax() {
        ci().tax as Double
    }

    Date insuranceStartDate(){
        getDate(parsed.biz_start_date as String, DATE_SHORTDATE_PATTERN)
    }

    Date insuranceEndDate(){
        getDate(parsed.biz_end_date as String, DATE_SHORTDATE_PATTERN)
    }

    Date ciStartDate(){
        getDate(parsed.force_start_date as String, DATE_SHORTDATE_PATTERN)
    }

    Date ciEndDate(){
        getDate(parsed.force_end_date as String, DATE_SHORTDATE_PATTERN)
    }


    String action() {
        BOTPY_CALLBACK_TYPES.get(type()).action
    }

    boolean actionForward() {
        ACTION_FORWARD == action()
    }

    boolean actionLocally() {
        ACTION_HANDLE_LOCALLY == action()
    }

    String typeDesc() {
        BOTPY_CALLBACK_TYPES.get(type()).desc
    }

   String statusDesc() {
       BOTPY_ORDER_STATUS.get(proposalStatus()).desc
   }

    static StringRedisTemplate getRedisTemplate() {
        if (!redisTemplate) {
            synchronized (BotpyCallBackBody.class) {
                redisTemplate = ApplicationContextHolder.getApplicationContext().getBean(StringRedisTemplate)
            }
        }
        return redisTemplate
    }

    String getImagesComment() {
        if (!(data().is_manually || notNeedUploadImage(data()))){
            ([dataComment()] + auditRecords()?.comment).findAll { it }.join('|')
        }
    }

    boolean payStatusCallbackProcessed(){
        getRedisTemplate().opsForSet().isMember(WORKED_PAYMENT_STATUS_REDIS_SET_KEY, proposalId())
    }

    void payStatusCallbackProcess(){
        getRedisTemplate().opsForSet().add(WORKED_PAYMENT_STATUS_REDIS_SET_KEY, proposalId())
    }
}
