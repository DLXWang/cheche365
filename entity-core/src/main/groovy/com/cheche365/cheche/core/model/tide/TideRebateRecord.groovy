package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.Area

import javax.persistence.*

/**
 * 潮汐系统: 合约点位记录
 * Created by wanglei on 2018/05/03.
 */
@Entity
class TideRebateRecord implements Serializable {
    private static final long serialVersionUID = 1L

    Long id
    // 关联点位
    TideContractRebate rebate
    // 点位编号
    String contractRebateCode
    // 合约
    TideContract tideContract
    // 支持投保城市
    Area supportArea
    // 投保类型
    String insuranceType
    // 使用性质
    String carType
    // 条件
    String chooseCondition
    // 商业险原始点位
    Double originalCommecialRate
    // 交强险原始点位
    Double originalCompulsoryRate
    // 车船税退税情况(比例/绝对值)
    Integer autoTaxReturnType
    // 车船税退税情况具体值
    Double autoTaxReturnValue
    // 商业险市场投放点位
    Double marketCommercialRate
    // 交强险市场投放点位
    Double marketCompulsoryRate
    // 车船税退税市场投放情况(比例/绝对值)
    Integer marketAutoTaxReturnType
    // 车船税退税市场投放情况
    Double marketAutoTaxReturnValue
    // 生效日期
    Date effectiveDate
    // 失效日期
    Date expireDate
    // 状态
    Integer status
    Date createTime
    String description
    // 禁用/启用
    Boolean disable = false

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId() {
        return id
    }

    @ManyToOne
    @JoinColumn(name = "rebate", foreignKey = @ForeignKey(name = "FK_REBATE_RECORD_REF_REBATE", foreignKeyDefinition = "FOREIGN KEY (rebate) REFERENCES tide_contract_rebate(id)"))
    TideContractRebate getRebate() {
        return rebate
    }

    @ManyToOne
    @JoinColumn(name = "tide_contract", foreignKey = @ForeignKey(name = "FK_REBATE_RECORD_REF_CONTRACT", foreignKeyDefinition = "FOREIGN KEY (tide_contract) REFERENCES tide_contract(id)"))
    TideContract getTideContract() {
        return tideContract
    }

    @ManyToOne
    @JoinColumn(name = "support_area", foreignKey = @ForeignKey(name = "FK_REBATE_RECORD_REF_AREA", foreignKeyDefinition = "FOREIGN KEY (support_area) REFERENCES area(id)"))
    Area getSupportArea() {
        return supportArea
    }

    @Column(columnDefinition = "varchar(60)")
    String getContractRebateCode() {
        return contractRebateCode
    }

    @Column(columnDefinition = "varchar(60)")
    String getInsuranceType() {
        return insuranceType
    }


    @Column(columnDefinition = "varchar(60)")
    String getCarType() {
        return carType
    }


    @Column(columnDefinition = "varchar(1000)")
    String getChooseCondition() {
        return chooseCondition
    }


    @Column(columnDefinition = "decimal(18, 4)")
    Double getOriginalCommecialRate() {
        return originalCommecialRate
    }


    @Column(columnDefinition = "decimal(18, 4)")
    Double getOriginalCompulsoryRate() {
        return originalCompulsoryRate
    }


    @Column(columnDefinition = "tinyint(1)")
    Integer getAutoTaxReturnType() {
        return autoTaxReturnType
    }


    @Column(columnDefinition = "decimal(18, 4)")
    Double getAutoTaxReturnValue() {
        return autoTaxReturnValue
    }


    @Column(columnDefinition = "decimal(18, 4)")
    Double getMarketCommercialRate() {
        return marketCommercialRate
    }


    @Column(columnDefinition = "decimal(18, 4)")
    Double getMarketCompulsoryRate() {
        return marketCompulsoryRate
    }


    @Column(columnDefinition = "tinyint(1)")
    Integer getMarketAutoTaxReturnType() {
        return marketAutoTaxReturnType
    }


    @Column(columnDefinition = "decimal(18, 4)")
    Double getMarketAutoTaxReturnValue() {
        return marketAutoTaxReturnValue
    }


    @Column(columnDefinition = "datetime")
    Date getEffectiveDate() {
        return effectiveDate
    }


    @Column(columnDefinition = "datetime")
    Date getExpireDate() {
        return expireDate
    }


    @Column(columnDefinition = "tinyint(1)")
    Integer getStatus() {
        return status
    }

    @Column(columnDefinition = "DATETIME")
    Date getCreateTime() {
        return createTime
    }

    @Column(columnDefinition = "VARCHAR(2000)")
    String getDescription() {
        return description
    }

    @Column(columnDefinition = 'tinyint(1)')
    Boolean getDisable() {
        return disable
    }

    @PrePersist
    void onCreate() {
        this.setCreateTime((new Date()))
    }
}
