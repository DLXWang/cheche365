package com.cheche365.cheche.operationcenter.web.model.tide

import com.cheche365.cheche.core.model.tide.TideContract
import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.ToString
/**
 * Created by yinJianBin on 2017/6/13.
 */
@ToString
class ContractViewModel {
    Long id
    String contractName
    def platformId
    String platformName
    Long branchId
    String branchName
    def institutionId
    String institutionName
    def insuranceCompanyId
    String insuranceCompanyName
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date effectiveDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    Date expireDate
    String partnerUserName
    String partnerPassword
    String orderCode
    String description
    def fileIds
    def status
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    def createTime
    def operator
    Long[] cityIds
    Long areaId
    def areaName
    Long statusId
    String loginUrl
    def areaViewModel
    def fileViewModel
    String contractCode
    Boolean disable
    def historyViewModel


    Integer currentPage
    Integer pageSize
    Integer draw

    static ContractViewModel buildViewData(TideContract contract) {
        def model = new ContractViewModel(
                id: contract.id,
                contractName: contract.contractName,
                platformName: contract.tideBranch.tidePlatform?.name,
                branchName: contract.tideBranch?.branchName,
                institutionName: contract.tideInstitution?.institutionName,
                insuranceCompanyName: contract.insuranceCompany?.name,
                description: contract.description,
                loginUrl: contract.loginUrl,
                partnerUserName: contract.partnerUserName,
                partnerPassword: contract.partnerPassword,
                orderCode: contract.orderCode,
                operator: contract.operator?.name,
                createTime: contract.createTime,
                contractCode: contract.contractCode ?: "",
                disable: contract.disable
        )
        model
    }

}
