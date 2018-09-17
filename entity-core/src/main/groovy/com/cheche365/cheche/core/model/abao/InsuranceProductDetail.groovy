package com.cheche365.cheche.core.model.abao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
@JsonIgnoreProperties(value = ["id", "insuranceProduct"])
class InsuranceProductDetail {

    private Long id;
    private InsuranceProduct insuranceProduct;
    private InsuranceProductDetailName detailName;
    private String value;
    private Boolean display;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @ManyToOne
    @JoinColumn(name = "insurance_product", foreignKey = @ForeignKey(name = "FK_PRODUCT_DETAIL_REF_INSURANCE_PRODUCT", foreignKeyDefinition = "FOREIGN KEY (`daily_insurance`) REFERENCES `daily_insurance` (`id`)"))
    InsuranceProduct getInsuranceProduct() {
        return insuranceProduct
    }

    void setInsuranceProduct(InsuranceProduct insuranceProduct) {
        this.insuranceProduct = insuranceProduct
    }

    @ManyToOne
    @JoinColumn(name = "detail_name", foreignKey = @ForeignKey(name = "FK_PRODUCT_DETAIL_REF_DETAIL_KEY", foreignKeyDefinition = "FOREIGN KEY (`detail_name`) REFERENCES `insurance_product_detail_name` (`id`)"))
    InsuranceProductDetailName getDetailName() {
        return detailName
    }

    void setDetailName(InsuranceProductDetailName detailName) {
        this.detailName = detailName
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getValue() {
        return value
    }

    void setValue(String value) {
        this.value = value
    }

    @Column(columnDefinition = "TINYINT")
    Boolean getDisplay() {
        return display
    }

    void setDisplay(Boolean display) {
        this.display = display
    }

    public static class Enum {
        public static Boolean allowDisplayDetail(InsuranceProductDetail productDetail) {
            if (productDetail && ["承保年龄", "承保期限", "保障特色"].contains(productDetail.detailName.description)) {
                return Boolean.TRUE
            }
            return Boolean.FALSE
        }
    }

}
