package com.cheche365.cheche.core.model;
import javax.persistence.*;

/**
 * Created by xu.yelong on 2016/2/17.
 */
@Entity
public class AgentRebate {
    private Long id;
    private Area area;//城市
    private Agent agent;//代理人
    private InsuranceCompany insuranceCompany;//保险公司
    private Double compulsoryRebate = 0.0;//交强险返点
    private Double commercialRebate = 0.0;//商业险返点

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "area", foreignKey=@ForeignKey(name="FK_AGENT_REBATE_REF_AREA", foreignKeyDefinition="FOREIGN KEY (area) REFERENCES area(id)"))
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @ManyToOne
    @JoinColumn(name = "agent", foreignKey=@ForeignKey(name="FK_AGENT_REBATE_REF_AGENT", foreignKeyDefinition="FOREIGN KEY (agent) REFERENCES agent(id)"))
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @ManyToOne
    @JoinColumn(name = "insuranceCompany", foreignKey=@ForeignKey(name="FK_AGENT_REBATE_REF_INSURANCE_COMPANY", foreignKeyDefinition="FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getCompulsoryRebate() {
        return compulsoryRebate;
    }

    public void setCompulsoryRebate(Double compulsoryRebate) {
        this.compulsoryRebate = compulsoryRebate;
    }

    @Column(columnDefinition = "Decimal(18,2)")
    public Double getCommercialRebate() {
        return commercialRebate;
    }

    public void setCommercialRebate(Double commercialRebate) {
        this.commercialRebate = commercialRebate;
    }

}
