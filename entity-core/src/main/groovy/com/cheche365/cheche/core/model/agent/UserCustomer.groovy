package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.User
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.*

@Entity
@JsonIgnoreProperties(value = ['id'])
@Canonical(excludes = ['id'])
class UserCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne
    @JoinColumn(name = "user", foreignKey = @ForeignKey(name = "FK_USER_CUSTOMER_REF_USER", foreignKeyDefinition = "FOREIGN KEY (user) REFERENCES user(id)"))
    User user

    @ManyToOne
    @JoinColumn(name = "customer", foreignKey = @ForeignKey(name = "FK_USER_CUSTOMER_REF_CUSTOMER", foreignKeyDefinition = "FOREIGN KEY (customer) REFERENCES customer(id)"))
    Customer customer

}
