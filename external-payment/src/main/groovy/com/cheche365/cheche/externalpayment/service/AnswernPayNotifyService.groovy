package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.answern.service.AnswernService
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.service.DailyRestartInsuranceService
import com.cheche365.cheche.core.service.IThirdPartyDailyInsuranceService
import com.cheche365.cheche.core.service.IThirdPartySyncInterruptableService
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.ThirdServiceFailService
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler
import com.cheche365.cheche.core.util.NotifyMessageUtils
import com.cheche365.cheche.core.service.DailyInsuranceService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.DateUtils.getDate
import static com.cheche365.cheche.core.constants.AnswernConstant.PROPORTION
import static com.cheche365.cheche.core.model.DailyInsuranceStatus.Enum.RESTART_INSURED
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.ALIPAY_1
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.WECHAT_4
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTSUCCESS_2
import static com.cheche365.cheche.core.model.ScheduleCondition.Enum.ANSWERN_INSURE_SUCCESS
import static com.cheche365.cheche.core.service.sms.ConditionTriggerUtil.sendMessage
import static com.cheche365.cheche.core.service.sms.ConditionTriggerUtil.sendMsgNotAllowed
import static com.cheche365.cheche.core.service.sms.ConditionTriggerUtil.sendPaymentSuccessMessage
import static com.cheche365.cheche.core.util.CacheUtil.doJacksonSerialize
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.WECHAT_APP
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.WECHAT_PC
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.WECHAT_WAP
import static com.cheche365.cheche.externalpayment.constants.AnswernConstant.WECHAT_WEB
import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE
import static java.lang.Thread.sleep
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace

/**
 * Created by chenqc on 2016/12/1.
 */
@Service
@Slf4j
class AnswernPayNotifyService {

    @Autowired
    private PaymentRepository paymentRepository

    @Autowired
    private PurchaseOrderService orderService

    @Autowired
    private InsuranceRepository insuranceRepository

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    private AnswernService answernService

    @Autowired(required = false)
    @Qualifier("answernDailyInsuranceService")
    private IThirdPartyDailyInsuranceService answernDailyInsuranceService

    @Autowired(required = false)
    @Qualifier("answernDailyInsuranceSyncService")
    private IThirdPartySyncInterruptableService answernDailyInsuranceSyncService

    @Autowired
    private DailyInsuranceService dailyInsuranceService

    @Autowired
    private DailyRestartInsuranceService restartInsuranceService

    @Autowired
    private OrderOperationInfoService orderOperationInfoService

    @Autowired
    private RedisPublisher redisPublisher

    @Autowired
    private ThirdServiceFailService thirdServiceFailService

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler

    @Autowired
    private QuoteRecordRepository quoteRecordRepository

    @Autowired
    private StringRedisTemplate stringRedisTemplate

    @Transactional
    void saveOrderAndPayment(PurchaseOrder order, Payment payment, Map<String, String> formParams) {
        paymentRepository.save payment.with {
            it.status = PAYMENTSUCCESS_2
            it.channel = [WECHAT_WEB, WECHAT_PC, WECHAT_APP, WECHAT_WAP].contains(formParams.payType) ? WECHAT_4 : ALIPAY_1
            it.comments = it.channel.description
            it.thirdpartyPaymentNo = formParams.payNo
            it.updateTime = formParams.payDate ? getDate(formParams.payDate, 'yyyyMMddHHmmss') : new Date()
            it
        }

        if (!formParams.attach) {
            orderService.saveOrder order.with {
                it.status = PAID_3
                it.channel = payment.channel
                it
            }
        }
        stringRedisTemplate.opsForList().leftPush 'payment_call_back', doJacksonSerialize(payment)
    }

    void insuranceNotify(Map<String, String> formParams, PurchaseOrder order) {
        log.info '接收安心支付回调订单支付结果处理完成，开始调用承保服务，订单号：{}', order.orderNo
        def insurance = insuranceRepository.findByQuoteRecordId order.objId
        def compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId order.objId
        def quoteRecord = insurance?.quoteRecord ?: compulsoryInsurance?.quoteRecord

        log.debug '安心支付回调处理完成，发送短信，订单号：{}', order.orderNo
        sendPaymentSuccessMessage conditionTriggerHandler, quoteRecordRepository.findOne(order.objId), order

        if (doInsure(order, insurance, compulsoryInsurance, formParams, doOrderInsure, 3)) {
            saveInsuranceAndCompulsoryInsurance insurance, compulsoryInsurance

            log.debug '调用安心承保服务成功后同步更新出单中心、通知第三方,订单号：{}', order.orderNo
            orderOperationInfoService.updatePurchaseOrderStatusForServiceSuccess order
            if (!sendMsgNotAllowed(quoteRecord)) {
                sendMessage conditionTriggerHandler, [
                    mobile: order.applicant.mobile,
                    auto  : order.auto.id as String,
                    type  : ANSWERN_INSURE_SUCCESS.id as String
                ]
            }
        }
    }

    def restart(Payment payment) {
        def restartInsurance = restartInsuranceService.findByPayment payment
        restartInsurance.status = RESTART_INSURED
        restartInsurance.dailyInsurance.restartDate = restartInsurance.beginDate
        def order = restartInsurance.dailyInsurance.purchaseOrder
        restartInsuranceService.saveDailyRestartInsurance restartInsurance
        dailyInsuranceService.saveDailyInsurance restartInsurance.dailyInsurance
        dailyInsuranceService.unbindPurchaseOrder order

        log.debug '安心支付回调处理完成，发送短信，订单号：{}', order.orderNo
        sendPaymentSuccessMessage conditionTriggerHandler, quoteRecordRepository.findOne(order.objId), order

        def insurance = insuranceRepository.findByQuoteRecordId payment.purchaseOrder.objId
        try {
            answernDailyInsuranceSyncService.syncResume restartInsurance.dailyInsurance.purchaseOrder, insurance, restartInsurance, null
        } catch (e) {
            log.error '调用安心复驶同步服务失败，订单号：{}， exception：{}', payment.purchaseOrder.orderNo, getStackTrace(e)
            restartInsurance.isSync = 2
        }
        restartInsuranceService.saveDailyRestartInsurance restartInsurance
    }

    private boolean doInsure(order, insurance, obj, formParams, Closure<Boolean> insureClosure, Integer reTryTime) {
        def result
        reTryTime.times {
            try {
                result = insureClosure(order, insurance, obj, formParams)
                log.debug '第 {} 次调用安心承保接口返回信息为:{}', it + 1, result
            } catch (Exception e) {
                log.warn '第 {} 次调用安心承保接口失败!', it + 1
            }

            if (result) {
                directive = Closure.DONE
            } else {
                sleep 3000L
            }
        }
        result
    }

    Closure<Boolean> doOrderInsure = { PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, String> formParams ->
        try {
            answernService.order order, insurance, compulsoryInsurance, formParams
            TRUE
        } catch (Exception e) {
            log.error '调用安心承保服务失败，订单号：{} exception：{}', order.orderNo, getStackTrace(e)
            if(e instanceof BusinessException && BusinessException.Code.NOTIFICATION == e.code){
                redisPublisher.publish(NotifyMessageUtils.getNotifyMessage(e.message,String.valueOf(e.errorObject)))
            }
            thirdServiceFailService.saveThirdServiceFail insurance.insuranceCompany.id, order.id, getStackTrace(e)
            FALSE
        }
    }

    @Transactional
    void saveInsuranceAndCompulsoryInsurance(Insurance insurance, CompulsoryInsurance compulsoryInsurance) {
        if (insurance) {
            insuranceRepository.save insurance.with {
                it.proportion = PROPORTION
                it
            }
            log.debug 'save insurance success'
        }
        if (compulsoryInsurance) {
            compulsoryInsuranceRepository.save compulsoryInsurance
            log.debug 'save compulsoryInsurance success'
        }
    }
}
