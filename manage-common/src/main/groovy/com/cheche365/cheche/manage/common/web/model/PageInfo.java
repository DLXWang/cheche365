package com.cheche365.cheche.manage.common.web.model;

/**
 * Created by wangfei on 2015/4/22.
 */
public class PageInfo {
    private long totalPage;
    private long totalElements;

    public PageInfo(){}

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
