package com.cheche365.cheche.core.model.abao;

import com.cheche365.cheche.core.model.abao.InsuranceProduct;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "INSURANCE_QUOTE_PERSON")
public class InsuranceQuotePerson {

    private Long id;// 主键
    private InsuranceProduct insuranceProduct;// 保险产品id
    private InsuranceQuote insuranceQuote;// 报价id
    private InsurancePerson insurancePerson;// 被保险人id
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_PRODUCT", foreignKey = @ForeignKey(name = "FK_QUOTE_PERSON_REF_INSURANCE_PRODUCT", foreignKeyDefinition = "FOREIGN KEY (`insurance_product`) REFERENCES `insurance_product` (`id`)"))
    public InsuranceProduct getInsuranceProduct() {
        return this.insuranceProduct;
    }

    public void setInsuranceProduct(InsuranceProduct insuranceProduct) {
        this.insuranceProduct = insuranceProduct;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_QUOTE", foreignKey = @ForeignKey(name = "FK_QUOTE_PERSON_REF_INSURANCE_QUOTE", foreignKeyDefinition = "FOREIGN KEY (`insurance_quote`) REFERENCES `insurance_quote` (`id`)"))
    public InsuranceQuote getInsuranceQuote() {
        return this.insuranceQuote;
    }

    public void setInsuranceQuote(InsuranceQuote insuranceQuote) {
        this.insuranceQuote = insuranceQuote;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_PERSON", foreignKey = @ForeignKey(name = "FK_QUOTE_PERSON_REF_INSURANCE_PERSON", foreignKeyDefinition = "FOREIGN KEY (`insurance_person`) REFERENCES `insurance_person` (`id`)"))
    public InsurancePerson getInsurancePerson() {
        return this.insurancePerson;
    }

    public void setInsurancePerson(InsurancePerson insurancePerson) {
        this.insurancePerson = insurancePerson;
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
}
