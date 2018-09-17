package com.cheche365.cheche.rest.model;

/**
 * Created by zhengwei on 12/24/15.
 * 支付API的输入参数，之前用PaymentChannel，扩展性不好，后续改成这个
 */
public class PaymentWrapper {

    private Long id;  //payment channel id，为了和前端兼容，只能叫id

    private String uuid;

    private boolean smspay;  //是否直接跳到从支付页面

    private Long paymentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isSmspay() {
        return smspay;
    }

    public void setSmspay(boolean smspay) {
        this.smspay = smspay;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
