package com.cheche365.cheche.core.model.abao;

import com.cheche365.cheche.core.model.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "INSURANCE_POLICY")
public class InsurancePolicy {

    private Long id; // 主键
    private InsuranceQuote insuranceQuote; // 报价id
    private PurchaseOrder purchaseOrder; // 订单id
    private User user; // 用户id
    private InsuranceCompany insuranceCompany; // 保险公司id
    private String proposalNo; // 投保单号
    private String policyNo; //  保单号
    private Date effectiveDate; // 生效日期
    private Date expireDate; // 失效日期
    private Integer effectiveHour; // 生效小时
    private Integer expireHour; // 过期小时
    private BigDecimal premium; // 保费
    private InsurancePerson insuredPerson; // 被保险人id
    private InsurancePerson applicantPerson; // 投保人姓名
    private String insuranceImage; // 保单扫描文件地址
    private InsuranceAgent insuranceAgent; // 保险代理机构
    private Institution institution; // 出单机构
    private InternalUser operator; // 内部操作人
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private BigDecimal discount; // 折扣率

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_QUOTE", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_QUOTE", foreignKeyDefinition = "FOREIGN KEY (`insurance_quote`) REFERENCES `insurance_quote` (`id`)"))
    public InsuranceQuote getInsuranceQuote() {
        return this.insuranceQuote;
    }

    public void setInsuranceQuote(InsuranceQuote insuranceQuote) {
        this.insuranceQuote = insuranceQuote;
    }

    @ManyToOne
    @JoinColumn(name = "PURCHASE_ORDER", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_ORDER", foreignKeyDefinition = "FOREIGN KEY (`purchase_order`) REFERENCES `purchase_order` (`id`)"))
    public PurchaseOrder getPurchaseOrder() {
        return this.purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    @ManyToOne
    @JoinColumn(name = "USER", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_USER", foreignKeyDefinition = "FOREIGN KEY (`user`) REFERENCES `user` (`id`)"))
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_COMPANY", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_COMPANY", foreignKeyDefinition = "FOREIGN KEY (`insurance_company`) REFERENCES `insurance_company` (`id`)"))
    public InsuranceCompany getInsuranceCompany() {
        return this.insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getProposalNo() {
        return this.proposalNo;
    }

    public void setProposalNo(String proposalNo) {
        this.proposalNo = proposalNo;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getPolicyNo() {
        return this.policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    @Column(columnDefinition = "DATE")
    public Date getEffectiveDate() {
        return this.effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Column(columnDefinition = "DATE")
    public Date getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Column(columnDefinition = "tinyint(3)")
    public Integer getEffectiveHour() {
        return this.effectiveHour;
    }

    public void setEffectiveHour(Integer effectiveHour) {
        this.effectiveHour = effectiveHour;
    }

    @Column(columnDefinition = "tinyint(3)")
    public Integer getExpireHour() {
        return this.expireHour;
    }

    public void setExpireHour(Integer expireHour) {
        this.expireHour = expireHour;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public BigDecimal getPremium() {
        return this.premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    @Column(columnDefinition = "VARCHAR(200)")
    public String getInsuranceImage() {
        return this.insuranceImage;
    }

    public void setInsuranceImage(String insuranceImage) {
        this.insuranceImage = insuranceImage;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_AGENT", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_AGENT", foreignKeyDefinition = "FOREIGN KEY (`insurance_agent`) REFERENCES `insurance_agent` (`id`)"))
    public InsuranceAgent getInsuranceAgent() {
        return this.insuranceAgent;
    }

    public void setInsuranceAgent(InsuranceAgent insuranceAgent) {
        this.insuranceAgent = insuranceAgent;
    }

    @ManyToOne
    @JoinColumn(name = "INSTITUTION", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_INSTITUTION", foreignKeyDefinition = "FOREIGN KEY (`institution`) REFERENCES `institution` (`id`)"))
    public Institution getInstitution() {
        return this.institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    @ManyToOne
    @JoinColumn(name = "OPERATOR", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (`operator`) REFERENCES `internal_user` (`id`)"))
    public InternalUser getOperator() {
        return this.operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(columnDefinition = "DECIMAL(18,2)")
    public BigDecimal getDiscount() {
        return this.discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    @ManyToOne
    @JoinColumn(name = "insured_person", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_INSURANCE_PERSON1", foreignKeyDefinition = "FOREIGN KEY (`insured_person`) REFERENCES `insurance_person` (`id`)"))
    public InsurancePerson getInsuredPerson() {
        return insuredPerson;
    }

    public void setInsuredPerson(InsurancePerson insuredPerson) {
        this.insuredPerson = insuredPerson;
    }

    @ManyToOne
    @JoinColumn(name = "applicant_person", foreignKey = @ForeignKey(name = "FK_INSURANCE_POLICY_REF_INSURANCE_PERSON2", foreignKeyDefinition = "FOREIGN KEY (`applicant_person`) REFERENCES `insurance_person` (`id`)"))
    public InsurancePerson getApplicantPerson() {
        return applicantPerson;
    }

    public void setApplicantPerson(InsurancePerson applicantPerson) {
        this.applicantPerson = applicantPerson;
    }

    public static class Enum {
        public static final String  PRE_EFFECTIVE="1";
        public static final String  EFFECTIVE="2";
        public static final String  EXPIRED="3";
    }

}
