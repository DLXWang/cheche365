package com.cheche365.cheche.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by shanxf on 2017/6/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteConfigPara {

    private String channelId;
    private String configType;
    private String configValue;
    private List<Map<String,Long>> insuranceCompanyArea;
    private List<Long> insuranceCompanyId;
    private List<Long> areaId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public List<Map<String, Long>> getInsuranceCompanyArea() {
        return insuranceCompanyArea;
    }

    public void setInsuranceCompanyArea(List<Map<String, Long>> insuranceCompanyArea) {
        this.insuranceCompanyArea = insuranceCompanyArea;
    }

    public List<Long> getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(List<Long> insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public List<Long> getAreaId() {
        return areaId;
    }

    public void setAreaId(List<Long> areaId) {
        this.areaId = areaId;
    }
}
