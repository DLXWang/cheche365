package com.cheche365.cheche.ordercenter.web.model.freightInsurance;

/**
 * Created by yinJianBin on 2017/8/22.
 */
public class FreightInsuranceOrderRequestModel {

    String orderNo;
    String policyNo;
    String claimed;
    String channel;
    String thirdPartyOrderNo;
    String categoryCode;
    String productName;
    String premiumStart;
    String premiumEnd;
    String insureTimeStart;
    String insureTimeEnd;
    String effectiveTimeStart;
    String effectiveTimeEnd;

    //理赔相关
    String compensationStart;
    String compensationEnd;
    String claimTimeStart;
    String claimTimeEnd;
    String claimNo;


    Integer currentPage;
    Integer pageSize;
    Integer draw;

    Integer pageNumber;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getClaimed() {
        return claimed;
    }

    public void setClaimed(String claimed) {
        this.claimed = claimed;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getThirdPartyOrderNo() {
        return thirdPartyOrderNo;
    }

    public void setThirdPartyOrderNo(String thirdPartyOrderNo) {
        this.thirdPartyOrderNo = thirdPartyOrderNo;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPremiumStart() {
        return premiumStart;
    }

    public void setPremiumStart(String premiumStart) {
        this.premiumStart = premiumStart;
    }

    public String getPremiumEnd() {
        return premiumEnd;
    }

    public void setPremiumEnd(String premiumEnd) {
        this.premiumEnd = premiumEnd;
    }

    public String getInsureTimeStart() {
        return insureTimeStart;
    }

    public void setInsureTimeStart(String insureTimeStart) {
        this.insureTimeStart = insureTimeStart;
    }

    public String getInsureTimeEnd() {
        return insureTimeEnd;
    }

    public void setInsureTimeEnd(String insureTimeEnd) {
        this.insureTimeEnd = insureTimeEnd;
    }

    public String getEffectiveTimeStart() {
        return effectiveTimeStart;
    }

    public void setEffectiveTimeStart(String effectiveTimeStart) {
        this.effectiveTimeStart = effectiveTimeStart;
    }

    public String getEffectiveTimeEnd() {
        return effectiveTimeEnd;
    }

    public void setEffectiveTimeEnd(String effectiveTimeEnd) {
        this.effectiveTimeEnd = effectiveTimeEnd;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }


    public String getCompensationStart() {
        return compensationStart;
    }

    public void setCompensationStart(String compensationStart) {
        this.compensationStart = compensationStart;
    }

    public String getCompensationEnd() {
        return compensationEnd;
    }

    public void setCompensationEnd(String compensationEnd) {
        this.compensationEnd = compensationEnd;
    }

    public String getClaimTimeStart() {
        return claimTimeStart;
    }

    public void setClaimTimeStart(String claimTimeStart) {
        this.claimTimeStart = claimTimeStart;
    }

    public String getClaimTimeEnd() {
        return claimTimeEnd;
    }

    public void setClaimTimeEnd(String claimTimeEnd) {
        this.claimTimeEnd = claimTimeEnd;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
