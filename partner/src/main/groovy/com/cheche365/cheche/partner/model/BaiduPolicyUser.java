package com.cheche365.cheche.partner.model;

/**
 * Created by mahong on 2016/2/17.
 */
public class BaiduPolicyUser {
    private String username; //被保人真实姓名(必填)
    private String mobile; //被保人手机号(必填)
    private String email; //被保人邮箱(非必填)
    private Integer id_type; //被保人证件类型(非必填), 1 居民身份证; 2 护照
    private String id_num; //被保人证件号码(非必填)

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId_type() {
        return id_type;
    }

    public void setId_type(Integer id_type) {
        this.id_type = id_type;
    }

    public String getId_num() {
        return id_num;
    }

    public void setId_num(String id_num) {
        this.id_num = id_num;
    }
}
