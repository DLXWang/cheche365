package com.cheche365.cheche.operationcenter.model;

/**
 * Created by chenxiangyin on 2017/7/7.
 */
public class QuoteOfflineAddQuery {
    private Long id;
    private Integer pageSize;
    private Integer currentPage;
    private String insureComp;//保险公司
    private String channel;//渠道
    private Integer status;//状态
    private Integer draw;
    private String reason;
    private Boolean enable;
    private String operateTime;
    private Long configValue;
    private String area;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getInsureComp() {
        return insureComp;
    }

    public void setInsureComp(String insureComp) {
        this.insureComp = insureComp;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Long getConfigValue() {
        return configValue;
    }

    public void setConfigValue(Long configValue) {
        this.configValue = configValue;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

}
