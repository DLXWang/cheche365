package com.cheche365.cheche.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * 出单机构佣金表 temp
 * Created by sunhuazhong on 2015/11/13.
 */
@Entity
public class InstitutionRebateTemp {
    private Long id;
    private InstitutionTemp institutionTemp;//出单机构
    private Area area;//城市
    private InsuranceCompany insuranceCompany;//保险公司
    private Double commercialRebate;//商业险佣金
    private Double compulsoryRebate;//交强险佣金
    private Date createTime;//创建时间
    private Date updateTime;//修改时间
    private InternalUser operator;//操作人

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "institutionTemp", foreignKey=@ForeignKey(name="`FK_INSTITUTION_REBATE_TEMP_REF_INSTITUTION_TEMP", foreignKeyDefinition="FOREIGN KEY (institution_temp) REFERENCES institution_temp(id)"))
    public InstitutionTemp getInstitutionTemp() {
        return institutionTemp;
    }

    public void setInstitutionTemp(InstitutionTemp institutionTemp) {
        this.institutionTemp = institutionTemp;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="`FK_INSTITUTION_REBATE_TEMP_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey=@ForeignKey(name="`FK_INSTITUTION_REBATE_TEMP_REF_INSURANCE_COMPANY", foreignKeyDefinition="FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    @Column(columnDefinition = "datetime")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(columnDefinition = "datetime")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey=@ForeignKey(name="`FK_INSTITUTION_REBATE_TEMP_REF_OPERATOR", foreignKeyDefinition="FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }
}
