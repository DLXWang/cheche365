package com.cheche365.cheche.operationcenter.model;

import com.cheche365.cheche.manage.common.model.PublicQuery;

/**
 * Created by chenxiangyin on 2017/8/29.
 */
public class AccessDetailQuery extends PublicQuery {
    private String source;
    private String startDate;
    private String endDate;
    private String sort;//排序

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
