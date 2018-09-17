package com.cheche365.cheche.web.util

import com.cheche365.cheche.common.util.AreaUtils
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.web.service.order.PurchaseOrderLockService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.constants.WebConstants.NOT_CHECK_ALLOW_PAY
import static com.cheche365.cheche.core.constants.WebConstants.NOT_CHECK_CHANNEL
import static com.cheche365.cheche.core.constants.WebConstants.PERIOD_NOT_ALLOWED_PAY
import static com.cheche365.cheche.core.exception.BusinessException.Code.*
import static com.cheche365.cheche.core.model.Area.Enum.NJ
import static com.cheche365.cheche.core.model.Area.Enum.SZ
import static com.cheche365.cheche.core.model.InsuranceCompany.apiQuoteCompanies
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.payableStatus
import static com.cheche365.cheche.core.model.PaymentType.Enum.ADDITIONALPAYMENT_2
import static com.cheche365.cheche.core.model.QuoteSource.Enum.*

/**
 * Created by zhengwei on 08/08/2018.
 */
class PaymentValidationUtil {

    private static Logger logger = LoggerFactory.getLogger(PaymentValidationUtil.class)


    static void checkBeforePay(OrderRelatedService.OrderRelated or, Channel channel, Boolean pingPlusPay, HttpServletRequest request) {

        PurchaseOrderLockService lockService = ApplicationContextHolder.getApplicationContext().getBean(PurchaseOrderLockService.class)
        if(lockService.stillLocking(or.po.orderNo)){
            throw new BusinessException(PAY_NOT_ALLOWED, "订单正在核保中，请稍后重试")
        }

        validateOrderPayable(channel, or.qr, or.po, pingPlusPay, request)

        Payment pending = or.findPending()
        if (null == pending) {
            throw new BusinessException(ORDER_ALREADY_PAID, "无待支付记录")
        }

        if (pending.reDriving()) {
            logger.info("安心订单复驶支付, 跳过订单状态和过期时间校验, 订单号为:{} ,当前渠道为:{} ,保险公司为:{}", or.po.orderNo, channel.name, or.qr.insuranceCompany.name)
            return
        }
        if (PAID_3 == or.po.getStatus()) {
            throw new BusinessException(ORDER_ALREADY_PAID, "订单已支付完成");
        }
        if (!payableStatus().contains(or.po.getStatus())) {
            throw new BusinessException(ORDER_STATUS_ERROR, "订单不允许支付, 状态为[${or.po.status?.description}]")
        }

        if (or.qr.getEffectiveDate() != null && DateUtils.compareDate(new Date(), or.qr.getEffectiveDate())) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, "该订单已失效，请重新下单或联系客服。")
        }
        if (ADDITIONALPAYMENT_2 == pending.paymentType) {
            logger.info("订单增补跳过过期时间校验, 订单号为:{} ,当前渠道为:{} ,保险公司为:{}", or.po.orderNo, channel.name, or.qr.insuranceCompany.name)
            return
        }
        if (or.po.checkExpire()) {
            throw new BusinessException(OPERATION_NOT_ALLOWED, "订单已过期，不能支付")
        }
    }

    static void validateOrderPayable(Channel channel, QuoteRecord quoteRecord, PurchaseOrder order, Boolean pingPlusPay, HttpServletRequest request) {

        StringRedisTemplate redisTemplate = ApplicationContextHolder.getApplicationContext().getBean(StringRedisTemplate.class)
        if (Boolean.valueOf(redisTemplate.opsForValue().get(NOT_CHECK_CHANNEL + channel.apiPartner?.code)) ||
            Boolean.valueOf(redisTemplate.opsForValue().get(NOT_CHECK_ALLOW_PAY))) {
            return
        }

        if (channel == Channel.Enum.WEB_5) {
            return
        }

        if (quoteRecord.type == RULEENGINE2_8) {
            throw new BusinessException(PAY_NOT_ALLOWED, "订单提交成功,客服会在24小时内与您联系,请保持手机畅通")
        }

        def payNotSupportedChannels = [Channel.Enum.PARTNER_TUHU_203, Channel.Enum.PARTNER_JD]
        if (quoteRecord.type == REFERENCED_7 && payNotSupportedChannels.contains(quoteRecord.channel)) {
            throw new BusinessException(PAY_NOT_ALLOWED, "订单提交成功,客服会在24小时内与您联系,请保持手机畅通")
        }

        if (!quoteRecord.apiQuote() && isUnpayableArea(quoteRecord.area)) {
            throw new BusinessException(PAY_NOT_ALLOWED, "订单提交成功,客服会在24小时内与您联系,请保持手机畅通")
        }


        Boolean payable = redisTemplate.opsForSet().isMember(WebConstants.ALLOW_ORDER_PAY, order.orderNo)
        if (payable || quoteRecord.apiQuote()) {
            logger.info("订单跳转到第三方支付, 订单号为:{} ,当前渠道为:{} ,保险公司为:{} ,redis状态:{}", order.orderNo, channel.name, quoteRecord.insuranceCompany.name, payable)
            return
        }

        if (!pingPlusPay && Channel.unAllowPay().contains(channel)) {
            throw new BusinessException(PAY_NOT_ALLOWED, "订单提交成功,客服会在24小时内与您联系,请保持手机畅通")
        }

        if ("xiaomic" == channel?.apiPartner?.code && !isSmsUrl(request)) {
            throw new BusinessException(PAY_NOT_ALLOWED, "订单提交成功,客服会在24小时内与您联系,请保持手机畅通")
        }

    }

    static boolean isSmsUrl(HttpServletRequest request) {
        request.getHeader('Referer')?.toURI()?.query?.contains("uuid=")
    }

    static boolean isUnpayableArea(Area area) {
        320000L == AreaUtils.getProvinceCode(area.id) || area in [NJ, SZ]
    }

    static boolean isHolidayPeriod(QuoteRecord quoteRecord, StringRedisTemplate redisTemplate) {
        def begin = Date.parse('yyyy-MM-dd hh:mm:ss', '2018-02-12 00:00:00')
        def end = Date.parse('yyyy-MM-dd hh:mm:ss', '2018-02-21 23:59:59')
        if(apiQuoteCompanies().contains(quoteRecord.insuranceCompany)){ //api报价保险公司假期可出单，泛华除外
            return false
        }
        return (Boolean.valueOf(redisTemplate.opsForValue().get(NOT_CHECK_CHANNEL + quoteRecord.channel?.apiPartner?.code)) ||
            Boolean.valueOf(redisTemplate.opsForValue().get(PERIOD_NOT_ALLOWED_PAY)) ||
            DateUtils.compareCurrentDateBetweenStartDateAndEndDate(new Date(), begin, end))
    }
}
