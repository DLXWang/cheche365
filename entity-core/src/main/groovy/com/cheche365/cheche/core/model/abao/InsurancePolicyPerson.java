package com.cheche365.cheche.core.model.abao;

import javax.persistence.*;

@Entity
@Table(name = "INSURANCE_POLICY_PERSON")
public class InsurancePolicyPerson {

    private Long id; // 主键
    private InsurancePolicy insurancePolicy; // 保单id
    private InsurancePerson insurancePerson; // 被保险人id

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_POLICY", foreignKey = @ForeignKey(name = "FK_POLICY_PERSON_REF_INSURANCE_POLICY", foreignKeyDefinition = "FOREIGN KEY (`insurance_policy`) REFERENCES `insurance_policy` (`id`)"))
    public InsurancePolicy getInsurancePolicy() {
        return this.insurancePolicy;
    }

    public void setInsurancePolicy(InsurancePolicy insurancePolicy) {
        this.insurancePolicy = insurancePolicy;
    }

    @ManyToOne
    @JoinColumn(name = "INSURANCE_PERSON", foreignKey = @ForeignKey(name = "FK_POLICY_PERSON_REF_INSURANCE_PERSON", foreignKeyDefinition = "FOREIGN KEY (`insurance_person`) REFERENCES `insurance_person` (`id`)"))
    public InsurancePerson getInsurancePerson() {
        return this.insurancePerson;
    }

    public void setInsurancePerson(InsurancePerson insurancePerson) {
        this.insurancePerson = insurancePerson;
    }
}
