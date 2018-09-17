package com.cheche365.cheche.core.model;

/**
 * Created by wangfei on 2015/8/18.
 */
public class ResultModel {
    private boolean pass;
    private String message;

    public ResultModel() {
        this(true, "成功");
    }

    public ResultModel(boolean pass, String message) {
        this.pass = pass;
        this.message = message;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
