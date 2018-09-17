package com.cheche365.cheche.core.model;

/**
 * Created by shanxf on 2017/8/28.
 * 太汇保 费率
 */
public class RebateRate {

    private String area;
    private String insuranceCompany;
    private int  compulsoryRebateRate;
    private int  insuranceRebateRate;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public int getCompulsoryRebateRate() {
        return compulsoryRebateRate;
    }

    public void setCompulsoryRebateRate(int compulsoryRebateRate) {
        this.compulsoryRebateRate = compulsoryRebateRate;
    }

    public int getInsuranceRebateRate() {
        return insuranceRebateRate;
    }

    public void setInsuranceRebateRate(int insuranceRebateRate) {
        this.insuranceRebateRate = insuranceRebateRate;
    }
}
