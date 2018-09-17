package com.cheche365.cheche.scheduletask.model;

import java.util.List;
import java.util.Map;

/**
 * Created by guoweifu on 2016/1/18.
 */
public class SheetConfig {

    private String sheetName;
    private List<ColumnConfig> columns;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<ColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnConfig> columns) {
        this.columns = columns;
    }
}
