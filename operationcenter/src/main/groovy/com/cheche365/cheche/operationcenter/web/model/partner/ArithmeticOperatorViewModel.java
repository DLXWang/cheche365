package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;

/**
 * 基础监控类型
 * Created by sunhuazhong on 2015/8/26.
 */
public class ArithmeticOperatorViewModel {
    private Long id;
    @NotNull
    private String name;//运算符，包括加减乘除
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
