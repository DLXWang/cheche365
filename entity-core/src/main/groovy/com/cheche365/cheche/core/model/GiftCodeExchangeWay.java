package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.cheche365.cheche.core.service.giftcode.GiftCodeExchange;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mahong on 2015/6/26.
 */
@Entity
public class GiftCodeExchangeWay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(50)")
    private String name;
    @Column(columnDefinition = "VARCHAR(100)")
    private String exchangeClass;
    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    private Double amount;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_EXCHANGE_WAY_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES INTERNAL_USER(id)"))
    private InternalUser operator;
    @Column(columnDefinition = "DATETIME")
    private Date createTime;
    @Column(columnDefinition = "DATETIME")
    private Date updateTime;
    @Column(columnDefinition = "VARCHAR(200)")
    private String description;
    @Column(columnDefinition = "DATE")
    private Date effectiveDate;
    @Column(columnDefinition = "DATE")
    private Date expireDate;
    @Column(columnDefinition = "VARCHAR(500)")
    private String amountParam;
    @Column(columnDefinition = "VARCHAR(500)")
    private String fullLimitParam;
    @Column(columnDefinition = "VARCHAR(200)")
    private String ruleParam;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchangeClass() {
        return exchangeClass;
    }

    public void setExchangeClass(String exchangeClass) {
        this.exchangeClass = exchangeClass;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAmountParam() {
        return amountParam;
    }

    public void setAmountParam(String amountParam) {
        this.amountParam = amountParam;
    }

    public String getFullLimitParam() {
        return fullLimitParam;
    }

    public void setFullLimitParam(String fullLimitParam) {
        this.fullLimitParam = fullLimitParam;
    }

    public String getRuleParam() {
        return ruleParam;
    }

    public void setRuleParam(String ruleParam) {
        this.ruleParam = ruleParam;
    }

    public GiftCodeExchange createGiftCodeExchanggInstance() {
        String exchangeClass = this.getExchangeClass();
        GiftCodeExchange giftCodeExchange = null;
        try {
            Class clazz = Class.forName(exchangeClass);
            giftCodeExchange = (GiftCodeExchange) clazz.newInstance();
        } catch (Exception e) {
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "优惠码兑换处理类创建失败");
        }
        return giftCodeExchange;
    }
}
