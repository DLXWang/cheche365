package com.cheche365.cheche.rest.processor.login;

/**
 * Created by zhaozhong on 2015/12/17.
 */
public class LoginInfo {

    private String mobile;

    private String verificationCode;

    private String gender;

    public LoginInfo() {
    }

    public LoginInfo(String mobile, String verificationCode) {
        this.mobile = mobile;
        this.verificationCode = verificationCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
