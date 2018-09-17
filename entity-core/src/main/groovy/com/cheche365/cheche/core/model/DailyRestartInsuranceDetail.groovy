package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize

import javax.persistence.*

/**
 * Created by mahong on 2016/12/23.
 * 按天买车险-提前复驶 险种详情
 */
@Entity
@JsonIgnoreProperties(["id", "dailyRestartInsurance"])
class DailyRestartInsuranceDetail {
    private Long id;                   // 主键
    private DailyRestartInsurance dailyRestartInsurance;  // 按天买车险复驶id
    private String code;               // 险别代码
    private String name;               // 险别名称
    private Double amount;             // 保额
    private Double premium;            // 保费
    private Boolean iop;               // 免赔标志
    private Double iopPremium;         // 免赔保费

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @ManyToOne
    @JoinColumn(name = "daily_restart_insurance", foreignKey = @ForeignKey(name = "FK_RESTART_INSURANCE_DETAIL_REF_RESTART_INSURANCE", foreignKeyDefinition = "FOREIGN KEY (`daily_restart_insurance`) REFERENCES `daily_restart_insurance` (`id`)"))
    DailyRestartInsurance getDailyRestartInsurance() {
        return dailyRestartInsurance
    }

    void setDailyRestartInsurance(DailyRestartInsurance dailyRestartInsurance) {
        this.dailyRestartInsurance = dailyRestartInsurance
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

    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getAmount() {
        return amount
    }

    void setAmount(Double amount) {
        this.amount = amount
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getPremium() {
        return premium
    }

    void setPremium(Double premium) {
        this.premium = premium
    }

    @Column(columnDefinition = "tinyint(1)")
    Boolean getIop() {
        return iop
    }

    void setIop(Boolean iop) {
        this.iop = iop
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    Double getIopPremium() {
        return iopPremium
    }

    void setIopPremium(Double iopPremium) {
        this.iopPremium = iopPremium
    }
}
