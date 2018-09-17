package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationContext

import javax.persistence.*

import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_HUIBAO_75
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_JD
import static com.cheche365.cheche.core.model.Channel.Enum.PARTNER_NCI_25

@Slf4j
@Entity
@Canonical
class ScheduleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(100)")
    String name

    @Column(columnDefinition = "VARCHAR(45)")
    String description

    static class Enum {

        //官网、M站、APP通用
        public static ScheduleCondition REQUEST_VERIFY_CODE// 短信6位请求验证码
        public static ScheduleCondition ORDER_COMMIT// 提交订单
        public static ScheduleCondition PAYMENT_SUCCESS// 支付成功
        public static ScheduleCondition ORDER_CANCEL// 订单取消
        public static ScheduleCondition NO_PAYMENT_REMIND// 未支付订单短信提醒
        public static ScheduleCondition NOTIFY_CUSTOMER_PAYMENT// 通知客户上门收款
        public static ScheduleCondition NOTIFY_CUSTOMER_DELIVERY// 通知客户派送保单

        //世纪通保不使用共用的模板
        public static ScheduleCondition NCI_REQUEST_VERIFY_CODE
        public static ScheduleCondition NCI_ORDER_COMMIT
        public static ScheduleCondition NCI_PAYMENT_SUCCESS
        public static ScheduleCondition NCI_ORDER_CANCEL
        public static ScheduleCondition NCI_NO_PAYMENT_REMIND

        // 京东不使用公共模板
        public static ScheduleCondition JD_REQUEST_VERIFY_CODE
        public static ScheduleCondition JD_ORDER_COMMIT
        public static ScheduleCondition JD_PAYMENT_SUCCESS
        public static ScheduleCondition JD_ORDER_CANCEL
        public static ScheduleCondition JD_NO_PAYMENT_REMIND
        public static ScheduleCondition JD_AMEND_QUOTE_ORDER
        public static ScheduleCondition JD_RECOMMENDED_ORDER_IMAGE_UPLOAD
        public static ScheduleCondition JD_ORDER_COMMIT_NOT_ALLOW_PAY //京东正常的提交订单短信模板有支付链接，所以如果京东模糊报价另配一个模板不包含支付链接

        //第三方线上通用
        public static ScheduleCondition PARTNER_ORDER_COMMIT
        public static ScheduleCondition PARTNER_PAYMENT_SUCCESS
        public static ScheduleCondition PARTNER_ORDER_CANCEL
        public static ScheduleCondition PARTNER_NO_PAYMENT_REMIND
        public static ScheduleCondition PARTNER_REQUEST_VERIFY_CODE

        //惠保模板
        public static ScheduleCondition HUIBAO_ORDER_COMMIT
        public static ScheduleCondition HUIBAO_PAYMENT_SUCCESS
        public static ScheduleCondition HUIBAO_ORDER_CANCEL
        public static ScheduleCondition HUIBAO_NO_PAYMENT_REMIND
        public static ScheduleCondition HUIBAO_REQUEST_VERIFY_CODE
        //人工报价报价详情-惠保
        public static ScheduleCondition CUSTOMER_QUOTE_DETAIL_HUIBAO

        //人工报价报价详情-普通
        public static ScheduleCondition CUSTOMER_QUOTE_DETAIL_NORMAL
        //人工报价报价详情-支付宝
        public static ScheduleCondition CUSTOMER_QUOTE_DETAIL_ALIPAY
        //人工报价报价详情-第三方合作
        public static ScheduleCondition CUSTOMER_QUOTE_DETAIL_THIRDPARTNER
        //人工报价提交订单-普通线上支付
        public static ScheduleCondition CUSTOMER_QUOTE_ORDER_NORMAL_ONLINE
        //人工报价提交订单-普通线下支付
        public static ScheduleCondition CUSTOMER_QUOTE_ORDER_NORMAL_OFFLINE
        //人工报价提交订单-支付宝
        public static ScheduleCondition CUSTOMER_QUOTE_ORDER_ALIPAY
        //人工报价提交订单-第三方合作
        public static ScheduleCondition CUSTOMER_QUOTE_ORDER_THIRDPARTNER

        //人工提交订单取消-第三方
        public static ScheduleCondition CUSTOMER_QUOTE_PARTNER_ORDER_CANCEL
        //人工提交订单支付成功-第三方
        public static ScheduleCondition CUSTOMER_QUOTE_PARTNER_PAYMENT_SUCCESS
        //人工提交订单30分钟未支付-第三方
        public static ScheduleCondition CUSTOMER_QUOTE_PARTNER_NO_PAYMENT_REMIND

        // 会员生日祝福短信
        public static ScheduleCondition MEMBER_BIRTHDAY_WISHES
        //用户订单照片上传提醒
        public static ScheduleCondition RECOMMENDED_ORDER_IMAGE_UPLOAD
        //增补——追加付款短信
        public static ScheduleCondition AMEND_QUOTE_ORDER
        //核保失败订单详情短信
        public static ScheduleCondition INSURE_FAILURE_ORDER_DETAIL
        //停驶开始日期前12小时短信提醒
        public static ScheduleCondition STOP_BEGIN_TWELVE_HOUR
        //复驶开始日期前12小时短信提醒
        public static ScheduleCondition RESTART_BEGIN_TWELVE_HOUR
        //安心承保成功短信提醒
        public static ScheduleCondition ANSWERN_INSURE_SUCCESS
        //安心停驶返钱账单短信发送
        public static ScheduleCondition ANSWERN_SUSPEND_BILL
        //续保短信提醒
        public static ScheduleCondition RENEWAL_ORDER_REMIND
        // 未支付订单短信提醒第二次
        public static ScheduleCondition NO_PAYMENT_REMIND_TWICE
        // 长时间未更新点位提醒
        public static ScheduleCondition REBATE_NO_CHANGE

        // 发送代理人注册成功短信
        public static ScheduleCondition CHANNEL_AGENT_REGISTER


        //个性化短信模版
        public static Map VERIFY_CODE_MAPPING
        public static Map COMMIT_ORDER_MAPPING
        public static Map CANCEL_ORDER_MAPPING
        public static Map PAYMENT_SUCCESS_MAPPING
        public static Map PAYMENT_REMIND_MAPPING
        public static Map AMEND_MAPPING
        public static Map UPLOAD_ORDER_IMAGE_MAPPING

        public static Map SELF_DEFAULT_MAPPING
        public static Map PARTNER_DEFAULT_MAPPING
        public static Map OC_DEFAULT_MAPPING
        public static Map OC_PARTNER_JD_MAPPING

        static enum ConditionType {

            VERIFY_CODE(0), COMMIT_ORDER(1), CANCEL_ORDER(2), PAYMENT_SUCCESS(3), PAYMENT_REMIND(4), AMEND(5), UPLOAD_ORDER_IMAGE(6)
            private Integer index

            ConditionType(Integer index) {
                this.index = index
            }

            Integer getIndex() {
                return index
            }
        }

        static {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
            if (applicationContext != null) {
                def scheduleConditionRepository = applicationContext.getBean('scheduleConditionRepository')
                scheduleConditionRepository.findAll().forEach { scheduleCondition ->
                    try {
                        Enum.class.getDeclaredField(scheduleCondition.getName()).set(Enum.class.newInstance(), scheduleCondition)
                    } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                        log.error("schedule condition data init error ,field name is ->{}", scheduleCondition.getDescription())
                    }
                }
            } else {
                throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "Schedule Condition 初始化失败")
            }

            VERIFY_CODE_MAPPING = [
                (PARTNER_HUIBAO_75): HUIBAO_REQUEST_VERIFY_CODE,
                (PARTNER_NCI_25)   : NCI_REQUEST_VERIFY_CODE,
                (PARTNER_JD)       : JD_REQUEST_VERIFY_CODE
            ]
            COMMIT_ORDER_MAPPING = [
                (PARTNER_HUIBAO_75): HUIBAO_ORDER_COMMIT,
                (PARTNER_NCI_25)   : NCI_ORDER_COMMIT,
                (PARTNER_JD)       : JD_ORDER_COMMIT
            ]
            CANCEL_ORDER_MAPPING = [
                (PARTNER_HUIBAO_75): HUIBAO_ORDER_CANCEL,
                (PARTNER_NCI_25)   : NCI_ORDER_CANCEL,
                (PARTNER_JD)       : JD_ORDER_CANCEL
            ]
            PAYMENT_SUCCESS_MAPPING = [
                (PARTNER_HUIBAO_75): HUIBAO_PAYMENT_SUCCESS,
                (PARTNER_NCI_25)   : NCI_PAYMENT_SUCCESS,
                (PARTNER_JD)       : JD_PAYMENT_SUCCESS
            ]
            PAYMENT_REMIND_MAPPING = [
                (PARTNER_HUIBAO_75): HUIBAO_NO_PAYMENT_REMIND,
                (PARTNER_NCI_25)   : NCI_NO_PAYMENT_REMIND,
                (PARTNER_JD)       : JD_NO_PAYMENT_REMIND
            ]
            AMEND_MAPPING = [
                (PARTNER_JD) : JD_AMEND_QUOTE_ORDER
            ]
            UPLOAD_ORDER_IMAGE_MAPPING = [
                (PARTNER_JD):JD_RECOMMENDED_ORDER_IMAGE_UPLOAD
            ]

            SELF_DEFAULT_MAPPING = [
                (ConditionType.VERIFY_CODE.getIndex())       : REQUEST_VERIFY_CODE,
                (ConditionType.COMMIT_ORDER.getIndex())      : ORDER_COMMIT,
                (ConditionType.CANCEL_ORDER.getIndex())      : ORDER_CANCEL,
                (ConditionType.PAYMENT_SUCCESS.getIndex())   : PAYMENT_SUCCESS,
                (ConditionType.PAYMENT_REMIND.getIndex())    : NO_PAYMENT_REMIND,
                (ConditionType.AMEND.getIndex())             : AMEND_QUOTE_ORDER,
                (ConditionType.UPLOAD_ORDER_IMAGE.getIndex()): RECOMMENDED_ORDER_IMAGE_UPLOAD
            ]
            PARTNER_DEFAULT_MAPPING = [
                (ConditionType.VERIFY_CODE.getIndex())       : PARTNER_REQUEST_VERIFY_CODE,
                (ConditionType.COMMIT_ORDER.getIndex())      : PARTNER_ORDER_COMMIT,
                (ConditionType.CANCEL_ORDER.getIndex())      : PARTNER_ORDER_CANCEL,
                (ConditionType.PAYMENT_SUCCESS.getIndex())   : PARTNER_PAYMENT_SUCCESS,
                (ConditionType.PAYMENT_REMIND.getIndex())    : PARTNER_NO_PAYMENT_REMIND,
                (ConditionType.AMEND.getIndex())             : AMEND_QUOTE_ORDER,
                (ConditionType.UPLOAD_ORDER_IMAGE.getIndex()): RECOMMENDED_ORDER_IMAGE_UPLOAD
            ]
            OC_DEFAULT_MAPPING = [
                (ConditionType.CANCEL_ORDER.getIndex())   : CUSTOMER_QUOTE_PARTNER_ORDER_CANCEL,
                (ConditionType.PAYMENT_SUCCESS.getIndex()): CUSTOMER_QUOTE_PARTNER_PAYMENT_SUCCESS,
                (ConditionType.PAYMENT_REMIND.getIndex()) : CUSTOMER_QUOTE_PARTNER_NO_PAYMENT_REMIND,
            ]
            OC_PARTNER_JD_MAPPING = [
                (ConditionType.VERIFY_CODE.getIndex())       : JD_REQUEST_VERIFY_CODE,
                (ConditionType.COMMIT_ORDER.getIndex())      : JD_ORDER_COMMIT,
                (ConditionType.CANCEL_ORDER.getIndex())      : JD_ORDER_CANCEL,
                (ConditionType.PAYMENT_SUCCESS.getIndex())   : JD_PAYMENT_SUCCESS,
                (ConditionType.PAYMENT_REMIND.getIndex())    : JD_NO_PAYMENT_REMIND,
                (ConditionType.AMEND.getIndex())             : JD_AMEND_QUOTE_ORDER,
                (ConditionType.UPLOAD_ORDER_IMAGE.getIndex()): JD_RECOMMENDED_ORDER_IMAGE_UPLOAD
            ]
        }

        static final DEFAULT_MAPPING = { Channel channel ->
            (channel.isThirdPartnerChannel() ? PARTNER_DEFAULT_MAPPING : SELF_DEFAULT_MAPPING) +
            (channel.isOrderCenterChannel() ? OC_DEFAULT_MAPPING : [:]) +
            (PARTNER_JD == channel.parent ? OC_PARTNER_JD_MAPPING : [:])
        }

        static ScheduleCondition getVerifyCode(Channel channel) {
            VERIFY_CODE_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.VERIFY_CODE.getIndex()))
        }

        static ScheduleCondition getCommitOrder(Channel channel) {
            COMMIT_ORDER_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.COMMIT_ORDER.getIndex()))
        }

        static ScheduleCondition getCancelOrder(Channel channel) {
            CANCEL_ORDER_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.CANCEL_ORDER.getIndex()))
        }

        static ScheduleCondition getPaymentSuccess(Channel channel) {
            PAYMENT_SUCCESS_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.PAYMENT_SUCCESS.getIndex()))
        }

        static ScheduleCondition getPaymentRemind(Channel channel) {
            PAYMENT_REMIND_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.PAYMENT_REMIND.getIndex()))
        }

        static ScheduleCondition getAmend(Channel channel) {
            AMEND_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.AMEND.getIndex()))
        }

        static ScheduleCondition getUploadOrderImage(Channel channel) {
            UPLOAD_ORDER_IMAGE_MAPPING.get(channel, DEFAULT_MAPPING(channel).get(ConditionType.UPLOAD_ORDER_IMAGE.getIndex()))
        }

    }
}
