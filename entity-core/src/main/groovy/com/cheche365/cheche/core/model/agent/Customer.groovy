package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.model.DescribableEntity
import com.cheche365.cheche.core.model.Gender
import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Customer extends DescribableEntity {

    private String name
    private String mobile
    private String identity
    private Gender gender
    private String email

    @Column(columnDefinition = "VARCHAR(45)")
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getMobile() {
        return mobile
    }

    void setMobile(String mobile) {
        this.mobile = mobile
    }

    @Column(columnDefinition = "VARCHAR(45)")
    String getIdentity() {
        return identity
    }

    void setIdentity(String identity) {
        this.identity = identity
    }

    @ManyToOne
    @JoinColumn(name = "gender", foreignKey = @ForeignKey(name = "FK_CUSTOMER_REF_GENDER", foreignKeyDefinition = "FOREIGN KEY (gender) REFERENCES gender(id)"))
    @JsonIgnore()
    Gender getGender() {
        return gender
    }

    void setGender(Gender gender) {
        this.gender = gender
    }

    @Column(columnDefinition = "VARCHAR(45)")
    @JsonIgnore()
    String getEmail() {
        return email
    }

    void setEmail(String email) {
        this.email = email
    }

}
