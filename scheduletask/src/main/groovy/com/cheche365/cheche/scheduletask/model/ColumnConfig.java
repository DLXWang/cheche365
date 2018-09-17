package com.cheche365.cheche.scheduletask.model;

import java.util.Map;

/**
 * Created by guoweifu on 2016/1/18.
 */
public class ColumnConfig {

    private String columnType;
    private Map<String,String> column;

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Map<String, String> getColumn() {
        return column;
    }

    public void setColumn(Map<String, String> column) {
        this.column = column;
    }
}
