package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 缓存报价记录表，包含电销报价和传统报价
 * Created by sunhuazhong on 2016/3/16.
 */
@Entity
public class QuoteRecordCache {
    private Long id;
    private int type = 1;// 报价类型，1-电销报价，2-传统报价，3-修改报价
    private QuoteRecord quoteRecord;//报价记录
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人
    private InsuranceCompany insuranceCompany;//保险公司
    private String policyDescription;//政策描述性文本
    private QuoteModification quoteModification;//修改报价


    //非持久化字段
    private String strQuoteSource;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "tinyint(1)")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @ManyToOne
    @JoinColumn(name = "quoteRecord", foreignKey=@ForeignKey(name="FK_QUOTE_RECORD_CACHE_REF_QUOTE_RECORD", foreignKeyDefinition="FOREIGN KEY (quote_record) REFERENCES quote_record(id)"))
    public QuoteRecord getQuoteRecord() {
        return quoteRecord;
    }

    public void setQuoteRecord(QuoteRecord quoteRecord) {
        this.quoteRecord = quoteRecord;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="FK_QUOTE_RECORD_CACHE_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey=@ForeignKey(name="FK_QUOTE_RECORD_CACHE_REF_INSURANCE_COMPANY", foreignKeyDefinition="FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @Column(columnDefinition = "VARCHAR(1000)")
    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    @ManyToOne
    @JoinColumn(name = "quoteModification", foreignKey=@ForeignKey(name="FK_QUOTE_RECORD_CACHE_REF_QUOTE_MODIFICATION", foreignKeyDefinition="FOREIGN KEY (quote_modification) REFERENCES quote_modification(id)"))
    public QuoteModification getQuoteModification() {
        return quoteModification;
    }

    public void setQuoteModification(QuoteModification quoteModification) {
        this.quoteModification = quoteModification;
    }

    @Transient
    public String getStrQuoteSource() {
        return strQuoteSource;
    }

    public void setStrQuoteSource(String strQuoteSource) {
        this.strQuoteSource = strQuoteSource;
    }
}
