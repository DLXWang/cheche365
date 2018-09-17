package com.cheche365.cheche.manage.common.web.model.sms;

import javax.validation.constraints.NotNull;

/**
 * Created by guoweifu on 2015/10/12.
 */
public class FilterUserViewModel {

    private long id;
    @NotNull
    private String name;
    @NotNull
    private long sqlTemplateId;
    private SqlTemplateViewModel sqlTemplateViewModel;
    private String content;
    private String parameter;
    private Integer disable;
    private String comment;
    private String createTime;
    private String updateTime;
    private String operator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSqlTemplateId() {
        return sqlTemplateId;
    }

    public void setSqlTemplateId(long sqlTemplateId) {
        this.sqlTemplateId = sqlTemplateId;
    }

    public SqlTemplateViewModel getSqlTemplateViewModel() {
        return sqlTemplateViewModel;
    }

    public void setSqlTemplateViewModel(SqlTemplateViewModel sqlTemplateViewModel) {
        this.sqlTemplateViewModel = sqlTemplateViewModel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Integer getDisable() {
        return disable;
    }

    public void setDisable(Integer disable) {
        this.disable = disable;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
