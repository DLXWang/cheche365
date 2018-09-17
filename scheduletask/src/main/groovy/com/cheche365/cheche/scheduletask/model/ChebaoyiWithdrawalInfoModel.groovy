package com.cheche365.cheche.scheduletask.model
/**
 * Created by yinJianBin on 2018/4/3.
 */
class ChebaoyiWithdrawalInfoModel extends AttachmentData {
    String batchNo //批次号
    String sumCount    //总笔数
    String sumAmount   //总金额(元)
    String merchantSeqNo //商户订单号
    String accountNo  //收款账号
    String accountName //收款户名
    String identity    //身份证号
    String transferAmount  //打款金额
    String userId //用户id
    String walletTradeId //walleTradeId
    String requestNo //请求流水号

    String getRequestNo() {
        return requestNo
    }

    void setRequestNo(String requestNo) {
        this.requestNo = requestNo
    }

    String getBatchNo() {
        return batchNo
    }

    void setBatchNo(String batchNo) {
        this.batchNo = batchNo
    }

    String getSumCount() {
        return sumCount
    }

    void setSumCount(String sumCount) {
        this.sumCount = sumCount
    }

    String getSumAmount() {
        return sumAmount
    }

    void setSumAmount(String sumAmount) {
        this.sumAmount = sumAmount
    }

    String getMerchantSeqNo() {
        return merchantSeqNo
    }

    void setMerchantSeqNo(String merchantSeqNo) {
        this.merchantSeqNo = merchantSeqNo
    }

    String getAccountNo() {
        return accountNo
    }

    void setAccountNo(String accountNo) {
        this.accountNo = accountNo
    }

    String getAccountName() {
        return accountName
    }

    void setAccountName(String accountName) {
        this.accountName = accountName
    }

    String getIdentity() {
        return identity
    }

    void setIdentity(String identity) {
        this.identity = identity
    }

    String getTransferAmount() {
        return transferAmount
    }

    void setTransferAmount(String transferAmount) {
        this.transferAmount = transferAmount
    }

    String getUserId() {
        return userId
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    String getWalletTradeId() {
        return walletTradeId
    }

    void setWalletTradeId(String walletTradeId) {
        this.walletTradeId = walletTradeId
    }
}
