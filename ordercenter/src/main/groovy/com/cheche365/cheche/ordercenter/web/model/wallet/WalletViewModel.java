package com.cheche365.cheche.ordercenter.web.model.wallet;

/**
 * Created by wangshaobin on 2017/6/9.
 */
public class WalletViewModel {
    private String mobile;//手机号
    private String status;//当前状态
    private String type;//当前类型
    private String inAmount;//入账总金额
    private String outAmount;//提现总金额
    private String balance;//当前余额
    private String lastOperatorTime;//最后操作时间
    //银行信息
    private String bankName;//银行名称
    private String bankNo;//银行卡号
    //交易信息
    private String tradeNo;//流水号
    private String licensePlateNo;//车牌号
    private String opeateAmount;//操作金额
    private String platform;//操作平台
    private Long walletId;
    private String failReason;//订单失败原因

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getOpeateAmount() {
        return opeateAmount;
    }

    public void setOpeateAmount(String opeateAmount) {
        this.opeateAmount = opeateAmount;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInAmount() {
        return inAmount;
    }

    public void setInAmount(String inAmount) {
        this.inAmount = inAmount;
    }

    public String getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(String outAmount) {
        this.outAmount = outAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLastOperatorTime() {
        return lastOperatorTime;
    }

    public void setLastOperatorTime(String lastOperatorTime) {
        this.lastOperatorTime = lastOperatorTime;
    }

    public String getfailReason() {
        return failReason;
    }

    public void setfailReason(String failReason) {
        this.failReason = failReason;
    }
}
