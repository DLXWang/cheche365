package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.TideContractRebate
import com.cheche365.cheche.core.model.tide.TideRebateRecord
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString
import org.springframework.format.annotation.DateTimeFormat

@ToString
class RebateViewModel extends TideContractRebate {
    Long contractId
    String contractName
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date contractEffectiveDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date contractExpireDate
    String insuranceCompanyName
    Long supportAreaId
    String supportAreaName
    String insuranceTypeStr
    String carTypeStr
    String autoTaxReturnTypeStr
    String marketAutoTaxReturnTypeStr
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date effectiveDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date expireDate
    String statusStr
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date createTime
    Boolean contractDisable

    Integer currentPage
    Integer pageSize
    Integer draw

    static RebateViewModel buildViewData(TideContractRebate rebate) {
        new RebateViewModel(
            id: rebate.id,
            contractId: rebate.tideContract?.id,
            contractName: rebate.tideContract?.contractName,
            contractEffectiveDate: rebate.tideContract?.effectiveDate,
            contractExpireDate: rebate.tideContract?.expireDate,
            insuranceCompanyName: rebate.tideContract?.insuranceCompany?.name,
            contractRebateCode: rebate.contractRebateCode,
            supportAreaId: rebate.supportArea?.id,
            supportAreaName: rebate.supportArea?.name,
            insuranceType: rebate.insuranceType,
            insuranceTypeStr: TideConstants.INSURANCETYPE_MAP.get(rebate.insuranceType),
            carType: rebate.carType,
            carTypeStr: TideConstants.CARTYPE_MAP.get(rebate.carType),
            chooseCondition: rebate.chooseCondition,
            originalCommecialRate: rebate.originalCommecialRate,
            originalCompulsoryRate: rebate.originalCompulsoryRate,
            autoTaxReturnType: rebate.autoTaxReturnType,
            autoTaxReturnTypeStr: TideConstants.AUTOTAXRETURNTYPE_MAP.get(rebate.autoTaxReturnType),
            autoTaxReturnValue: rebate.autoTaxReturnValue,
            marketCommercialRate: rebate.marketCommercialRate,
            marketCompulsoryRate: rebate.marketCompulsoryRate,
            marketAutoTaxReturnType: rebate.marketAutoTaxReturnType,
            marketAutoTaxReturnTypeStr: TideConstants.AUTOTAXRETURNTYPE_MAP.get(rebate.marketAutoTaxReturnType),
            marketAutoTaxReturnValue: rebate.marketAutoTaxReturnValue,
            effectiveDate: rebate.effectiveDate,
            expireDate: rebate.expireDate,
            status: rebate.status,
            statusStr: TideConstants.REBATESTATUS_MAP.get(rebate.status),
            version: rebate.version,
            disable: rebate.disable,
            contractDisable: rebate.tideContract?.disable
        )
    }

    static RebateViewModel buildViewData(TideRebateRecord rebate) {
        new RebateViewModel(
            id: rebate.rebate.id,
            contractId: rebate.tideContract?.id,
            contractName: rebate.tideContract?.contractName,
            insuranceCompanyName: rebate.tideContract?.insuranceCompany?.name,
            contractRebateCode: rebate.contractRebateCode,
            supportAreaId: rebate.supportArea?.id,
            supportAreaName: rebate.supportArea?.name,
            insuranceType: rebate.insuranceType,
            insuranceTypeStr: TideConstants.INSURANCETYPE_MAP.get(rebate.insuranceType),
            carType: rebate.carType,
            carTypeStr: TideConstants.CARTYPE_MAP.get(rebate.carType),
            chooseCondition: rebate.chooseCondition,
            originalCommecialRate: rebate.originalCommecialRate,
            originalCompulsoryRate: rebate.originalCompulsoryRate,
            autoTaxReturnType: rebate.autoTaxReturnType,
            autoTaxReturnTypeStr: TideConstants.AUTOTAXRETURNTYPE_MAP.get(rebate.autoTaxReturnType),
            autoTaxReturnValue: rebate.autoTaxReturnValue,
            marketCommercialRate: rebate.marketCommercialRate,
            marketCompulsoryRate: rebate.marketCompulsoryRate,
            marketAutoTaxReturnType: rebate.marketAutoTaxReturnType,
            marketAutoTaxReturnTypeStr: TideConstants.AUTOTAXRETURNTYPE_MAP.get(rebate.marketAutoTaxReturnType),
            marketAutoTaxReturnValue: rebate.marketAutoTaxReturnValue,
            effectiveDate: rebate.effectiveDate,
            expireDate: rebate.expireDate,
            status: rebate.status,
            statusStr: TideConstants.REBATESTATUS_MAP.get(rebate.status),
            disable: rebate.disable,
            createTime: rebate.createTime
        )
    }
}
