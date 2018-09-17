package com.cheche365.cheche.fanhua.web.model.insurance

import com.cheche365.cheche.fanhua.annotation.Essential

/**
 * Created by zhangtc on 2017/11/30.
 */
class RecordInsuranceViewModel {

    String taxCharge
    AgentInfoViewModel agentInfo
    String proposeNum
    @Essential
    String expiryDate
    CarInfoViewModel carInfo
    InsuredPersonInfoViewModel insuredPersonInfo
    @Essential
    String charge
    String buyRist
    String insCorpCode
    List<Map> agencyOrgList
    @Essential
    String suiteCode
    List<Map> suite
    @Essential
    String policyNum
    @Essential
    String effectiveDate
}
