package com.cheche365.cheche.manage.common.web.model.sms;

import java.util.List;

/**
 * Created by guoweifu on 2015/10/13.
 */
public class SqlTemplateViewModel {

    private long id;
    private String name;
    private String content;
    private List<SqlParameterViewModel> sqlParameterViewModelList;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<SqlParameterViewModel> getSqlParameterViewModelList() {
        return sqlParameterViewModelList;
    }

    public void setSqlParameterViewModelList(List<SqlParameterViewModel> sqlParameterViewModelList) {
        this.sqlParameterViewModelList = sqlParameterViewModelList;
    }
}
