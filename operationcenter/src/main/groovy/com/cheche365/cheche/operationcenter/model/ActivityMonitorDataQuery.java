package com.cheche365.cheche.operationcenter.model;

import org.hibernate.validator.constraints.URL;

import java.util.Date;

/**
 * Created by chenxy on 2017/6/8.
 */
public class ActivityMonitorDataQuery {
    private Integer pageSize;
    private Integer currentPage;
    private String scope;//岗位
    private String source;//渠道
    private String plan;//计划
    private String unit;//单元
    private String keyword;//关键词
    @URL(message = "请输入正确的网址")
    private String url; //生成链接
    private String tinyUrl;//短链接

    private String startTimeStr;
    private String endTimeStr;
    private Integer draw;
    private String sort ="amu.id";//排序
    private String sortRule = "DESC";//排序规则
    private Boolean groupByDay = false;
    private Integer quote;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTinyUrl() {
        return tinyUrl;
    }

    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public String getSortRule() {
        return sortRule;
    }

    public void setSortRule(String sortRule) {
        this.sortRule = sortRule;
    }

    public Boolean getGroupByDay() {
        return groupByDay;
    }

    public void setGroupByDay(Boolean groupByDay) {
        this.groupByDay = groupByDay;
    }

    public Integer getQuote() {
        return quote;
    }

    public void setQuote(Integer quote) {
        this.quote = quote;
    }
}
