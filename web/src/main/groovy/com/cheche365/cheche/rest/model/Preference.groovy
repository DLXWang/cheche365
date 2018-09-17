package com.cheche365.cheche.rest.model

import com.cheche365.cheche.core.model.InsuranceCompany
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import static com.cheche365.cheche.core.model.InsuranceCompany.allCompanies

/**
 * Created by zhengwei on 3/24/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Preference {

    private Boolean serverPush
    private List<Long> companyIds
    private String uuid
    private Integer flowType // 1:新投保、转保 2:续保
    private Long insuranceAreaId //行驶城市
    private Double discount  //报价折扣系数

    List<Long> getCompanyIds() {
        return companyIds
    }

    void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds
    }

    boolean isServerPush() {
        return serverPush
    }

    void setServerPush(Boolean serverPush) {
        this.serverPush = serverPush
    }

    Boolean getServerPush() {
        return serverPush
    }

    String getUuid() {
        return uuid
    }

    void setUuid(String uuid) {
        this.uuid = uuid
    }

    Integer getFlowType() {
        return flowType
    }

    void setFlowType(Integer flowType) {
        this.flowType = flowType
    }

    Long getInsuranceAreaId() {
        return insuranceAreaId
    }

    void setInsuranceAreaId(Long insuranceAreaId) {
        this.insuranceAreaId = insuranceAreaId
    }

    Double getDiscount() {
        return discount
    }

    void setDiscount(Double discount) {
        this.discount = discount
    }

    List<InsuranceCompany> getInsuranceCompanies() {
        companyIds ? allCompanies().findAll { companyIds.contains(it.id) } : allCompanies()
    }

}
