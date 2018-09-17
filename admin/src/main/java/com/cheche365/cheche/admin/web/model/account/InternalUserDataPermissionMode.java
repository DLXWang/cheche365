package com.cheche365.cheche.admin.web.model.account;

import java.util.List;

/**
 * Created by sufc on 2017/7/3.
 */
//用于前台展示用的bean
public class InternalUserDataPermissionMode {

    private String entity;//操作的对象

    private String field;//字段

    private String values;//所选择的权限

    private String comment; //备注

    private Long id; //InternalUserDataPermission的id

    private Boolean status; //状态

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
