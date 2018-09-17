package com.cheche365.cheche.scheduletask.model;

import java.util.List;

/**
 * Created by guoweifu on 2015/11/30.
 */
public class ExcelSheetData {

    private String sheetName;

    private List<ColumnData> columnDataList;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<ColumnData> getColumnDataList() {
        return columnDataList;
    }

    public void setColumnDataList(List<ColumnData> columnDataList) {
        this.columnDataList = columnDataList;
    }
}
