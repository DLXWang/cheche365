package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.DateUtils.getCurrentDateString
import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.ALIPAY_1
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.SOO_PAY_17
import static com.cheche365.cheche.core.model.PaymentType.Enum.PAYMENT_TYPE_SEGMENT_MAP
import static com.cheche365.cheche.core.model.PaymentType.Segment.DR
import static com.cheche365.cheche.core.model.PaymentType.Segment.TK
import static com.cheche365.cheche.core.model.PaymentType.Segment.ZB

@Slf4j
@Service
class PaymentSerialNumberGenerator {

    @Autowired
    private RedisTemplate redisTemplate

    private static String OUT_TRADE_NO_KEY = "outTradeNos:" + RuntimeUtil.getEvnProfile()

    /**
     * 根据流水号反查订单号
     */
    static String getPurchaseNo(String orderId) {
        String purchaseNo
        if (orderId.contains(ZB)) {
            purchaseNo = orderId.split(ZB)[0]
        } else if (orderId.contains(DR)) {
            purchaseNo = orderId.split(DR)[0]
        } else {
            if (RuntimeUtil.isProductionEnv()) {//生产环境
                purchaseNo = orderId.split(TK)[0]
            } else {
                String[] outTradeAry = orderId.split(TK)
                purchaseNo = "T" + outTradeAry[1]
            }
        }
        return purchaseNo
    }

    /**
     * 通过订单和支付渠道生成流水号
     */
    static String next(Payment payment) {
        if (payment.outTradeNo) {
            return payment.outTradeNo
        }

        String segment = getSegmentByPaymentType(payment)
        String orderNo = payment.purchaseOrder.orderNo
        String outTradeNo
        if ((SOO_PAY_17 == payment.channel) && (TK == segment)) {
            outTradeNo = soopayRefundNum()
        } else {
            String paymentId = String.valueOf(payment.id)
            String tailNum = paymentId.length() >= 3 ? paymentId.substring(paymentId.length() - 3) : paymentId
            Boolean aliPayRefund = (payment.channel == ALIPAY_1 && TK == segment)
            outTradeNo = new StringBuilder()
                .append(aliPayRefund ? getCurrentDateString("yyyyMMdd") : "")
                .append(orderNo)
                .append(segment)
                .append((StringUtils.leftPad(tailNum, 3, '0')))
                .toString()
        }
        payment.setOutTradeNo(outTradeNo)
        return payment.outTradeNo
    }

    private static String getSegmentByPaymentType(Payment payment) {
        String segment = PAYMENT_TYPE_SEGMENT_MAP.get(payment.paymentType)
        if (!segment) {
            throw new BusinessException(OPERATION_NOT_ALLOWED,
                "订单号[${payment.purchaseOrder.orderNo}]类型[${payment.paymentType.description}]生成支付流水号失败")
        }
        segment
    }

    private static String soopayRefundNum() {
        return getCurrentDateString("yyMMddhhmmss") + String.format('%1$04d', new Random().nextInt(9999))
    }

    String setOutTradeNo(Payment payment) {
        String orderNo = payment.purchaseOrder.orderNo
        String segment = getSegmentByPaymentType(payment)
        String tailNum = CacheUtil.getSetSize(redisTemplate, orderNo) + 1

        Boolean aliPayRefund = (payment.channel == ALIPAY_1 && TK == segment)
        String outTradeNo = new StringBuilder()
            .append(aliPayRefund ? getCurrentDateString("yyyyMMdd") : "")
            .append(orderNo)
            .append(segment)
            .append((StringUtils.leftPad(tailNum, 3, '0')))
            .toString()
        CacheUtil.putObjectToCache(redisTemplate, OUT_TRADE_NO_KEY, outTradeNo, payment.id)
        CacheUtil.putToSet(redisTemplate, orderNo, outTradeNo)
        log.debug("订单号[${orderNo}],payment id[${payment.id}],原流水号[${payment.outTradeNo}],新流水号[${outTradeNo}]")
        payment.setOutTradeNo(outTradeNo)
        return payment.outTradeNo
    }

    Long getPaymentIdByOutTradeNo(String outTradeNo) {
        return CacheUtil.getObjectFromCache(redisTemplate, OUT_TRADE_NO_KEY, outTradeNo, Long.class)
    }

}
