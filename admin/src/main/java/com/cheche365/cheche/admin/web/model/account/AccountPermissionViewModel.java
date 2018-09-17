package com.cheche365.cheche.admin.web.model.account;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sufc on 2017/6/27.
 */
public class AccountPermissionViewModel {

    private  Long internalID; //internalUser的id
    private Long id; //用户id

    private String entity; //操作的对象

    private String field; //字段

    private int chooseType; //选择的是地区还是保险公司 等等

    private ArrayList<Long> values; //所选择的权限

    private String comment; //备注

    private String code = "OC1";

    public int getChooseType() {
        return chooseType;
    }

    public void setChooseType(int chooseType) {
        this.chooseType = chooseType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getInternalID() {
        return internalID;
    }

    public void setInternalID(Long internalID) {
        this.internalID = internalID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ArrayList<Long> getValues() {
        return values;
    }

    public void setValues(ArrayList<Long> values) {
        this.values = values;
    }
}
