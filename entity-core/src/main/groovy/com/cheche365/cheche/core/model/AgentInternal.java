package com.cheche365.cheche.core.model;

import com.cheche365.cheche.core.repository.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000;

/**
 * Created by liqiang on 3/31/15.
 */
@Entity
public class AgentInternal extends BaseEntity {

    private Long id;
    private String name; //内部员工姓名
    private String identity; //证件号码
    private Long agentId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "VARCHAR(45)")
    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Column(columnDefinition = "BIGINT")
    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

}
