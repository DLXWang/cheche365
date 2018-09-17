package com.cheche365.cheche.core.model;

import java.util.Date;

/**
 * Created by wangshaobin on 2017/8/18.
 */
public class MoParserMutualMessage {
    private Long id;
    private Date createTime;
    private Date updateTime;
    private String description;
    private Integer messageType;
    private String content;
    private String stepNote;
    private String licensePlateNo;
    private Long insuranceCompany;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStepNote() {
        return stepNote;
    }

    public void setStepNote(String stepNote) {
        this.stepNote = stepNote;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public Long getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }
}
