package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.OrderType
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.model.agent.CustomerAuto
import com.cheche365.cheche.core.repository.agent.CustomerAutoRepository
import com.cheche365.cheche.core.repository.agent.CustomerRepository
import com.cheche365.cheche.core.service.PurchaseOrderIdService
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.agent.CustomerService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.web.service.PaymentChannelService
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey
import static com.cheche365.cheche.rest.util.WebFlowUtil.getBusinessErrorFSRV

/**
 * Created by zhengwei on 12/20/16.
 */

@Component
@Slf4j
class InitParam implements TPlaceOrderStep {

    @Transactional
    @Override
    def run(Object context) {
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder order = context.order
        PurchaseOrderService orderService = context.orderService
        PurchaseOrderIdService orderIdService = context.orderIdService
        PaymentChannelService paymentChannelService = context.paymentChannelService
        HttpServletRequest request = context.request
        CustomerRepository customerRepository = context.customerRepository
        CustomerAutoRepository customerAutoRepository = context.customerAutoRepository
        CustomerService customerService = context.customerService
        def customer = context.additionalParameters?.order?.customer

        if (orderService.findByQuoteRecordId(quoteRecord.getId())) {
            log.debug("该报价重复下单,qrId ：${quoteRecord.getId()}")
            return getBusinessErrorFSRV (OPERATION_NOT_ALLOWED.codeValue, '该报价已下单，不可重复下单')
        }

        if (!quoteRecord.premium && !quoteRecord.compulsoryPremium) {
            return getBusinessErrorFSRV (OPERATION_NOT_ALLOWED.codeValue, '商业险和交强险都未投保，不能下单。')
        }

        PaymentChannel formattedChannel = PaymentChannel.Enum.format(order.getChannel());
        order.setChannel(formattedChannel);
        quoteRecord.setOwnerMobile(order.getOwnerMobile());
        order.setObjId(quoteRecord.getId());
        order.setType(OrderType.Enum.INSURANCE);
        order.setApplicant(quoteRecord.getApplicant());
        order.setAuto(quoteRecord.getAuto());
        order.setOrderNo(orderIdService.getNext(OrderType.Enum.INSURANCE));
        order.setCreateTime(Calendar.getInstance().getTime());
        order.getDeliveryAddress().setApplicant(quoteRecord.getApplicant());
        order.setArea(quoteRecord.getArea());
        order.setAudit(1);
        order.setSourceChannel(ClientTypeUtil.getChannel(request));

        order.setPaidAmount(quoteRecord.getTotalPremium());
        order.setPayableAmount(order.getPaidAmount());
        order.setStatus(OrderStatus.Enum.PENDING_PAYMENT_1)

        order.setApplicantIdentityType(order.applicantIdentityType?.id ? IdentityType.toIdentityType(order.applicantIdentityType.id) : IdentityType.Enum.IDENTITYCARD)
        order.setInsuredIdentityType(order.insuredIdentityType?.id ? IdentityType.toIdentityType(order.insuredIdentityType.id) : IdentityType.Enum.IDENTITYCARD)

        if (null == order.getChannel()) {
            order.setChannel(getDefaultPaymentChannel(paymentChannelService));
        }

        fillWithOrderChannel(context)
        fillWithExpireTime(context)

        order.auto.billRelated = true
        context.toBePersistObjects.with{
            if(!order.deliveryAddress.id) {
                it << order.deliveryAddress
            }
            if(customer){
                customer = customerRepository.findOne(customer.id as Long)
            }
            if (customer && !customerAutoRepository.findFirstByCustomerAndAuto(customer, order.auto)) {
                it << new CustomerAuto(customer: customer, auto: order.auto)
            }
            if (order.customer && !customer) {
                customer = customerService.createIfNotExists(order.applicant, order.customer)
                it << new CustomerAuto(customer: customer, auto: order.auto)
            }
            it << order
            it << order.auto
        }

        if (order.sourceChannel.isStandardAgent()) {
            context.additionalParameters.agent = ["customer": customer ?: customerAutoRepository.findFirstByAutoOrderByIdDesc(order.auto)?.customer]
        }

        getContinueFSRV true
    }

    static PaymentChannel getDefaultPaymentChannel(PaymentChannelService paymentChannelService) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Channel channel = ClientTypeUtil.getChannel(request);

        List<PaymentChannel> channels = paymentChannelService.getByChannel(channel)
        return channels ? channels[0] : null
    }

    BusinessActivity getCpsActivityFromSession(Object context) {
        Object cpsChannelObj = context.request.session.getAttribute(WebConstants.SESSION_KEY_CPS_CHANNEL)
        if (cpsChannelObj != null) {
            return CacheUtil.doJacksonDeserialize(cpsChannelObj.toString(), BusinessActivity.class)
        }
        return null;
    }

    private void fillWithExpireTime(Object context) {
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder order = context.order
        QuoteRecordCacheService cacheService = context.quoteRecordCacheService
        Map additionalQRMap = cacheService.getPersistentState(persistQRParamHashKey(quoteRecord.getId()))
        if (additionalQRMap?.persistentState?.payValidTime) {
            order.setExpireTime(additionalQRMap.persistentState.payValidTime instanceof Date ? additionalQRMap.persistentState.payValidTime : new Date(additionalQRMap.persistentState.payValidTime))
        } else {
            order.setExpireTime(DateUtils.calculateDate(Calendar.getInstance().getTime(), WebConstants.ORDER_EXPIRE_INTERVAL, WebConstants.ORDER_EXPIRE_INTERVAL_TIMEUNIT))
        }

        if (order.checkExpire()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "本平台只支持起保日期在明天及以后时间的订单(1、不包含起保日期在节假日及节假日之后第一个工作日起保的订单 2、不支持非工作时间操作的订单)")
        }
    }

    private void fillWithOrderChannel(Object context) {
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder order = context.order
        BusinessActivity cpsBusinessActivity = getCpsActivityFromSession(context)
        if (cpsBusinessActivity != null && cpsBusinessActivity.checkActivityDate()) {
            order.setOrderSourceType(OrderSourceType.Enum.CPS_CHANNEL_1)
            order.setOrderSourceId(String.valueOf(cpsBusinessActivity.getId()))
        }

        if (StringUtils.isNotEmpty(quoteRecord.getQuoteSourceId())) {
            order.setOrderSourceType(OrderSourceType.Enum.PLANTFORM_BX_5)
            order.setOrderSourceId(quoteRecord.getQuoteSourceId())
        }

        if(QuoteSource.Enum.AGENTPARSER_9 == quoteRecord.getType()){
            order.setOrderSourceType(OrderSourceType.Enum.PLANTFORM_AGENT_PARSER_9)
        }
    }

}
