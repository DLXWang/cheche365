package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mahong on 2016/7/28.
 */
@Entity
public class RuleConfig {
    private Long id;
    private MarketingRule marketingRule;
    private RuleParam ruleParam;
    private String ruleValue;
    private Date createTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "marketingRule", foreignKey = @ForeignKey(name = "FK_RULE_CONFIG_REF_MARKETING_RULE", foreignKeyDefinition = "FOREIGN KEY (`marketing_rule`) REFERENCES `marketing_rule` (`id`)"))
    public MarketingRule getMarketingRule() {
        return marketingRule;
    }

    public void setMarketingRule(MarketingRule marketingRule) {
        this.marketingRule = marketingRule;
    }

    @ManyToOne
    @JoinColumn(name = "ruleParam", foreignKey = @ForeignKey(name = "FK_RULE_CONFIG_REF_RULE_PARAM", foreignKeyDefinition = "FOREIGN KEY (`rule_param`) REFERENCES `rule_param` (`id`)"))
    public RuleParam getRuleParam() {
        return ruleParam;
    }

    public void setRuleParam(RuleParam ruleParam) {
        this.ruleParam = ruleParam;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public RuleConfig(RuleParam ruleParam,String value){
        this.setCreateTime(new Date());
        this.setRuleParam(ruleParam);
        this.setRuleValue(value);
    }
    public RuleConfig(){
    }
}
