package com.cheche365.cheche.operationcenter.web.model.partner;

import javax.validation.constraints.NotNull;

/**
 * 自定义字段
 * Created by sunhuazhong on 2015/8/26.
 */
public class CustomerFieldViewModel {
    private Long id;
    @NotNull
    private String name;//自定义字段名称
    @NotNull
    private Long businessActivity;//商务活动id
    @NotNull
    private Long firstField;//基础字段A
    @NotNull
    private Long secondField;//基础字段B
    @NotNull
    private Long operator;//运算符

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

    public Long getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(Long businessActivity) {
        this.businessActivity = businessActivity;
    }

    public Long getFirstField() {
        return firstField;
    }

    public void setFirstField(Long firstField) {
        this.firstField = firstField;
    }

    public Long getSecondField() {
        return secondField;
    }

    public void setSecondField(Long secondField) {
        this.secondField = secondField;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }
}
