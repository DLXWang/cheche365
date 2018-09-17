package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@Entity
public class AgentTmp {

    private Long id;
    private String name; //代理人姓名

    private String mobile; //手机
    private String identity; //证件号码
    private IdentityType identityType; //证件类型
    private String cardNumber; //银行卡号

    private User user;
    private String openingBank; //开户行
    private String bankAccount; //账户名
    private String comment; //备注
    private AgentCompany agentCompany ; //代理人公司
    private AgentTmp parentAgent; //上级代理

    private InternalUser operator;//操作人员
    private String bankBranch;//开户行支行

    private Double rebate;//返点
    private Date createTime;
    private Date updateTime;
    private Boolean enable=false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
    @ManyToOne
    @JoinColumn(foreignKey=@ForeignKey(name="FK_AGENT_TMP_REF_IDENTITY_TYPE", foreignKeyDefinition="FOREIGN KEY (identity_type) REFERENCES identity_type(id)"))
    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    @OneToOne
    @JoinColumn(foreignKey=@ForeignKey(name="FK_AGENT_TMP_REF_USER", foreignKeyDefinition="FOREIGN KEY (user) REFERENCES user(id)"))
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @OneToOne
    @JoinColumn(foreignKey=@ForeignKey(name="FK_AGENT_TMP_REF_AGENT_COMPANY", foreignKeyDefinition="FOREIGN KEY (agent_company) REFERENCES agent_company(id)"))
    public AgentCompany getAgentCompany() {
        return agentCompany;
    }

    public void setAgentCompany(AgentCompany agentCompany) {
        this.agentCompany = agentCompany;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_agent", referencedColumnName = "id")
    public AgentTmp getParentAgent() {
        return parentAgent;
    }

    public void setParentAgent(AgentTmp parentAgent) {
        this.parentAgent = parentAgent;
    }

    @Column (columnDefinition = "VARCHAR(45)")
    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    @OneToOne
    @JoinColumn(foreignKey=@ForeignKey(name="FK_AGENT_TMP_REF_INTERNAL_USER", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES INTERNAL_USER(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column (columnDefinition = "VARCHAR(45)")
    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getRebate() {
        return rebate;
    }

    public void setRebate(Double rebate) {
        this.rebate = rebate;
    }

    @Column (columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column (columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Double caculatRebateAmount(QuoteRecord quoteRecord) {
        if (ZHONGAN_50000.getId().equals(quoteRecord.getInsuranceCompany().getId())) {
            return 0d;
        }

        Double rebateableAmount = quoteRecord.calculateRebateablePremium();
        return new BigDecimal((rebateableAmount) * rebate / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
