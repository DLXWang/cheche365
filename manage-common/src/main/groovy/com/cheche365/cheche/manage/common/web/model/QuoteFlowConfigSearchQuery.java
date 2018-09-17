package com.cheche365.cheche.manage.common.web.model;

/**
 * Created by chenxiangyin on 2017/7/7.
 */
public class QuoteFlowConfigSearchQuery {
    private Integer pageSize;
    private Integer currentPage;
    private Long insureCompanys;//保险公司
    private Long channels;//渠道
    private Boolean enable;//状态
    private Long areas;
    private Integer draw;
    private Long configId;
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

    public Long getInsureCompanys() {
        return insureCompanys;
    }

    public void setInsureCompanys(Long insureCompanys) {
        this.insureCompanys = insureCompanys;
    }

    public Long getChannels() {
        return channels;
    }

    public void setChannels(Long channels) {
        this.channels = channels;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Long getAreas() {
        return areas;
    }

    public void setAreas(Long areas) {
        this.areas = areas;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

}
