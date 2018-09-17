package com.cheche365.cheche.core.model.abao;

import com.cheche365.cheche.core.model.abao.InsuranceProduct;

import javax.persistence.*;

@Entity
@Table(name = "INSURANCE_PRODUCT_FIELD")
public class InsuranceProductField {
    private Long id; // 主键
    private InsuranceProduct insuranceProduct; // 保险产品id
    private InsuranceField insuranceField; // 险种id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_PRODUCT", foreignKey = @ForeignKey(name = "FK_PRODUCT_FIELD_REF_INSURANCE_PRODUCT", foreignKeyDefinition = "FOREIGN KEY (`insurance_product`) REFERENCES `insurance_product` (`id`)"))
    public InsuranceProduct getInsuranceProduct() {
        return insuranceProduct;
    }

    public void setInsuranceProduct(InsuranceProduct insuranceProduct) {
        this.insuranceProduct = insuranceProduct;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_FIELD", foreignKey = @ForeignKey(name = "FK_PRODUCT_FIELD_REF_INSURANCE_FIELD", foreignKeyDefinition = "FOREIGN KEY (`insurance_field`) REFERENCES `insurance_field` (`id`)"))
    public InsuranceField getInsuranceField() {
        return insuranceField;
    }

    public void setInsuranceField(InsuranceField insuranceField) {
        this.insuranceField = insuranceField;
    }
}
