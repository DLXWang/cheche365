package com.cheche365.cheche.core.model.abao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.*

/**
 * Created by wangjiahuan on 2016/11/16 0016.
 */
@Entity
@JsonIgnoreProperties(value = ["id", "insuranceProduct"])
class InsuranceProductTag {

    private Long id;
    private InsuranceProduct insuranceProduct;
    private TagType tagType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @ManyToOne
    @JoinColumn(name = "insurance_product", foreignKey = @ForeignKey(name = "FK_PRODUCT_TAG_REF_INSURANCE_PRODUCT", foreignKeyDefinition = "FOREIGN KEY (`insurance_product`) REFERENCES `insurance_product` (`id`)"))
    InsuranceProduct getInsuranceProduct() {
        return insuranceProduct
    }

    void setInsuranceProduct(InsuranceProduct insuranceProduct) {
        this.insuranceProduct = insuranceProduct
    }

    @ManyToOne
    @JoinColumn(name = "tag_type", foreignKey = @ForeignKey(name = "FK_PRODUCT_TAG_REF_TAG_TYPE", foreignKeyDefinition = "FOREIGN KEY (`tag_type`) REFERENCES `tag_type` (`id`)"))
    TagType getTagType() {
        return tagType
    }

    void setTagType(TagType tagType) {
        this.tagType = tagType
    }
}
