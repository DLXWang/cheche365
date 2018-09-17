package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.TideContractRebateHistory
import com.cheche365.cheche.manage.common.constants.TideConstants
import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString

@ToString
class RebateHistoryViewModel {
    Long id
    Long rebateId
    Long contractId
    String contractName
    String insuranceCompanyName
    String contractRebateCode
    Long supportAreaId
    String supportAreaName
    String insuranceType
    String carType
    String chooseCondition
    Double originalCommecialRate
    Double originalCompulsoryRate
    Integer autoTaxReturnType
    String autoTaxReturnTypeStr
    Double autoTaxReturnValue
    Double marketCommercialRate
    Double marketCompulsoryRate
    Integer marketAutoTaxReturnType
    String marketAutoTaxReturnTypeStr
    Double marketAutoTaxReturnValue
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date effectiveDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date expireDate
    String status
    String modifyer
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime

    Integer currentPage
    Integer pageSize
    Integer draw

    static RebateHistoryViewModel buildViewData(TideContractRebateHistory rebateHis) {
        new RebateHistoryViewModel(
            id: rebateHis.id,
            rebateId: rebateHis.contractRebate,
            contractId: rebateHis.tideContract?.id,
            contractName: rebateHis.tideContract?.contractName,
            insuranceCompanyName: rebateHis.tideContract?.insuranceCompany?.name,
            contractRebateCode: rebateHis.contractRebateCode,
            supportAreaId: rebateHis.supportArea?.id,
            supportAreaName: rebateHis.supportArea?.name,
            insuranceType: TideConstants.INSURANCETYPE_MAP.get(rebateHis.insuranceType),
            carType: TideConstants.CARTYPE_MAP.get(rebateHis.carType),
            chooseCondition: rebateHis.chooseCondition,
            originalCommecialRate: rebateHis.originalCommecialRate,
            originalCompulsoryRate: rebateHis.originalCompulsoryRate,
            autoTaxReturnType: rebateHis.autoTaxReturnType,
            autoTaxReturnTypeStr: TideConstants.AUTOTAXRETURNTYPE_MAP.get(rebateHis.autoTaxReturnType),
            autoTaxReturnValue: rebateHis.autoTaxReturnValue,
            marketCommercialRate: rebateHis.marketCommercialRate,
            marketCompulsoryRate: rebateHis.marketCompulsoryRate,
            marketAutoTaxReturnType: rebateHis.marketAutoTaxReturnType,
            marketAutoTaxReturnTypeStr: TideConstants.AUTOTAXRETURNTYPE_MAP.get(rebateHis.marketAutoTaxReturnType),
            marketAutoTaxReturnValue: rebateHis.marketAutoTaxReturnValue,
            effectiveDate: rebateHis.effectiveDate,
            expireDate: rebateHis.expireDate,
            status: "已失效",
            modifyer: rebateHis.modifyer.name,
            createTime:rebateHis.createTime
        )
    }
}
