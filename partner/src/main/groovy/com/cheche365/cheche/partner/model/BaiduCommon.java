package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.partner.config.app.Constant;
import com.sun.jersey.api.representation.Form;

import java.util.List;

/**
 * Created by zhengwei on 2/20/16.
 */
public abstract class BaiduCommon extends Form {

    public abstract List<String> getSignFields();

    public void setPartner_id(String partner_id) {
        this.putSingle("partner_id", partner_id);
    }

    public void setPartner_id(InsuranceCompany company) {
        this.putSingle("partner_id", toPartnerId(company));
    }

    public void setSign(String sign) {
        this.putSingle("sign", sign);
    }

    public String toPartnerId(InsuranceCompany company){
        return Constant.BAIDU_PARTNER_ID + "-" + company.getId();
    }
}
