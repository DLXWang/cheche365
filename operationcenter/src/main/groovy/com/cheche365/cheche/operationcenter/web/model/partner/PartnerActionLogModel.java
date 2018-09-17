package com.cheche365.cheche.operationcenter.web.model.partner;

import groovy.transform.ToString;

import java.io.Serializable;

/**
 * Created by zhangpengcheng on 2018/4/14.
 */
@ToString
public class PartnerActionLogModel implements Serializable {

    private static final long serialVersionUID = 7596263118226381450L;
    Long id;
    String operationTime;
    String operator;
    String operationContent;
    String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperationContent() {
        return operationContent;
    }

    public void setOperationContent(String operationContent) {
        this.operationContent = operationContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
