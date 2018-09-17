package com.cheche365.cheche.web.service.order

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftStatus
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OperationType
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PaymentStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderGift
import com.cheche365.cheche.core.model.PurchaseOrderGiftHistory
import com.cheche365.cheche.core.model.PurchaseOrderHistory
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.AddressRepository
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.GiftRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository
import com.cheche365.cheche.core.repository.PurchaseOrderGiftRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.repository.WebPurchaseOrderRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.core.repository.agent.UserCustomerRepository
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.MarketingSuccessService
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.core.service.PaymentAmountCalculator
import com.cheche365.cheche.core.service.PurchaseOrderGiftHistoryService
import com.cheche365.cheche.core.service.PurchaseOrderHistoryService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.QuoteSupplementInfoService
import com.cheche365.cheche.core.service.SimplifiedOrder
import com.cheche365.cheche.core.service.agent.ChannelRebateService
import com.cheche365.cheche.core.service.gift.rules.DefaultGiftRule
import com.cheche365.cheche.core.service.WebPurchaseOrderService
import com.cheche365.cheche.web.service.ZhongAnSignService
import com.cheche365.cheche.web.service.order.discount.AgentDiscountService
import com.cheche365.cheche.web.service.order.discount.strategy.DiscountCalculator
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.core.exception.BusinessException.Code.OPERATION_NOT_ALLOWED
import static com.cheche365.cheche.core.exception.BusinessException.Code.ORDER_ALREADY_PAID
import static com.cheche365.cheche.core.exception.BusinessException.Code.ORDER_STATUS_ERROR
import static com.cheche365.cheche.core.model.OrderStatus.Enum.CANCELED_6
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.INSURE_FAILURE_7
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDED_9
import static com.cheche365.cheche.core.model.OrderStatus.Enum.immutableStatus
import static com.cheche365.cheche.core.model.OrderStatus.Enum.isStatusFlowAllowed
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.PAYMENTSUCCESS_2
import static com.cheche365.cheche.core.model.PaymentType.Enum.BAOXIANPAY_8
import static com.cheche365.cheche.core.model.PaymentType.Enum.CHECHEPAY_6
import static com.cheche365.cheche.core.model.PaymentType.Enum.DISCOUNT_5
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11
import static com.cheche365.cheche.core.util.AutoUtils.encryptionWithStar

/**
 * Created by zhengwei on 08/02/2018.
 * C端订单服务
 */

@Service
@Slf4j
class ClientOrderService {

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;
    @Autowired
    private WebPurchaseOrderRepository webOrderRepository
    @Autowired
    private OrderImageService orderImageService
    @Autowired
    private PaymentRepository paymentRepository
    @Autowired
    private GiftService giftService
    @Autowired
    private PurchaseOrderAmendRepository orderAmendRepository
    @Autowired
    private PaymentAmountCalculator amountCalculator
    @Autowired
    private QuoteConfigService quoteConfigService
    @Autowired
    private UserCustomerRepository userCustomerRepository
    @Autowired
    private ChannelRebateService channelRebateService
    @Autowired
    private AgentDiscountService agentDiscountService
    @Autowired
    PurchaseOrderHistoryService orderHistoryService
    @Autowired
    PurchaseOrderGiftHistoryService orderGiftHistoryService
    @Autowired
    private PurchaseOrderGiftRepository purchaseOrderGiftRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private DiscountCalculator paymentGenerator
    @Autowired
    private GiftRepository giftRepository;
    @Autowired
    private MarketingSuccessService marketingSuccessService;
    @Autowired
    private OrderOperationInfoService orderOperationInfoService;
    @Autowired
    @Qualifier('defaultGiftRule')
    private DefaultGiftRule giftRule;
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    private ChannelAgentRepository channelAgentRepository
    @Autowired
    private WebPurchaseOrderService orderService
    @Autowired
    private QuoteSupplementInfoService quoteSupplementInfoService
    @Autowired
    ZhongAnSignService zhongAnSignService
    @Autowired
    private StringRedisTemplate stringRedisTemplate

    PurchaseOrder update(PurchaseOrder oldOrder, PurchaseOrder newOrder) {

        if (immutableStatus().contains(oldOrder.status)){
            throw new BusinessException(PAID_3 == oldOrder.status ? ORDER_ALREADY_PAID : ORDER_STATUS_ERROR, "订单不能修改，当前状态为： "+oldOrder.getStatus().getStatus());  //当是PAID状态时，前端需要单独的code
        }
        PurchaseOrderHistory orderHistory = this.saveOrderHistory(oldOrder, OperationType.Enum.ORDER_UPDATE)
        def payments = paymentRepository.findByPurchaseOrder(oldOrder)
        this.switchGift(oldOrder, payments, newOrder.giftId, false, orderHistory)

        paymentRepository.save(payments)
        this.updatePaymentChannel(oldOrder, newOrder)
        this.updateInsuredRelated(oldOrder, newOrder)
        this.updateDeliveryRelated(oldOrder, newOrder)

        PurchaseOrder afterSave
        afterSave = this.webOrderRepository.save(oldOrder);

        log.debug("updated the purchase order {}", afterSave.getOrderNo());

        return afterSave==null ? oldOrder : afterSave;

    }

    PurchaseOrder cancel(PurchaseOrder purchaseOrder,Boolean setHistory) {
        PurchaseOrderHistory history = this.saveOrderHistory(purchaseOrder, OperationType.Enum.ORDER_CANCEL)
        purchaseOrder.setStatusDisplay(null) //#10688 for fanhua
        purchaseOrder = this.releaseOrderGift(purchaseOrder, CANCELED_6, history);
        // 同步订单状态到出单中心状态
        if(setHistory){
            orderOperationInfoService.setExpiredStatus(purchaseOrder);
        }else{
            orderOperationInfoService.setExpiredStatus(purchaseOrder,false);
        }
        return purchaseOrder;
    }

    PurchaseOrder cancel(PurchaseOrder purchaseOrder){
        this.cancel(purchaseOrder,true)
    }

    PurchaseOrder refund(PurchaseOrder purchaseOrder) {
        PurchaseOrderHistory history = this.saveOrderHistory(purchaseOrder, OperationType.Enum.ORDER_REFUND)
        purchaseOrder = this.releaseOrderGift(purchaseOrder, REFUNDED_9, history);
        // 同步订单状态到出单中心状态
        orderOperationInfoService.setExpiredStatus(purchaseOrder);
        return purchaseOrder;
    }

    Map purchaseOrderBills(PurchaseOrder purchaseOrder, Channel channel) {
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.objId)
        def sourceData = [
            channel      : channel,
            quoteRecord  : quoteRecord,
            purchaseOrder: purchaseOrder,
            payments     : paymentRepository.findAllByPurchaseOrder(purchaseOrder),
            ci           : compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByIdDesc(quoteRecord),
            insurance    : insuranceRepository.findFirstByQuoteRecordOrderByIdDesc(quoteRecord),
        ]

        fillOrderInsuranceInfo(sourceData)
        sourceData.gifts = giftService.findGiftsByOrder(sourceData)
        sourceData.customerPayments = Payment.customPayments(sourceData.payments)

        Boolean showImageTab =  orderImageService.showImageTab(quoteRecord, purchaseOrder)

        sourceData << [
            'orderAmended'      : orderAmendRepository.findLatestAmendNotFullRefundNotCancel(purchaseOrder) as boolean,
            'amendAmount'       : amountCalculator.customerAdditionalPayable(sourceData.payments),
            'needUploadImage'   : showImageTab && orderImageService.uploadStatusEnabled(quoteRecord, purchaseOrder),
            'showImageTab'      : showImageTab,
            'showDailyInsurance': showDailyInsurance(purchaseOrder, quoteRecord),
            'reinsure'          : needReInsure(quoteRecord, purchaseOrder),
            'innerPay'          : quoteConfigService.isInnerPay(quoteRecord, purchaseOrder),
            'canRenewal'        : canRenewal(sourceData),
            'specialAgreement'  : insuranceRepository.findByQuoteRecordId(quoteRecord.id)?.specialAgreement,
            'supportRequote'    : purchaseOrder.supportReQuote()
        ]

        boolean payWarningSupport = (quoteRecord.getType() in [AGENTPARSER_9, PLATFORM_BOTPY_11]) && (PENDING_PAYMENT_1 == purchaseOrder.status)
        sourceData << [
            payWarningSupport: payWarningSupport,
            needPayWarning   : payWarningSupport ?: null
        ]

        ChannelAgent channelAgent = channelAgentRepository.findByUserAndChannel(purchaseOrder.applicant, Channel.findAgentChannel(channel))
        if (channelAgent && channel.isLevelAgent()) {
            agentDiscountService.loadDiscountsByOrder(sourceData.quoteRecord, sourceData.purchaseOrder, channelAgent)
        } else if (quoteRecord.channel.isAgentChannel()) {
            agentDiscountService.calculateDiscounts(sourceData.quoteRecord, channelRebateService.getChannelRebate(quoteRecord, purchaseOrder))
            sourceData.customer = userCustomerRepository.findByPurchaseOrder(purchaseOrder)?.customer
        }

        if(quoteRecord.insuranceCompany.isZhongAn() && zhongAnSignService.isUnsigned(purchaseOrder)){
            sourceData.signLink = zhongAnSignService.buildSignLink(quoteRecord.auto)
        }

        def supplementInfo = quoteSupplementInfoService.getSupplementInfosByPurchaseOrder(purchaseOrder)?.collectEntries { [(it.fieldPath): it.value] }
        sourceData.dateInfo = [
            effectiveDate          : sourceData.insurance?.effectiveDate ?: sourceData.quoteRecord.effectiveDate ?: supplementInfo?.commercialStartDate ? _DATE_FORMAT3.parse(supplementInfo.commercialStartDate) : null,
            expireDate             : sourceData.insurance?.expireDate ?: sourceData.quoteRecord.expireDate,
            compulsoryEffectiveDate: sourceData.ci?.effectiveDate ?: sourceData.quoteRecord.compulsoryEffectiveDate ?: supplementInfo?.compulsoryStartDate ? _DATE_FORMAT3.parse(supplementInfo.compulsoryStartDate) : null,
            compulsoryExpireDate   : sourceData.ci?.expireDate ?: sourceData.quoteRecord.compulsoryExpireDate,
        ]

        return new SimplifiedOrder().convert(sourceData)
    }

    private void fillOrderInsuranceInfo(Map sourceData) {
        Insurance insurance = sourceData.insurance
        CompulsoryInsurance compulsoryInsurance = sourceData.ci
        PurchaseOrder purchaseOrder = sourceData.purchaseOrder

        purchaseOrder.insuredName = insurance?.insuredName ?: compulsoryInsurance?.insuredName
        purchaseOrder.insuredIdNo = encryptionWithStar insurance?.insuredIdNo ?: compulsoryInsurance?.insuredIdNo, 'identity'
        purchaseOrder.applicantName = insurance?.applicantName ?: compulsoryInsurance?.applicantName
        purchaseOrder.applicantIdNo = encryptionWithStar insurance?.applicantIdNo ?: compulsoryInsurance?.applicantIdNo, 'identity'
        purchaseOrder.applicantIdentityType = insurance?.applicantIdentityType ?: compulsoryInsurance?.applicantIdentityType
        purchaseOrder.insuredIdentityType = insurance?.insuredIdentityType ?: compulsoryInsurance?.insuredIdentityType
    }

    Boolean needReInsure(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {
        if (INSURE_FAILURE_7 != purchaseOrder.getStatus()) {
            return false
        }

        if (quoteRecord.getInsuranceCompany().reInsureSupport()) {
            return true
        }
        if (quoteConfigService.isBaoXian(quoteRecord.channel, quoteRecord.area, quoteRecord.insuranceCompany)) {
            return true
        }
        if (quoteConfigService.isBotpy(quoteRecord)) {
            return true
        }
        if (quoteConfigService.quoteSourceEquals(quoteRecord.channel, quoteRecord.area, quoteRecord.insuranceCompany, AGENTPARSER_9)) {
            return true
        }
        if (InsuranceCompany.Enum.ZHONGAN_50000 == quoteRecord.insuranceCompany &&
            (Area.isSZArea(quoteRecord.area) || Area.isBJArea(quoteRecord.area))) {
            return true
        }
        if (InsuranceCompany.Enum.SINOSAFE_205000 == quoteRecord.insuranceCompany &&
            Area.isBJArea(quoteRecord.area)) {
            return true
        }

        return false
    }

    boolean showDailyInsurance(PurchaseOrder purchaseOrder, QuoteRecord quoteRecord) {
        if (purchaseOrder.sourceChannel.isStandardAgent()) {
            return false
        }
        purchaseOrder.answernFinished(quoteRecord) &&
            (quoteRecord.premium > 0) &&
            purchaseOrder.dailyInsuranceOperationAllowed()
    }

    boolean canRenewal(sourceData) {
        Channel channel = sourceData.channel
        QuoteRecord quoteRecord = sourceData.quoteRecord
        PurchaseOrder purchaseOrder = sourceData.purchaseOrder

        if (purchaseOrder.status != FINISHED_5) {
            return false
        }

        if (channel.isAgentChannel()) {
            return false
        }

        if (!quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannel(
            quoteRecord.area, quoteRecord.insuranceCompany, channel.parent
        )) {
            return false
        }

        orderService.renewalbe(
            sourceData.purchaseOrder,
            sourceData.insurance ?: insuranceRepository.findFirstByQuoteRecordOrderByIdDesc(quoteRecord),
            sourceData.ci ?: compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByIdDesc(quoteRecord)
        )
    }

    def saveOrderHistory(PurchaseOrder purchaseOrder, OperationType operationType) {
        PurchaseOrderHistory oldOrderHistory = orderHistoryService.saveOrderHistory(purchaseOrder, operationType);
        orderGiftHistoryService.saveOrderGiftHistories(oldOrderHistory);
        oldOrderHistory
    }

    private boolean updatePaymentChannel(PurchaseOrder originalOne, PurchaseOrder newOne){
        if(null == newOne.getChannel()) {
            log.debug("no payment channel detected in the to update order, will not update the payment channel.");
        } else if(newOne.getChannel().getId().equals(originalOne.getChannel().getId())){
            log.debug("the payment channel detected in the to update order is same with the original one, will not update the payment channel, payment channel: {}", originalOne.getChannel().getId());
        } else {
            PaymentChannel formattedChannel = PaymentChannel.Enum.format(newOne.getChannel());
            if(immutableStatus().contains(originalOne.getStatus())){
                throw new BusinessException(OPERATION_NOT_ALLOWED, "订单 "+originalOne.getOrderNo()+" 不能更新支付方式, 状态为["+(originalOne.getStatus()==null ? "" : originalOne.getStatus().getDescription())+"]");
            }

            log.debug("update the payment channel for {}, the original one is {}, new one is {}", originalOne.getOrderNo(), originalOne.getChannel().getId(), formattedChannel.getId());
            originalOne.setChannel(formattedChannel);

            List<Payment> paymentList = this.paymentRepository.findByPurchaseOrder(originalOne);
            if (null != paymentList && !paymentList.isEmpty()) {
                paymentList.each {
                    if (PaymentChannel.Enum.isNonRebatePayment(PaymentChannel.Enum.format(it.getChannel()))) {
                        it.setChannel(formattedChannel);
                        it.setComments(formattedChannel.getDescription());
                        this.paymentRepository.save(it);
                    }
                }
            }
            assembleOrderDescription(paymentList, originalOne);
            return true;
        }

        return false;
    }

    private boolean updateDeliveryRelated(PurchaseOrder originalOne, PurchaseOrder newOne){

        boolean result = false;
        if(null != newOne.getDeliveryAddress()){
            Address addressAfterSave = this.addressRepository.save(newOne.getDeliveryAddress());
            originalOne.appendDescription("修改地址，原地址id: "+originalOne.getDeliveryAddress().getId());
            originalOne.setDeliveryAddress(addressAfterSave);
            log.debug("delivery address is updated for order {}", originalOne.getOrderNo());
            result = true;
        }

        if(null!=newOne.getSendDate() && newOne.getSendDate() != originalOne.getSendDate()){
            originalOne.setSendDate(newOne.getSendDate());
            log.debug("update the send date for order {}, the new send date is {}", originalOne.getOrderNo(), newOne.getSendDate());
            result = true;
        }

        if(null!=newOne.getTimePeriod() && newOne.getTimePeriod() != originalOne.getTimePeriod()){
            originalOne.setTimePeriod(newOne.getTimePeriod());
            log.debug("update the send time period for order{}, the new send time period is {}", originalOne.getOrderNo(), newOne.getTimePeriod());
            result = true;
        }

        return result;

    }

    private boolean updateInsuredRelated(PurchaseOrder originalOne, PurchaseOrder newOne){

        if(StringUtils.isBlank(newOne.getInsuredName()) && StringUtils.isBlank(newOne.getInsuredIdNo())){
            return false;
        }
        QuoteRecord quoteRecord = this.quoteRecordRepository.findOne(originalOne.getObjId());
        if(null == quoteRecord){
            log.error("there is no quote record for purchase order {}, should never happen. ", originalOne.getOrderNo());
            return false;
        }
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord);

        if(null != insurance) {
            if(StringUtils.isNoneBlank(newOne.getInsuredName())){
                insurance.setInsuredName(newOne.getInsuredName());
            }
            if (StringUtils.isNoneBlank(newOne.getInsuredIdNo()) && !newOne.getInsuredIdNo().contains("*")) {
                insurance.setInsuredIdNo(newOne.getInsuredIdNo());
            }

            insurance.setUpdateTime(Calendar.getInstance().getTime());

            Insurance afterSave = insuranceRepository.save(insurance);
            log.debug("update insured related info for insurance bill id {}, the latest insured name: {},  insured id NO {}", afterSave.getId(), afterSave.getInsuredName(), afterSave.getInsuredIdNo());

        }

        if(null != compulsoryInsurance) {
            if(StringUtils.isNoneBlank(newOne.getInsuredName())){
                compulsoryInsurance.setInsuredName(newOne.getInsuredName());
            }
            if (StringUtils.isNoneBlank(newOne.getInsuredIdNo()) && !newOne.getInsuredIdNo().contains("*")) {
                compulsoryInsurance.setInsuredIdNo(newOne.getInsuredIdNo());
            }

            compulsoryInsurance.setUpdateTime(Calendar.getInstance().getTime());
            CompulsoryInsurance afterSave = compulsoryInsuranceRepository.save(compulsoryInsurance);
            log.debug("update insured related info for compulsory insurance bill id {}, the latest insured name: {}, insured  id NO {}", afterSave.getId(), afterSave.getInsuredName(), afterSave.getInsuredIdNo());
        }

        return true;
    }

    static assembleOrderDescription(List<Payment> payments, PurchaseOrder order) {
        StringBuffer description = new StringBuffer();
        for (Payment payment : payments) {
            description.append(",");
            description.append(payment.getComments()).append(payment.getAmount()).append("元");
        }

        if (order.getRealGifts() != null && !order.getRealGifts().isEmpty() && !UserType.Enum.isAgent(order.getApplicant().getUserType())) {
            for (Gift gift : order.getRealGifts()) {
                description.append(",赠送");
                description.append(gift.getGiftType().getName()).append(gift.getQuantity()).append(gift.getUnit());
            }
        }
        order.appendDescription(description.length() > 0 ? description.substring(1) : "");
    }

    def switchGift(PurchaseOrder order, List<Payment> payments, Object newGiftId, boolean amend, PurchaseOrderHistory orderHistory) {

        releaseOldOrderGift(order, payments, amend, orderHistory)

        resetPaymentStatus(payments, order, orderHistory)

        if (newGiftId) {
            order.giftId = newGiftId
        }

        QuoteRecord newQuoteRecord = this.quoteRecordRepository.findOne(order.objId)
        def paymentsAfterNewGift = this.applyNewGift(newQuoteRecord, order, amend)
        if(paymentsAfterNewGift) {
            payments.addAll(paymentsAfterNewGift)
        }
    }

    def releaseOldOrderGift(PurchaseOrder order, List<Payment> payments, boolean amend, PurchaseOrderHistory orderHistory) {
        List<PurchaseOrderGift> oldOrderGifts = this.purchaseOrderGiftRepository.searchUsedGift(order.getId())
        if (oldOrderGifts) {
            oldOrderGifts.each {
                this.releaseGift(it, payments);
                this.updateOrderByGift(it, order, payments, amend, orderHistory);
                log.info("释放一张优惠券，id: {}", it.getGift().getId());
            }
        }
    }

    def releaseGift(PurchaseOrderGift orderGift, List<Payment> payments) {
        Gift gift = orderGift.getGift();
        if (orderGift.isGivenAfterOrder()) {
            gift.setStatus(GiftStatus.Enum.CANCLED_5);
            gift.appendDescription(",订单取消，重置礼品状态为已取消");
        } else {
            if (gift.isExpired()) {
                gift.setStatus(GiftStatus.Enum.EXCEEDED_4);
                gift.appendDescription(",订单取消，重置礼品状态为已过期");
            } else {
                gift.setStatus(GiftStatus.Enum.CREATED_1);
                gift.appendDescription(",订单取消，重置礼品状态为已创建");
            }
        }

        giftRule.processAfterReleaseGift(gift);

        this.giftRepository.save(gift);
    }

    def updateOrderByGift(PurchaseOrderGift orderGift, PurchaseOrder order, List<Payment> payments, boolean amend, PurchaseOrderHistory orderHistory) {

        Payment customerPay = payments.find{PaymentChannel.Enum.isNonRebatePayment(it.channel)}

        if(!customerPay){
            log.error('订单未{}找到用户支付payment记录，忽略处理切换优惠券逻辑', order.orderNo)
            return
        }


        log.debug("修改前用户支付金额 ${customerPay.amount}")

        payments.findAll { !it.purchaseOrderHistory }.each { it.purchaseOrderHistory = orderHistory }
        payments.findAll {
            PaymentChannel.Enum.isCoupon(it.channel) && DISCOUNT_5 == it.paymentType && it.status != PaymentStatus.Enum.CANCEL_4
        }.each {
            it.status = PaymentStatus.Enum.CANCEL_4
            log.debug("切换优惠券时将payment状态置为取消，订单 {}, payment {}", order.getOrderNo(), it.id);

            if(!amend) {
                customerPay.amount += it.amount
                order.paidAmount += it.amount
                log.debug("用户支付金额增加取消优惠券金额, 优惠券金额 ${it.amount}, 当前用户支付金额 ${customerPay.amount}")
            }
        }

        this.purchaseOrderGiftRepository.delete(orderGift);
        log.debug("delete the purchase order gift record for user remove the gift to use");
        assembleOrderDescription(payments, order);
    }

    def applyNewGift(QuoteRecord quoteRecord, PurchaseOrder order, boolean amend) {

        def payments = paymentGenerator.calculatePurchaseOrder(quoteRecord, order, order.giftId)

        amend ? payments.find{it.paymentType == DISCOUNT_5} : payments  //增补的情况只需要优惠券的payment,不需要用户支付部分
    }

    private PurchaseOrder releaseOrderGift(PurchaseOrder order, OrderStatus finalStatus, PurchaseOrderHistory orderHistory) {

        if(!isStatusFlowAllowed(order.status, finalStatus)){
            throw new BusinessException(OPERATION_NOT_ALLOWED, "无效操作，当前状态为： "+order.getStatus().getStatus());
        }

        releaseOldOrderGift(order, paymentRepository.findByPurchaseOrder(order), false, orderHistory)

        marketingSuccessService.releaseDirectReduce(order);

        order.setStatus(finalStatus);
        order.appendDescription(",订单被取消");

        return this.webOrderRepository.save(order);
    }

    private List<Payment> resetPaymentStatus(List<Payment> payments, PurchaseOrder order, PurchaseOrderHistory orderHistory) {
        payments.findAll { !it.purchaseOrderHistory }.each { it.purchaseOrderHistory = orderHistory }
        payments.findAll {
            it.status == NOTPAYMENT_1 || (it.status == PAYMENTSUCCESS_2 && [DISCOUNT_5, CHECHEPAY_6, BAOXIANPAY_8].contains(it.paymentType))
        }.each {
            it.status = PaymentStatus.Enum.CANCEL_4
            log.debug("切换优惠券时将payment状态置为取消，订单 {}, payment {}", order.getOrderNo(), it.id);
        }
    }

    def revertGiftByOrderHistory(PurchaseOrderHistory orderHistory) {
        List<PurchaseOrderGiftHistory> orderGiftHistories = orderGiftHistoryService.findByPurchaseOrderHistory(orderHistory)
        List<PurchaseOrderGift> orderGifts = new ArrayList<>();
        orderGiftHistories.each {
            it.gift.status = it.getGivenAfterOrder() ? GiftStatus.Enum.WAITDELIVERED_6 : GiftStatus.Enum.USED_3
            it.gift.appendDescription(",订单全额退款回退，重置礼品状态为使用状态")

            PurchaseOrderGift orderGift = new PurchaseOrderGift()
            orderGift.setGift(it.gift)
            orderGift.setGivenAfterOrder(it.givenAfterOrder)
            orderGift.setPurchaseOrder(it.purchaseOrderHistory.purchaseOrder)
            orderGifts.add(orderGift)
        }
        purchaseOrderGiftRepository.save(orderGifts)
    }

}
