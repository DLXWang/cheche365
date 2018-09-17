package com.cheche365.cheche.ordercenter.web.model.agent;

import com.cheche365.cheche.core.model.AgentRebateTmp;
import com.cheche365.cheche.core.model.AgentRebateHistoryTmp;

/**
 * Created by wangshaobin on 2017/5/5.
 */
public class AgentRebateTmpViewData {
    private Long id;
    private Long area;//城市
    private Long agent;//代理人
    private Long insuranceCompany;//保险公司
    private Double compulsoryRebate;//交强险返点
    private Double commercialRebate;//商业险返点

    public AgentRebateTmpViewData(){}

    public AgentRebateTmpViewData(AgentRebateTmp agentRebate){
        this.id=agentRebate.getId();
        this.agent=agentRebate.getAgent().getId();
        this.area=agentRebate.getArea().getId();
        this.insuranceCompany=agentRebate.getInsuranceCompany().getId();
        this.commercialRebate=agentRebate.getCommercialRebate();
        this.compulsoryRebate=agentRebate.getCompulsoryRebate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public Long getAgent() {
        return agent;
    }

    public void setAgent(Long agent) {
        this.agent = agent;
    }

    public Long getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(Long insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    public static AgentRebateTmpViewData createViewModelByHistory(AgentRebateHistoryTmp agentRebateHistory){
        AgentRebateTmpViewData viewData=new AgentRebateTmpViewData();
        viewData.setAgent(agentRebateHistory.getAgent().getId());
        viewData.setArea(agentRebateHistory.getArea().getId());
        viewData.setInsuranceCompany(agentRebateHistory.getInsuranceCompany().getId());
        viewData.setCommercialRebate(agentRebateHistory.getCommercialRebate());
        viewData.setCompulsoryRebate(agentRebateHistory.getCompulsoryRebate());
        return viewData;
    }
}
