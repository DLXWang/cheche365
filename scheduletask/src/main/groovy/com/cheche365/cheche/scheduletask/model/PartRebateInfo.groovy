package com.cheche365.cheche.scheduletask.model

/**
 * 部分退款信息
 * Created by zhangpengcheng on 2018/5/7.
 */
public class PartRebateInfo extends AttachmentData{
    private String orderNo;//部分退款我方订单号
    private String chargeOrderNo;//部分退款商户订单号
    private String amount;// 部分退款失败的金额
    private String orderStatus;//部分退款现状

    String getOrderNo() {
        return orderNo
    }

    void setOrderNo(String orderNo) {
        this.orderNo = orderNo
    }

    String getAmount() {
        return amount
    }

    void setAmount(String amount) {
        this.amount = amount
    }

    String getOrderStatus() {
        return orderStatus
    }

    void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus
    }

    String getChargeOrderNo() {
        return chargeOrderNo
    }

    void setChargeOrderNo(String chargeOrderNo) {
        this.chargeOrderNo = chargeOrderNo
    }
}
