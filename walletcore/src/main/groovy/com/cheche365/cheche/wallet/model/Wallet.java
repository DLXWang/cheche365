package com.cheche365.cheche.wallet.model;

import com.cheche365.cheche.core.model.Channel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mjg on 6/6/17.
 */
@Entity
public class Wallet {
    private Long id;
    private Long userId;
    private String mobile;
    private Channel channel;
    private String paymentPwd;
    private BigDecimal balance;
    private BigDecimal unbalance;
    private int status;
    private Date createTime;
    private Date updateTime;
    private String memo;
    private int wrTimes;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @ManyToOne
    @JoinColumn(name = "channel", foreignKey = @ForeignKey(name = "FK_WALLET_REF_CHANNEL", foreignKeyDefinition = "FOREIGN KEY (`channel`) REFERENCES `channel` (`id`)"))
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Column(name="payment_pwd")
    public String getPaymentPwd() {
        return paymentPwd;
    }

    public void setPaymentPwd(String paymentPwd) {
        this.paymentPwd = paymentPwd;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getUnbalance() {
        return unbalance;
    }

    public void setUnbalance(BigDecimal unbalance) {
        this.unbalance = unbalance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name="wr_times")
    public int getWrTimes() {
        return wrTimes;
    }

    public void setWrTimes(int wrTimes) {
        this.wrTimes = wrTimes;
    }
}
