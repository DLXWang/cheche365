package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by liqiang on 4/9/15.
 */
public class PlaceOrderResult {


    private String purchaseOrderNo;

    private Insurance insurance;

    private CompulsoryInsurance compulsoryInsurance;


    private Double discountAmount;

    private Double totalAmount;

    private Double payableAmount;
    private Double paidAmount;

    private boolean insureFailure;

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public CompulsoryInsurance getCompulsoryInsurance() {
        return compulsoryInsurance;
    }

    public void setCompulsoryInsurance(CompulsoryInsurance compulsoryInsurance) {
        this.compulsoryInsurance = compulsoryInsurance;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }


    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isInsureFailure() {
        return insureFailure;
    }

    public void setInsureFailure(boolean insureFailure) {
        this.insureFailure = insureFailure;
    }


}
