package com.cheche365.cheche.wechat.model;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wechat.message.UnifiedOrderResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by zhengwei on 5/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = false, value = {"purchaseOrderId","returnMsg","aPackage"})
public class PrePayResult {

    private String timeStamp;
    private String nonceString;
    private String prePayId;
    private String paySign;
    private String purchaseOrderId;
    private String signType;
    private String returnMsg;
    private String QRImageUrl;
    private String aPackage;
    private String partnerId;
    private String appId;
    private String deepLink;

    public PrePayResult(UnifiedOrderResponse response, Channel channel) {
        this.timeStamp = String.valueOf((System.currentTimeMillis() / 1000));
        this.nonceString = RandomStringUtils.randomAlphanumeric(32);
        this.returnMsg = response.getReturn_msg();
        this.prePayId = response.getPrepay_id();
        this.partnerId = WechatConstant.findMchId(channel);
        this.appId = WechatConstant.findAppId(channel);
        this.signType = "MD5";
        this.deepLink = response.getMweb_url();
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPrePayId() {
        return prePayId;
    }

    public void setPrePayId(String prePayId) {
        this.prePayId = prePayId;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public void setQRImageUrl(String QRImageUrl) {
        this.QRImageUrl = QRImageUrl;
    }

    public String getQRImageUrl() {
        return QRImageUrl;
    }

    public void setPackage(String aPackage) {
        this.aPackage = aPackage;
    }

    public String getPackage(){
        return aPackage;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public String getNonceString() {
        return nonceString;
    }

    public void setNonceString(String nonceString) {
        this.nonceString = nonceString;
    }
}
