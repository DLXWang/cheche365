package com.cheche365.cheche.rest.model

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsurancePackage

/**
 * Created by zhengwei on 3/24/15.
 */
class QuoteQuery {
    Auto auto
    Preference pref
    InsurancePackage insurancePackage
    String quoteFlag
    Map supplementInfo
    Map additionalParameters

    Boolean isNewCarFlag() {
        if (this.additionalParameters?.supplementInfo?.newCarFlag) {
            return Boolean.TRUE
        }
        if (Auto.NEW_CAR_PLATE_NO == auto.licensePlateNo) {
            this.additionalParameters = this.additionalParameters ?: [:]
            this.additionalParameters.supplementInfo = this.additionalParameters.supplementInfo ?: [:]
            this.additionalParameters.supplementInfo.newCarFlag = Boolean.TRUE
            return Boolean.TRUE
        }
        Boolean.FALSE
    }
}
