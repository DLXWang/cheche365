package com.cheche365.cheche.wallet.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mjg on 6/6/17.
 */
@Entity
public class WalletFrozenHistory {
    private long id;
    private Date createTime;
    private long walletId;
    private BigDecimal amount;
    private BigDecimal currentAmt;
    private int frozenType;
    private String requestNo;
    private int trxType;
    private String remark;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Column(name="create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name="wallet_id")
    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(name="current_amt")
    public BigDecimal getCurrentAmt() {
        return currentAmt;
    }

    public void setCurrentAmt(BigDecimal currentAmt) {
        this.currentAmt = currentAmt;
    }

    @Column(name="frozen_type")
    public int getFrozenType() {
        return frozenType;
    }

    public void setFrozenType(int frozenType) {
        this.frozenType = frozenType;
    }

    @Column(name="request_no")
    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    @Column(name="trx_type")
    public int getTrxType() {
        return trxType;
    }

    public void setTrxType(int trxType) {
        this.trxType = trxType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
