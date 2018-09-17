package com.cheche365.cheche.scheduletask.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoweifu on 2015/11/30.
 */
public class ExcelInfoData {

    private String fileName;

    private List<ExcelSheetData> sheetDatas;


    public void setFirstSheet(ExcelSheetData excelSheetData){
        this.sheetDatas = new ArrayList<>();
        this.sheetDatas.add(excelSheetData);
    }

    public ExcelSheetData getSheetData(){
        if(sheetDatas!=null && sheetDatas.size()>0){
            return sheetDatas.get(0);
        }
        return null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<ExcelSheetData> getSheetDatas() {
        return sheetDatas;
    }

    public void setSheetDatas(List<ExcelSheetData> sheetDatas) {
        this.sheetDatas = sheetDatas;
    }
}





