package com.cheche365.cheche.core.model

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext
import static com.cheche365.cheche.core.constants.CacheConstants.ZHONGAN_SIGN_UPLOAD_FINISHED

class SimpleOrderResult {

    @JsonIgnore
    QuoteRecord quoteRecord
    @JsonIgnore
    PurchaseOrder purchaseOrder

    SimpleOrderResult(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {
        this.quoteRecord = quoteRecord
        this.purchaseOrder = purchaseOrder
    }

    Integer getQuoteFieldsCount() {
        quoteRecord.insurancePackage.countQuotedFields()
    }

    String getLicensePlateNo() {
        quoteRecord.auto.licensePlateNo
    }

    Map getInsuranceCompany() {
        quoteRecord.insuranceCompany.with {
            [
                id     : it.id,
                name   : it.name,
                code   : it.code,
                logoUrl: it.logoUrl

            ]
        }
    }

    String getOrderNo() {
        purchaseOrder.orderNo
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    Double getPayableAmount() {
        purchaseOrder.payableAmount
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    Double getPaidAmount() {
        purchaseOrder.paidAmount
    }

    OrderStatus getStatus() {
        purchaseOrder.statusDisplay()
    }

    Date getCreateTime() {
        purchaseOrder.createTime
    }

    Date getUpdateTime() {
        purchaseOrder.updateTime
    }

    String getExpireTime() {
        purchaseOrder.answernFinished(quoteRecord) ?
            null :
            DateUtils.getDateString(purchaseOrder.expireTime, DateUtils.DATE_LONGTIME24_PATTERN)
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    Double getAmendAmount() {

        def paymentRepository = applicationContext.getBean("paymentRepository")
        def payments = paymentRepository.findAllByPurchaseOrder(purchaseOrder)

        def amountCalculator = applicationContext.getBean("paymentAmountCalculator")
        amountCalculator.customerAdditionalPayable(payments)
    }

    Boolean getCanRenewal() {
        def sourceData = [
            channel      : purchaseOrder.sourceChannel,
            quoteRecord  : quoteRecord,
            purchaseOrder: purchaseOrder
        ]

        def clientOrderService = applicationContext.getBean("clientOrderService")
        clientOrderService.canRenewal(sourceData)
    }

    Boolean isShowImageTab() {
        def orderImageService = applicationContext.getBean("orderImageService")
        orderImageService.showImageTab(quoteRecord, purchaseOrder)
    }

    Boolean isNeedUploadImage() {
        def orderImageService = applicationContext.getBean("orderImageService")
        def showImageTab = orderImageService.showImageTab(quoteRecord, purchaseOrder)
        showImageTab && orderImageService.uploadStatusEnabled(quoteRecord, purchaseOrder)
    }

    Boolean isNeedSignLink(){
        quoteRecord.insuranceCompany.isZhongAn() &&
        OrderStatus.Enum.FINISHED_5 == purchaseOrder.status &&
        !applicationContext.getBean('stringRedisTemplate').opsForSet().isMember(ZHONGAN_SIGN_UPLOAD_FINISHED, purchaseOrder.orderNo)
    }

    Boolean isSupportInsurance() {
        quoteRecord.premium > 0.0
    }

    Boolean isSupportCompulsoryInsurance() {
        (quoteRecord.autoTax + quoteRecord.compulsoryPremium) > 0.0
    }

    Boolean isInnerPay(){
        applicationContext.getBean("quoteConfigService").isInnerPay(quoteRecord, purchaseOrder)
    }

    Boolean isReinsure(){
        applicationContext.getBean("clientOrderService").needReInsure(quoteRecord, purchaseOrder)
    }

    Boolean isSupportRequote(){
        purchaseOrder.supportReQuote()
    }
}
