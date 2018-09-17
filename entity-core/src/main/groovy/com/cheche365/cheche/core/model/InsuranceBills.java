package com.cheche365.cheche.core.model;

/**
 * Created by zhengwei on 7/11/15.
 */

public class InsuranceBills {

    Insurance insurance;
    CompulsoryInsurance ci;
    String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public CompulsoryInsurance getCi() {
        return ci;
    }

    public void setCi(CompulsoryInsurance ci) {
        this.ci = ci;
    }
}
