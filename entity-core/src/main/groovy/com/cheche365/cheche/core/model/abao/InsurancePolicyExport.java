package com.cheche365.cheche.core.model.abao;

import javax.persistence.*;

/**
 * Created by xu.yelong on 2016/12/27.
 */
@Entity
public class InsurancePolicyExport {
    private Long id;
    private InsurancePolicy insurancePolicy;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "insurance_policy", foreignKey=@ForeignKey(name="FK_INSURANCE_POLICY_EXPORT_REF_INSURANCE_POLICY", foreignKeyDefinition="FOREIGN KEY (insurance_policy) REFERENCES insurance_policy(id)"))
    public InsurancePolicy getInsurancePolicy() {
        return insurancePolicy;
    }

    public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
        this.insurancePolicy = insurancePolicy;
    }
}
