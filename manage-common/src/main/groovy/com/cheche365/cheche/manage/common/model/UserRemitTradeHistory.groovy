package com.cheche365.cheche.manage.common.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Created by yinJianBin on 2018/4/4.
 */
@Entity
class UserRemitTradeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @Column(columnDefinition = "bigint(20)")
    Long userId
    @Column(columnDefinition = "varchar(40)")
    String merchantSeqNo
    @Column
    String requestNo
    @Column
    Long walletRemitTradeId
    @Column
    Date startTime
    @Column
    Date endTime

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    String getMerchantSeqNo() {
        return merchantSeqNo
    }

    void setMerchantSeqNo(String merchantSeqNo) {
        this.merchantSeqNo = merchantSeqNo
    }

    String getRequestNo() {
        return requestNo
    }

    void setRequestNo(String requestNo) {
        this.requestNo = requestNo
    }

    Long getWalletRemitTradeId() {
        return walletRemitTradeId
    }

    void setWalletRemitTradeId(Long walletRemitTradeId) {
        this.walletRemitTradeId = walletRemitTradeId
    }

    Date getStartTime() {
        return startTime
    }

    void setStartTime(Date startTime) {
        this.startTime = startTime
    }

    Date getEndTime() {
        return endTime
    }

    void setEndTime(Date endTime) {
        this.endTime = endTime
    }
}
