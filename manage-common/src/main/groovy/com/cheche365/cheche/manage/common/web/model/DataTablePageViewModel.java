package com.cheche365.cheche.manage.common.web.model;

import java.util.List;

public class DataTablePageViewModel<T> {

    private Long iTotalRecords;//实际的行数
    private Long iTotalDisplayRecords;//过滤之后，实际的行数
    private Integer draw;//datatables传过来的参数 原样返回
    private List<T> aaData;//返回实体
    private Object data;

    public DataTablePageViewModel(List<T> aaData, T data) {
        this.aaData = aaData;
        this.data = data;
    }

    public DataTablePageViewModel(List<T> aaData) {
        this.aaData = aaData;
    }

    //限制页数
    public DataTablePageViewModel(Integer limitNums, Long iTotalRecords, Integer draw, List<T> aaData){
        iTotalRecords = iTotalRecords > limitNums ? limitNums : iTotalRecords;
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalRecords;
        this.draw = draw;
        this.aaData = aaData;
    }

    public DataTablePageViewModel(Long iTotalRecords, Long iTotalDisplayRecords, Integer draw, List<T> aaData) {
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.draw = draw;
        this.aaData = aaData;
    }

    public DataTablePageViewModel(Long iTotalRecords, Long iTotalDisplayRecords, Integer draw, List<T> aaData, T data) {
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.draw = draw;
        this.aaData = aaData;
        this.data = data;
    }

    public DataTablePageViewModel() {
    }

    public Long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public Long getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }


    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public List<T> getAaData() {
        return aaData;
    }

    public void setAaData(List<T> aaData) {
        this.aaData = aaData;
    }

    public void setiTotalRecords(Long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
