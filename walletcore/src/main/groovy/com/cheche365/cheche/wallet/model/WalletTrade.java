package com.cheche365.cheche.wallet.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mjg on 6/6/17.
 */
@Entity
public class WalletTrade {
    private Long id;
    private Long walletId;
    private Long userId;
    private String tradeNo;
    private Date tradeDate;
    private Integer tradeFlag;
    private WalletTradeSource tradeType;
    private Long tradeSourceId;
    private WalletTradeStatus status;
    private BigDecimal amount;
    private BigDecimal tradeFee;
    private BigDecimal balance;
    private String remark;
    private Long channel;
    private Date createTime;
    private Date updateTime;
    private String partnerRequestno;
    private Long bankcardId;
    private String licensePlateNo;
    private Date stopBeginDate;
    private Date stopEndDate;
    private BigDecimal refundAmt;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="wallet_id")
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    @Column(name="user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name="trade_no")
    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name="trade_date")
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Column(name="trade_flag")
    public Integer getTradeFlag() {
        return tradeFlag;
    }

    public void setTradeFlag(Integer tradeFlag) {
        this.tradeFlag = tradeFlag;
    }

    @ManyToOne
    @JoinColumn(name = "trade_type", foreignKey=@ForeignKey(name="FK_WALLET_TRADE_REF_TRADE_SOURCE", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES wallet_trade_source(id)"))
    public WalletTradeSource getTradeType() {
        return tradeType;
    }

    public void setTradeType(WalletTradeSource tradeType) {
        this.tradeType = tradeType;
    }

    @Column(name="trade_source_id")
    public Long getTradeSourceId() {
        return tradeSourceId;
    }

    public void setTradeSourceId(Long tradeSourceId) {
        this.tradeSourceId = tradeSourceId;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey=@ForeignKey(name="FK_WALLET_TRADE_REF_TRADE_STATUS", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES wallet_trade_status(id)"))
    public WalletTradeStatus getStatus() {
        return status;
    }

    public void setStatus(WalletTradeStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    @Column(name="trade_fee")
    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    @Column(name="create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name="update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name="partner_requestno")
    public String getPartnerRequestno() {
        return partnerRequestno;
    }

    public void setPartnerRequestno(String partnerRequestno) {
        this.partnerRequestno = partnerRequestno;
    }

    @Column(name="bankcard_id")
    public Long getBankcardId() {
        return bankcardId;
    }

    public void setBankcardId(Long bankcardId) {
        this.bankcardId = bankcardId;
    }

    @Column(name="license_plate_no")
    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    @Column(name="stop_begin_date")
    public Date getStopBeginDate() {
        return stopBeginDate;
    }

    public void setStopBeginDate(Date stopBeginDate) {
        this.stopBeginDate = stopBeginDate;
    }

    @Column(name="stop_end_date")
    public Date getStopEndDate() {
        return stopEndDate;
    }

    public void setStopEndDate(Date stopEndDate) {
        this.stopEndDate = stopEndDate;
    }

    @Column(name="refund_amt")
    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
