package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.annotation.EqualsField
import com.cheche365.cheche.core.annotation.EqualsFiledHandler
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import groovy.transform.Canonical
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

import javax.persistence.*

@Entity
@Canonical()
class AutoType implements Serializable {
    private static final long serialVersionUID = 1L

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id
    private String brand //品牌
    private String brandLogo // 品牌logo
    private Double newPrice //新车价格
    @EqualsField
    private Integer seats //座位数
    private Double currentPrice //当前价格（折旧）
    @EqualsField
    private String code
    private String name
    private String manufacturer
    private String description
    private String family  //车系
    private String group   //车组
    private String model   //车型
    private Double exhaustScale
    private String logo    //车型logo
    private String logoHash
    private String logoUrl //url


    private SupplementInfo supplementInfo

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Column(columnDefinition = "VARCHAR(45)")
    @JsonIgnore()
    String getBrand() {
        return brand
    }

    void setBrand(String brand) {
        this.brand = brand
    }

    @Column(columnDefinition = "VARCHAR(45)")
    @JsonIgnore()
    String getBrandLogo() {
        return brandLogo
    }

    void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    @JsonIgnore()
    Double getNewPrice() {
        return newPrice
    }

    void setNewPrice(Double newPrice) {
        this.newPrice = newPrice
    }

    @Column(columnDefinition = "TINYINT(3)")
    Integer getSeats() {
        return seats
    }

    void setSeats(Integer seats) {
        this.seats = seats
    }
    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    @JsonIgnore()
    Double getCurrentPrice() {
        return currentPrice
    }

    void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getCode() {
        return code
    }

    void setCode(String code) {
        this.code = code
    }

    @Column(columnDefinition = "VARCHAR(45)")
    @JsonIgnore()
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Column(columnDefinition = "VARCHAR(45)")
    @JsonIgnore()
    String getManufacturer() {
        return manufacturer
    }

    void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    @JsonIgnore()
    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    void setFamily(String family) {
        this.family = family
    }

    @Column(columnDefinition = "VARCHAR(60)")
    @JsonIgnore()
    String getFamily() {
        return family
    }

    void setGroup(String group) {
        this.group = group
    }

    @Column(name="auto_group", columnDefinition = "VARCHAR(120)")
    @JsonIgnore()
    String getGroup() {
        return group
    }


    void setModel(String model) {
        this.model = model
    }

    @Column(columnDefinition = "VARCHAR(120)")
    String getModel() {
        return model
    }

    void setExhaustScale(Double exhaustScale) {
        this.exhaustScale = exhaustScale
    }
    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "VARCHAR(10)")
    @JsonIgnore()
    Double getExhaustScale() {
        return exhaustScale
    }

    @Column(columnDefinition = "VARCHAR(200)")
    @JsonIgnore()
    String getLogo() {
        return logo
    }

    void setLogo(String logo) {
        this.logo = logo
    }

    @JsonIgnore
    @Transient

    String getLogoHash() {
        return logoHash
    }

    void setLogoHash(String logoHash) {
        this.logoHash = logoHash
    }

    @Transient
    @JsonIgnore()
    String getLogoUrl() {
        return logoUrl
    }

    void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl
    }

    @Transient
    SupplementInfo getSupplementInfo() {
        return supplementInfo
    }

    void setSupplementInfo(SupplementInfo supplementInfo) {
        this.supplementInfo = supplementInfo
    }

    @Override
    String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
    }

    @Override
    boolean equals(Object obj) {

        if(obj ==this){return true}

        if(obj==null&&!getClass().equals(obj.getClass())){return  false}

        return EqualsFiledHandler.objectEquals(obj,this)
    }

}

