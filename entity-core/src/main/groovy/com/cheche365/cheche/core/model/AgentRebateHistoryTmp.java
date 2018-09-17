package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.serializer.FormattedDoubleSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@Entity
public class AgentRebateHistoryTmp {
    private Long id;
    private AgentTmp agent;
    private Area area;
    private InsuranceCompany insuranceCompany;
    private Double commercialRebate;
    private Double compulsoryRebate;
    private Integer operation; //1新增 2修改 3删除
    private Date startTime;
    private Date endTime;
    private InternalUser operator;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "agent", foreignKey = @ForeignKey(name = "FK_AGENT_REBATE_HISTORY_TMP_REF_AGENT", foreignKeyDefinition = "FOREIGN KEY (agent) REFERENCES agent_tmp(id)"))
    public AgentTmp getAgent() {
        return agent;
    }

    public void setAgent(AgentTmp agent) {
        this.agent = agent;
    }

    @ManyToOne
    @JoinColumn(name="area", foreignKey=@ForeignKey(name="FK_AGENT_REBATE_HISTORY_TMP_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES arae(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name="insuranceCompany", foreignKey=@ForeignKey(name="FK_AGENT_REBATE_HISTORY_TMP_REF_INSURANCE_COMPANY", foreignKeyDefinition="FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

    @JsonSerialize(using = FormattedDoubleSerializer.class)
    @Column(columnDefinition = "DECIMAL(18,2)")
    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    @Column(columnDefinition = "tinyint(1)")
    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(columnDefinition = "DATETIME")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @ManyToOne
    @JoinColumn(name = "operator", foreignKey = @ForeignKey(name = "FK_AGENT_REBATE_HISTORY_TMP_REF_INTERNAL_USER", foreignKeyDefinition = "FOREIGN KEY (operator) REFERENCES internal_user(id)"))
    public InternalUser getOperator() {
        return operator;
    }

    public void setOperator(InternalUser operator) {
        this.operator = operator;
    }

    public static class OPERATION{
        public static Integer ADD=1;
        public static Integer UPD=2;
        public static Integer DEL=3;
    }
}
