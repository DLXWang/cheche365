package com.cheche365.cheche.manage.common.web.model;

import java.util.List;

/**
 * Created by wangfei on 2015/5/5.
 */
public class PageViewModel<T> {
    private PageInfo pageInfo;//分页信息
    private List<T> viewList;//返回实体

    public PageViewModel() {
    }

    public PageViewModel(PageInfo pageInfo, List<T> viewList) {
        this.pageInfo = pageInfo;
        this.viewList = viewList;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<T> getViewList() {
        return viewList;
    }

    public void setViewList(List<T> viewList) {
        this.viewList = viewList;
    }
}
