package com.cheche365.cheche.ordercenter.web.model;

import com.cheche365.cheche.core.model.InsuranceCompany;

/**
 * 保险公司
 * Created by sunhuazhong on 2015/7/23.
 */
public class InsuranceCompanyData {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static InsuranceCompanyData createViewModel(InsuranceCompany insuranceCompany) {
        if (null == insuranceCompany) {
            return null;
        }
        InsuranceCompanyData data = new InsuranceCompanyData();
        data.setId(insuranceCompany.getId());
        data.setName(insuranceCompany.getName());
        return data;
    }
}
