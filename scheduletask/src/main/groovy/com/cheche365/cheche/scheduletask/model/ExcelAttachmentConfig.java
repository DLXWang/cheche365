package com.cheche365.cheche.scheduletask.model;

import java.util.List;

/**
 * Created by guoweifu on 2015/12/22.
 */
public class ExcelAttachmentConfig {

    private String fileName;
    private List<SheetConfig> sheets;

    public List<SheetConfig> getSheets() {
        return sheets;
    }

    public void setSheets(List<SheetConfig> sheets) {
        this.sheets = sheets;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
