package com.cheche365.cheche.partner.model;

/**
 * Created by mahong on 2016/2/17.
 */
public class BaiduInsuranceDetail {
    private String code;//编码(必填)
    private String name;//名称(必填)
    private Double insurance;//承保金额，默认0，精确到小数点后两位(必填)
    private Double price;//单价，默认0，精确到小数点后两位(必填)

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
