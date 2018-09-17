package com.cheche365.cheche.core.serializer.converter;

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhengwei on 10/8/15.
 */
public class Field {

    protected Map amount;//保额
    String name;
    String displayName;
    String shortName;
    FieldGroup group;//分组

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    private Object premium;//保费
    private Object iop = 0.0;//不计免赔

    public Field(){

    }
    public Field(FieldGroup group) {
        setAmount(new HashMap<>());
        getAmount().put("value", 0);
        getAmount().put("text", "");
        this.group = group;

    }

    public Field(String group){

        this(FieldGroup.valueOf(group));
    }
    public String getShortName() {
        return shortName;
    }
    public Field setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public FieldGroup getGroup() {
        return group;
    }

    public Field setGroup(FieldGroup group) {
        this.group = group;
        return this;
    }

    public String getName() {
        return name;
    }

    public Field setName(String name) {
        this.name = name;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Field setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Object getIop() {
        return iop;
    }

    public Field setIop(Object iop) {
        this.iop = iop;
        return this;
    }

    public Object getPremium() {
        return premium;
    }

    public Field setPremium(Object premium) {
        this.premium = premium;
        return this;
    }

    public Map getAmount() {
        return amount;
    }

    public Field setAmount(Map amount) {
        this.amount = amount;
        return this;
    }

    public enum FieldGroup {
        compulsory, base, autoTax
    }
}
