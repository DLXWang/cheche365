package com.cheche365.cheche.admin.web.model.user;

import com.cheche365.cheche.admin.web.model.auto.AutoViewModel;

import java.util.List;

/**
 * Created by guoweifu on 2015/9/8.
 */
public class UserViewModel {

    private Long id;
    private String name;
    private String userId;
    private String mobile;//手机号
    private String binding;//第三方绑定
    private String regtime;//注册时间
    private String regChannel;//注册渠道
    private String regIp;//注册IP
    private String lastLoginTime;//最后登录时间
    private List<AutoViewModel> autos;//车辆信息

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public String getRegChannel() {
        return regChannel;
    }

    public void setRegChannel(String regChannel) {
        this.regChannel = regChannel;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<AutoViewModel> getAutos() {
        return autos;
    }

    public void setAutos(List<AutoViewModel> autos) {
        this.autos = autos;
    }
}
