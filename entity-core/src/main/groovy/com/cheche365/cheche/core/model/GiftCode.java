package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mahong on 2015/6/26.
 */
@Entity
public class GiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(50)")
    private String code;
    @Column(columnDefinition = "DATE")
    private Date effectiveDate;
    @Column(columnDefinition = "DATE")
    private Date expireDate;
    @Column(columnDefinition = "DATETIME")
    private Date createTime;
    @ManyToOne
    @JoinColumn(name = "exchangeWay", foreignKey = @ForeignKey(name = "FK_GIFT_CODE_REF_EXCHANGE_WAY", foreignKeyDefinition = "FOREIGN KEY (exchangeWay) REFERENCES gift_code_exchange_way(id)"))
    private GiftCodeExchangeWay exchangeWay;
    @Column(columnDefinition = "TINYINT(1)")
    private boolean exchanged;
    @ManyToOne
    @JoinColumn(name = "applicant", foreignKey = @ForeignKey(name = "FK_GIFT_CODE_REF_USER", foreignKeyDefinition = "FOREIGN KEY (applicant) REFERENCES user(id)"))
    private User applicant;
    @Column(columnDefinition = "DATETIME")
    private Date exchangeTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public GiftCodeExchangeWay getExchangeWay() {
        return exchangeWay;
    }

    public void setExchangeWay(GiftCodeExchangeWay exchangeWay) {
        this.exchangeWay = exchangeWay;
    }

    public boolean isExchanged() {
        return exchanged;
    }

    public void setExchanged(boolean exchanged) {
        this.exchanged = exchanged;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Date getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(Date exchangeTime) {
        this.exchangeTime = exchangeTime;
    }
}
