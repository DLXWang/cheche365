package com.cheche365.cheche.manage.common.web.model;

import com.cheche365.cheche.core.model.InternalUser;

/**
 * Created by chenxiangyin on 2017/7/7.
 */
public class QuoteFlowConfigQuery {
    private Long id;
    private Integer pageSize;
    private Integer currentPage;
    private Long[] insureCompanys;//保险公司
    private Long[] channels;//渠道
    private Boolean status;//状态
    private Integer draw;
    private String reason;
    private Boolean enable;
    private Integer operateTime;
    private Integer quoteWay;
    private Long[] area;

    private InternalUser user;

    public InternalUser getUser() {
        return user;
    }

    public void setUser(InternalUser user) {
        this.user = user;
    }
    //private

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
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

    public Integer getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Integer operateTime) {
        this.operateTime = operateTime;
    }

    public Long[] getInsureCompanys() {
        return insureCompanys;
    }

    public void setInsureCompanys(Long[] insureCompanys) {
        this.insureCompanys = insureCompanys;
    }

    public Long[] getChannels() {
        return channels;
    }

    public void setChannels(Long[] channels) {
        this.channels = channels;
    }

    public Long[] getArea() {
        return area;
    }

    public void setArea(Long[] area) {
        this.area = area;
    }

    public Integer getQuoteWay() {
        return quoteWay;
    }

    public void setQuoteWay(Integer quoteWay) {
        this.quoteWay = quoteWay;
    }

    public enum OperationTime {
        NOW(0, "立即执行"), TOMORROW(1, "次日零点");

        private Integer index;
        private String name;

        OperationTime(Integer index, String name) {
            this.index = index;
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }
}
