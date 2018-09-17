package com.cheche365.cheche.core.model.tide

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InternalUser

import javax.persistence.*

/**
 * 潮汐系统: 合约点位
 * Created by yinJianBin on 2018/4/19.
 */
@Entity
class TideContractRebate extends TideBaseEntity {

    static final Integer AUTO_TAX_RETURN_TYPE_RATE = 1 //比例
    static final Integer AUTO_TAX_RETURN_TYPE_MODULUS = 2  //绝对值

    String contractRebateCode   //点位编号
    TideContract tideContract   //合约
    Area supportArea    //支持投保城市
    String insuranceType    //投保类型
    String carType  //使用性质
    String chooseCondition    //条件
    Double originalCommecialRate    //商业险原始点位
    Double originalCompulsoryRate   //交强险原始点位
    Integer autoTaxReturnType   // 车船税退税情况(比例/绝对值)
    Double autoTaxReturnValue   //车船税退税情况具体值
    Double marketCommercialRate //商业险市场投放点位
    Double marketCompulsoryRate //交强险市场投放点位
    Integer marketAutoTaxReturnType   // 车船税退税市场投放情况(比例/绝对值)
    Double marketAutoTaxReturnValue   //车船税退税市场投放情况
    Date effectiveDate //生效日期
    Date expireDate // 失效日期
    Integer status  //状态
    InternalUser operator //操作人
    int version
    Boolean disable = false// 禁用/启用


    @Column(columnDefinition = "varchar(60)")
    String getContractRebateCode() {
        return contractRebateCode
    }

    @ManyToOne
    @JoinColumn(name = "tide_contract", foreignKey = @ForeignKey(name = "FK_TIDE_CONTRACT_REF_TIDE_CONTRACT_ID", foreignKeyDefinition = "FOREIGN KEY (tide_contract) REFERENCES tide_contract(id)"))
    TideContract getTideContract() {
        return tideContract
    }

    @ManyToOne
    @JoinColumn(name = "support_area", foreignKey = @ForeignKey(name = "FK_TIDE_CONTRACT_REBATE_REF_AREA_ID", foreignKeyDefinition = "FOREIGN KEY (support_area) REFERENCES area(id)"))
    Area getSupportArea() {
        return supportArea
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

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "TIDE_CONTRACT_REBATE_OPERATOR", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    InternalUser getOperator() {
        return operator
    }

    @Version
    int getVersion() {
        return version
    }

    @Column(columnDefinition = 'tinyint(1)')
    Boolean getDisable() {
        return disable
    }

    boolean isChange(TideContractRebate rebate) {
        return [
                'effectiveDate',
                'expireDate',
                'originalCommecialRate',
                'originalCompulsoryRate',
                'autoTaxReturnType',
                'autoTaxReturnValue',
                'marketCommercialRate',
                'marketCompulsoryRate',
                'marketAutoTaxReturnType',
                'marketAutoTaxReturnValue'
        ].any {
            this[it] != rebate[it]
        }
    }
}
