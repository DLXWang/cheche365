package com.cheche365.cheche.ordercenter.web.model.insurance;
import java.util.List;

/**
 * 导入数据查询
 * Created by cxy on 2017/12/15.
 */
public class ImportDataHistoryModel {
    private String importDateStart;
    private String importDateEnd;
    private String historyId;
    private String dataType;
    private Integer dataTypeId;
    private String area;
    private String orderNum;
    private String comment;
    private Integer draw;
    private Integer currentPage;
    private Integer pageSize;
    private String description;
    private String balanceTime;

    public String getImportDateStart() {
        return importDateStart;
    }

    public void setImportDateStart(String importDateStart) {
        this.importDateStart = importDateStart;
    }

    public String getImportDateEnd() {
        return importDateEnd;
    }

    public void setImportDateEnd(String importDateEnd) {
        this.importDateEnd = importDateEnd;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBalanceTime() {
        return balanceTime;
    }

    public void setBalanceTime(String balanceTime) {
        this.balanceTime = balanceTime;
    }

}
