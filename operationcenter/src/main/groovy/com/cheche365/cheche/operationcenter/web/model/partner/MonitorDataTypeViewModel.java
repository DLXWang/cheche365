package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;

/**
 * 基础监控类型
 * Created by sunhuazhong on 2015/8/26.
 */
public class MonitorDataTypeViewModel {
    private Long id;
    @NotNull
    private String name;//监控数据类型表，包括PV，UV，注册，试算，提交订单数，提交订单总额，支付订单数，支付订单总额，特殊监控
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
