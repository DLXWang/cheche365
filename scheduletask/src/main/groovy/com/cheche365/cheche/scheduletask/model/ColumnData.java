package com.cheche365.cheche.scheduletask.model;

import java.util.List;

/**
 * Created by guoweifu on 2016/1/19.
 */
public class ColumnData {

    private int columnNum;

    private ExcelSheetHeader[] headers;

    private List<String[]> rows;

    public List<String[]> getRows() {
        return rows;
    }

    public void setRows(List<String[]> rows) {
        this.rows = rows;
    }

    public ExcelSheetHeader[] getHeaders() {
        return headers;
    }

    public void setHeaders(ExcelSheetHeader[] headers) {
        this.headers = headers;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }

}
