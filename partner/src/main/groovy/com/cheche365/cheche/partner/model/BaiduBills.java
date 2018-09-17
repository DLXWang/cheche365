package com.cheche365.cheche.partner.model;

import com.cheche365.cheche.partner.utils.BaiduEncryptUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mahong on 2016/2/17.
 */
public class BaiduBills extends BaiduCommon {
//    private String partner_id; //合作方编号，百度给出(必填,不加密)
//    private String order_id; //合作方订单号(必填,不加密)
//    private String bduid; //百度用户账号，必须回传(必填,不加密)
//    private String details; //保险详情，二维json(必填,加密)
//    private String policy_holder; //投保人信息，一维json(必填,加密)
//    private String policy_users; //被保人信息，无则为空，二维json(非必填,加密)
//    private String policy_car; //车辆信息，无则为空，一维json(非必填,加密)
//    private Double price_total; //该订单需要支付的总金额(非必填,不加密)
//    private Double price_discount; //该订单折扣的总金额(非必填,不加密)
//    private Double price_pay; //该订单用户支付的总金额(非必填,不加密)
//    private String callback_detail; //详情页面回调地址，为空是无详情页面或者不能跳转到详情页面(非必填,不加密)
//    private String callback_pay; //支付页面回调地址，为空是不能跳转到支付页面(非必填,不加密)
//    private Integer status; //订单状态，参考订单状态映射(必填,不加密)
//    private String create_time; //系统时间(必填,不加密)
//    private String sign; //签名值(必填,不加密)
//    private Integer sync_type; 同步类型，1为订单同步，2为信息同步，默认为1

    // TODO baidu rename to bdmap
    public static final List<String> ORDER_SYNC_SIGN_FIELDS = Arrays.asList(new String[]{"bduid", "partner_id", "order_id", "details", "policy_holder", "status", "create_time"});  //同步订单需要签名的字段集合

    public void setOrder_id(String order_id) {
        this.putSingle("order_id", order_id);
    }


    public void setBduid(String bduid) {
        this.putSingle("bduid", bduid);
    }

    public String getDetails() {
        return this.getFirst("details");
    }

    public void setDetails(String details) {
        this.putSingle("details", details);
    }

    public String getPolicy_holder() {
        return this.getFirst("policy_holder");
    }

    public void setPolicy_holder(String policy_holder) {
        this.putSingle("policy_holder", policy_holder);
    }

    public String getPolicy_users() {
        return this.getFirst("policy_users");
    }

    public void setPolicy_users(String policy_users) {
        this.putSingle("policy_users", policy_users);
    }

    public String getPolicy_car() {
        return this.getFirst("policy_car");
    }

    public void setPolicy_car(String policy_car) {
        this.putSingle("policy_car", policy_car);
    }


    public void setPrice_total(Double price_total) {
        this.putSingle("price_total", price_total);
    }


    public void setPrice_discount(Double price_discount) {
        this.putSingle("price_discount", price_discount);
    }


    public void setPrice_pay(Double price_pay) {
        this.putSingle("price_pay", price_pay);
    }


    public void setCallback_detail(String callback_detail) {
        this.putSingle("callback_detail", callback_detail);
    }


    public void setCallback_pay(String callback_pay) {
        this.putSingle("callback_pay", callback_pay);
    }


    public void setStatus(Integer status) {
        this.putSingle("status", status);
    }

    public void setCreate_time(String create_time) {
        this.putSingle("create_time", create_time);
    }


    @Override
    public List<String> getSignFields() {
        return ORDER_SYNC_SIGN_FIELDS ;
    }
}
