package com.cheche365.cheche.scheduletask.model;

/**
 * 钱包信息
 * Created by wangshaobin on 2017/7/28.
 */
public class WalletDataInfo extends AttachmentData {
    private String walletTotalBalance;//钱包总余额
    private String outTotalAmount;//提现总金额
    private String outTotalNumber;//提现总次数
    private String walletCount;//钱包总数
    private String outUserCount;//提现用户总数
    private String activeWalletCount;//钱包活跃数
    private String bindingBankCardUserCount;//绑定银行卡的用户数
    private String bindingBankCardCount;//绑定银行卡总数
    private String delBankCardCount;//删除银行卡的用户数
    private String yesterdayOutAmount;//昨日提现金额
    private String yesterdayOutNumber;//昨日提现次数
    private String yesterdayOutUserCount;//昨日提现用户数
    private String yesterdayOutFailCount;//昨日提现失败数
    private String yesterdayOutSuccessCount;//昨日提现成功数
    private String yesterdayOutingCount;//昨日提现中数

    public String getWalletTotalBalance() {
        return walletTotalBalance;
    }

    public void setWalletTotalBalance(String walletTotalBalance) {
        this.walletTotalBalance = walletTotalBalance;
    }

    public String getOutTotalAmount() {
        return outTotalAmount;
    }

    public void setOutTotalAmount(String outTotalAmount) {
        this.outTotalAmount = outTotalAmount;
    }

    public String getOutTotalNumber() {
        return outTotalNumber;
    }

    public void setOutTotalNumber(String outTotalNumber) {
        this.outTotalNumber = outTotalNumber;
    }

    public String getWalletCount() {
        return walletCount;
    }

    public void setWalletCount(String walletCount) {
        this.walletCount = walletCount;
    }

    public String getOutUserCount() {
        return outUserCount;
    }

    public void setOutUserCount(String outUserCount) {
        this.outUserCount = outUserCount;
    }

    public String getActiveWalletCount() {
        return activeWalletCount;
    }

    public void setActiveWalletCount(String activeWalletCount) {
        this.activeWalletCount = activeWalletCount;
    }

    public String getBindingBankCardUserCount() {
        return bindingBankCardUserCount;
    }

    public void setBindingBankCardUserCount(String bindingBankCardUserCount) {
        this.bindingBankCardUserCount = bindingBankCardUserCount;
    }

    public String getBindingBankCardCount() {
        return bindingBankCardCount;
    }

    public void setBindingBankCardCount(String bindingBankCardCount) {
        this.bindingBankCardCount = bindingBankCardCount;
    }

    public String getDelBankCardCount() {
        return delBankCardCount;
    }

    public void setDelBankCardCount(String delBankCardCount) {
        this.delBankCardCount = delBankCardCount;
    }

    public String getYesterdayOutAmount() {
        return yesterdayOutAmount;
    }

    public void setYesterdayOutAmount(String yesterdayOutAmount) {
        this.yesterdayOutAmount = yesterdayOutAmount;
    }

    public String getYesterdayOutNumber() {
        return yesterdayOutNumber;
    }

    public void setYesterdayOutNumber(String yesterdayOutNumber) {
        this.yesterdayOutNumber = yesterdayOutNumber;
    }

    public String getYesterdayOutUserCount() {
        return yesterdayOutUserCount;
    }

    public void setYesterdayOutUserCount(String yesterdayOutUserCount) {
        this.yesterdayOutUserCount = yesterdayOutUserCount;
    }

    public String getYesterdayOutFailCount() {
        return yesterdayOutFailCount;
    }

    public void setYesterdayOutFailCount(String yesterdayOutFailCount) {
        this.yesterdayOutFailCount = yesterdayOutFailCount;
    }

    public String getYesterdayOutSuccessCount() {
        return yesterdayOutSuccessCount;
    }

    public void setYesterdayOutSuccessCount(String yesterdayOutSuccessCount) {
        this.yesterdayOutSuccessCount = yesterdayOutSuccessCount;
    }

    public String getYesterdayOutingCount() {
        return yesterdayOutingCount;
    }

    public void setYesterdayOutingCount(String yesterdayOutingCount) {
        this.yesterdayOutingCount = yesterdayOutingCount;
    }
}
