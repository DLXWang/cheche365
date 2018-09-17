package com.cheche365.cheche.externalpayment.model

import com.cheche365.cheche.core.service.OrderRelatedService

abstract class BaseCallbackBody {

    abstract String ciPolicyNo()
    abstract String ciProposalNo()
    abstract Date ciStartDate()
    abstract Date ciEndDate()

    abstract String insurancePolicyNo()
    abstract String insuranceProposalNo()
    abstract Date insuranceStartDate()
    abstract Date insuranceEndDate()


    void syncBillNos(OrderRelatedService.OrderRelated or) {
        if (or.ci) {
            or.ci.policyNo = ciPolicyNo() ?: or.ci.policyNo
            or.ci.proposalNo = ciProposalNo() ?: or.ci.proposalNo
            or.ci.effectiveDate = ciStartDate() ?: or.ci.effectiveDate
            or.ci.expireDate = ciEndDate() ?: or.ci.expireDate
            or.toBePersist << or.ci
        }

        if (or.insurance) {
            or.insurance.policyNo = insurancePolicyNo() ?: or.insurance.policyNo
            or.insurance.proposalNo = insuranceProposalNo() ?: or.insurance.proposalNo
            or.insurance.effectiveDate = insuranceStartDate() ?: or.insurance.effectiveDate
            or.insurance.expireDate = insuranceEndDate() ?:  or.insurance.expireDate
            or.toBePersist << or.insurance
        }
    }
}
