package com.cheche365.cheche.wallet.model;

import com.cheche365.cheche.core.model.Bank;
import com.cheche365.cheche.core.model.Channel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mjg on 6/6/17.
 */
@Entity
public class WalletRemitRecord {
    private Long id;
    private String requestNo;
    private Date remitDate;
    private long tradeSource;
    private long accountType;
    private Bank bankId;
    private String accountNo;
    private String accountName;
    private String bankName;
    private String province;
    private String city;
    private BigDecimal tradeAmt;
    private BigDecimal tradeFee;
    private WalletTradeStatus status;
    private String remark;
    private String responseNo;
    private Date responseTime;
    private String responseCode;
    private String responseMsg;
    private Date createTime;
    private Date updateTime;
    private Channel channel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="request_no")
    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    @Column(name="remit_date")
    public Date getRemitDate() {
        return remitDate;
    }

    public void setRemitDate(Date remitDate) {
        this.remitDate = remitDate;
    }

    @Column(name="trade_source")
    public long getTradeSource() {
        return tradeSource;
    }

    public void setTradeSource(long tradeSource) {
        this.tradeSource = tradeSource;
    }

    @Column(name="account_type")
    public long getAccountType() {
        return accountType;
    }

    public void setAccountType(long accountType) {
        this.accountType = accountType;
    }

    @ManyToOne
    @JoinColumn(name = "bank_id", foreignKey = @ForeignKey(name = "FK_WALLET_REMIT_RECORD_REF_BANK", foreignKeyDefinition = "FOREIGN KEY (bank) REFERENCES bank(id)"))
    public Bank getBankId() {
        return bankId;
    }

    public void setBankId(Bank bankId) {
        this.bankId = bankId;
    }

    @Column(name="account_no")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Column(name="account_name")
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Column(name="bank_name")
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name="trade_amt")
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    @Column(name="trade_fee")
    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    @ManyToOne
    @JoinColumn(name = "status", foreignKey=@ForeignKey(name="FK_WALLET_REMIT_RECORD_REF_TRADE_STATUS", foreignKeyDefinition="FOREIGN KEY (status) REFERENCES wallet_trade_status(id)"))
    public WalletTradeStatus getStatus() {
        return status;
    }

    public void setStatus(WalletTradeStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Column(name="response_no")
    public String getResponseNo() {
        return responseNo;
    }

    public void setResponseNo(String responseNo) {
        this.responseNo = responseNo;
    }

    @Column(name="response_time")
    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    @Column(name="response_code")
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Column(name="response_msg")
    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
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

    @ManyToOne
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
