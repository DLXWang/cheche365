package com.cheche365.cheche.ordercenter.web.model;

import java.util.List;

/**
 * Created by xu.yelong on 2016/11/4.
 */
public class GridViewModel<T> {
    private  List<T> data;

    public GridViewModel(){};

    public GridViewModel(List<T> dataList){
        this.data=dataList;
    }
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
