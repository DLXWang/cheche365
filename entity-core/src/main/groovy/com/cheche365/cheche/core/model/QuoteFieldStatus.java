package com.cheche365.cheche.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * 记录报价套餐项的变化情况
 * enable与originalAmount、currentAmount是互斥的
 * enable记录套餐项改变（true）与未改变（false）
 * originalAmount记录套餐项原保额
 * currentAmount记录套餐项新保额
 * Created by zhengwei on 4/14/15.
 */
public class QuoteFieldStatus implements Serializable {

    private static final long serialVersionUID = -7315760923703305044L;
    private String filedName;
    private boolean enabled = true;
    private Double originalAmount;//原保额
    private Double currentAmount;//新保额
    private String description;
    private Map meta;

    public String getFiledName() {
        return filedName;
    }

    public void setFieldName(String filedName) {
        this.filedName = filedName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map getMeta() {
        return meta;
    }

    public void setMeta(Map meta) {
        this.meta = meta;
    }

    public enum FieldName{

    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof QuoteFieldStatus && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
