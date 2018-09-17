package com.cheche365.cheche.ordercenter.model;

import java.util.List;

/**
 * Created by zhengwei on 3/24/15.
 */
public class Preference {

    private boolean serverPush;
    private String areaId;// 报价中区域信息不取此字段值，从Auto下的Area中
    private List<Long> companyIds;
    private List<Long> companiesExcept;
    private String uuid;
    private Integer flowType; // 1:新投保、转保 2:续保

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }


    public boolean isServerPush() {
        return serverPush;
    }

    public void setServerPush(boolean serverPush) {
        this.serverPush = serverPush;
    }

    public List<Long> getCompaniesExcept() {
        return companiesExcept;
    }

    public void setCompaniesExcept(List<Long> companiesExcept) {
        this.companiesExcept = companiesExcept;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getFlowType() {
        return flowType;
    }

    public void setFlowType(Integer flowType) {
        this.flowType = flowType;
    }

    public static boolean isRenewal(Integer flowType) {
        return null!=flowType && 2==flowType;
    }
}
