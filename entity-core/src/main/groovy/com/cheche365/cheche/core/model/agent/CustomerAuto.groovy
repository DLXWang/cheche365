package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.Auto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.*

@Entity
@JsonIgnoreProperties(value = ['id'])
@Canonical(excludes = ['id'])
class CustomerAuto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name = "customer", foreignKey = @ForeignKey(name = "FK_CUSTOMER_AUTO_REF_CUSTOMER", foreignKeyDefinition = "FOREIGN KEY (customer) REFERENCES customer(id)"))
    Customer customer

    @ManyToOne
    @JoinColumn(name = "auto", foreignKey = @ForeignKey(name = "FK_CUSTOMER_AUTO_REF_AUTO", foreignKeyDefinition = "FOREIGN KEY (auto) REFERENCES auto(id)"))
    Auto auto

}


