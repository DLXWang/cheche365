package com.cheche365.cheche.ordercenter.model;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.InsurancePackage;
import com.cheche365.cheche.core.model.SupplementInfo;

/**
 * Created by zhengwei on 3/24/15.
 */

public class QuoteQuery {
    private Auto auto;
    private Preference pref;
    private InsurancePackage insurancePackage;
    private String source;
    private Long sourceId;
    private String sourceIdStr;
    private Long userId;
    private AdditionalParameters additionalParameters;

    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public Auto getAuto() {
        return auto;
    }

    public void setAuto(Auto auto) {
        this.auto = auto;
    }

    public Preference getPref() {
        return pref;
    }

    public void setPref(Preference pref) {
        this.pref = pref;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceIdStr() {
        return sourceIdStr;
    }

    public void setSourceIdStr(String sourceIdStr) {
        this.sourceIdStr = sourceIdStr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AdditionalParameters getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(AdditionalParameters additionalParameters) {
        this.additionalParameters = additionalParameters;
    }
    public class AdditionalParameters{
        private SupplementInfo supplementInfo;

        public SupplementInfo getSupplementInfo() {
            return supplementInfo;
        }

        public void setSupplementInfo(SupplementInfo supplementInfo) {
            this.supplementInfo = supplementInfo;
        }
    }


}
