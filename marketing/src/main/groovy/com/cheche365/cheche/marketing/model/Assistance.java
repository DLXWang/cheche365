package com.cheche365.cheche.marketing.model;

import javax.validation.constraints.NotNull;

/**
 * Created by zhengwei on 11/16/15.
 * 助力参数实体。2015年双12/感恩节活动设计。
 */
public class Assistance {

    @NotNull
    private String targetOpenId;

    public String getTargetOpenId() {
        return targetOpenId;
    }

    public void setTargetOpenId(String targetOpenId) {
        this.targetOpenId = targetOpenId;
    }
}
