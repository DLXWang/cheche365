package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangfei on 2016/5/5.
 */
@Entity
public class QuoteModification {
    private Long id;
    private OcQuoteSource quoteSource;//出单中心报价来源
    private Long quoteSourceId;// 来源ID
    private String insuranceCompanyIds;//选择保险公司集合
    private InsurancePackage insurancePackage;
    private Date createTime;//创建时间
    private Date updateTime;//修改时间

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "quoteSource", foreignKey=@ForeignKey(name="FK_QUOTE_SOURCE_REF_OC_QUOTE_SOURCE", foreignKeyDefinition="FOREIGN KEY (quote_source) REFERENCES quote_source(id)"))
    public OcQuoteSource getQuoteSource() {
        return quoteSource;
    }

    public void setQuoteSource(OcQuoteSource quoteSource) {
        this.quoteSource = quoteSource;
    }

    @Column
    public Long getQuoteSourceId() {
        return quoteSourceId;
    }

    public void setQuoteSourceId(Long quoteSourceId) {
        this.quoteSourceId = quoteSourceId;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    public String getInsuranceCompanyIds() {
        return insuranceCompanyIds;
    }

    public void setInsuranceCompanyIds(String insuranceCompanyIds) {
        this.insuranceCompanyIds = insuranceCompanyIds;
    }

    @ManyToOne
    @JoinColumn(name = "insurancePackage", foreignKey=@ForeignKey(name="FK_INSURANCE_PACKAGE_REF_INSURANCE_PACKAGE", foreignKeyDefinition="FOREIGN KEY (insurance_package) REFERENCES insurance_package(id)"))
    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
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
}
