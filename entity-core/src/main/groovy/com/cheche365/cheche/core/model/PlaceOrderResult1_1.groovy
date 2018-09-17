package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Created by zhengwei on 7/22/15.
 */
class PlaceOrderResult1_1 {

    private Long id
    private String purchaseOrderNo
    private OrderStatus status
    private Insurance insurance
    private CompulsoryInsurance compulsoryInsurance
    private Double discountAmount
    private Double payableAmount
    private Double paidAmount
    private boolean insureFailure
    private String createTime
    private String expireTime
    private Address deliveryAddress
    private Boolean innerPay
    private boolean reinsure

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    Double getPayableAmount() {
        return payableAmount
    }

    void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    Double getPaidAmount() {
        return paidAmount
    }

    void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount
    }

    CompulsoryInsurance getCompulsoryInsurance() {
        return compulsoryInsurance
    }

    void setCompulsoryInsurance(CompulsoryInsurance compulsoryInsurance) {
        this.compulsoryInsurance = compulsoryInsurance
    }

    String getPurchaseOrderNo() {
        return purchaseOrderNo
    }

    void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo
    }

    OrderStatus getStatus() {
        return status
    }

    void setStatus(OrderStatus status) {
        this.status = status
    }

    Insurance getInsurance() {
        return insurance
    }

    void setInsurance(Insurance insurance) {
        this.insurance = insurance
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    Double getDiscountAmount() {
        return discountAmount
    }

    void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount
    }


    boolean isInsureFailure() {
        return insureFailure
    }

    void setInsureFailure(boolean insureFailure) {
        this.insureFailure = insureFailure
    }

    String getCreateTime() {
        return createTime
    }

    void setCreateTime(String createTime) {
        this.createTime = createTime
    }

    String getExpireTime() {
        return expireTime
    }

    void setExpireTime(String expireTime) {
        this.expireTime = expireTime
    }

    Address getDeliveryAddress() {
        return deliveryAddress
    }

    void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress
    }

    Boolean getInnerPay() {
        return innerPay
    }

    void setInnerPay(Boolean innerPay) {
        this.innerPay = innerPay
    }

    boolean isReinsure() {
        return reinsure
    }

    void setReinsure(boolean reinsure) {
        this.reinsure = reinsure
    }


}
