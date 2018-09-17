package com.cheche365.cheche.ordercenter.web.model;


/**
 * Created by xu.yelong on 2016/1/26.
 */
public class ApplicationLogViewModel {
    private String id;

    private Long user;//用户

    private String logId;//日志的ID，标示是那种日志
    private int logLevel;//日志的级别 1debug  2info 3warn 4error
    private String logMessage;//日志信息
    private Long logType;

    private String objId;//对象id，例如，对报价相关的log，obj_id就是quote表的id
    private String objTable;//对象表名

    private Long operator;//操作员
    private String operatorName;
    private String instanceNo;//log是在哪个application instance 上生成的
    private String createTime;//创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public Long getLogType() {
        return logType;
    }

    public void setLogType(Long logType) {
        this.logType = logType;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getObjTable() {
        return objTable;
    }

    public void setObjTable(String objTable) {
        this.objTable = objTable;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(String instanceNo) {
        this.instanceNo = instanceNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }






}
