package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import javax.persistence.*

/**
 * Created by mahong on 2016/11/29.
 * 按天买车险-停驶 险种详情
 */
@Entity
@JsonIgnoreProperties(["dailyInsurance"])
class DailyInsuranceDetail {

    private Long id;                   // 主键
    private DailyInsurance dailyInsurance;  // 按天买车险停复驶id
    private String code;               // 险别代码
    private String name;               // 险别名称
    private Double refundPremium;      // 应退保费

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @ManyToOne
    @JoinColumn(name = "daily_insurance", foreignKey = @ForeignKey(name = "FK_INSURANCE_DETAIL_REF_DAILY_INSURANCE", foreignKeyDefinition = "FOREIGN KEY (`daily_insurance`) REFERENCES `daily_insurance` (`id`)"))
    DailyInsurance getDailyInsurance() {
        return dailyInsurance
    }

    void setDailyInsurance(DailyInsurance dailyInsurance) {
        this.dailyInsurance = dailyInsurance
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getCode() {
        return code
    }

    void setCode(String code) {
        this.code = code
    }

    @Column(columnDefinition = "VARCHAR(50)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Transient
    boolean getWithIop() {
        if(refundPremium>0.0d&&!ArrayFieldsGenerator.FIELDS_WITHOUT_IOP.contains(code)){
            return true;
        }else{
            return false;
        }
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getRefundPremium() {
        return refundPremium
    }

    void setRefundPremium(Double refundPremium) {
        this.refundPremium = refundPremium
    }
}
