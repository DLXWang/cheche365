package com.cheche365.cheche.alipay.dto;

/**
 * Created by chenxiaozhe on 15-8-18.
 */
public class PayRequestDto {
    private String partner;//签约合作者身份ID
    private String sellerId;// 签约卖家支付宝账号
    private String outTradeNo;// 商户网站唯一订单号
    private String subject;// 商品名称
    private String body;// 商品详情
    private String totalFee;//商品金额

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
