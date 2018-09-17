package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.core.serializer.FormattedDateSerializer;
import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 * Created by mahong on 2016/2/17.
 */
public class BaiduBillDetail {
    private Long id; //详情编号，需要唯一(必填)
    private String policy_num; //保单号(必填)
    private String product_code; //保险产品编码，各业务编码即可(必填)
    private String product_name; //保险产品名称，如“交强险”、“亲子险”(必填)
    private Integer amount; //数量，默认1，非车险一种可以购买多份(必填)
    private Date start_date; //保险承保开始日期(必填)
    private Date end_date; //保险承保结束日期(必填)
    private Double price_insurance; //承保金额，默认0，精确到小数点后两位(必填)
    private Double price_single; //详情单价，默认0，精确到小数点后两位(必填)
    private Double price_tax; //详情税费，默认0，精确到小数点后两位(必填)
    private Double price_discount; //优惠价格，默认0，精确到小数点后两位(必填)
    private Double price_total; //详情总价，默认0，精确到小数点后两位(必填)
    private List<BaiduInsuranceDetail> lists; //详情保险列表，二维json，如车险商业包含“车损险”、“第三者责任险”等(非必填)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicy_num() {
        return policy_num;
    }

    public void setPolicy_num(String policy_num) {
        this.policy_num = policy_num;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @JsonSerialize(using = FormattedDateSerializer.class)
    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    @JsonSerialize(using = FormattedDateSerializer.class)
    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPrice_insurance() {
        return price_insurance;
    }

    public void setPrice_insurance(Double price_insurance) {
        this.price_insurance = price_insurance;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPrice_single() {
        return price_single;
    }

    public void setPrice_single(Double price_single) {
        this.price_single = price_single;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPrice_tax() {
        return price_tax;
    }

    public void setPrice_tax(Double price_tax) {
        this.price_tax = price_tax;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPrice_discount() {
        return price_discount;
    }

    public void setPrice_discount(Double price_discount) {
        this.price_discount = price_discount;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    public Double getPrice_total() {
        return price_total;
    }

    public void setPrice_total(Double price_total) {
        this.price_total = price_total;
    }

    public List<BaiduInsuranceDetail> getLists() {
        return lists;
    }

    public void setLists(List<BaiduInsuranceDetail> lists) {
        this.lists = lists;
    }
}
