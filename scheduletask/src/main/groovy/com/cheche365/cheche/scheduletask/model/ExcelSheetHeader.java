package com.cheche365.cheche.scheduletask.model;

/**
 * Created by guoweifu on 2015/11/30.
 */
public class ExcelSheetHeader {

    private int columeWidth;

    private String name;

    public ExcelSheetHeader(int columeWidth, String name) {
        this.columeWidth = columeWidth;
        this.name = name;
    }

    public ExcelSheetHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColumeWidth() {
        return columeWidth;
    }

    public void setColumeWidth(int columeWidth) {
        this.columeWidth = columeWidth;
    }


}
