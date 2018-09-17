package com.cheche365.cheche.core.model.developer

import com.cheche365.cheche.core.model.DescribableEntity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Transient
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by zhengwei on 07/03/2018.
 */

@Entity
class DeveloperInfo extends DescribableEntity {

    DeveloperType developerType

    DeveloperBusinessType developerBusinessType

    String company

    String mobile

    String contactName

    String email

    String address

    Long developerTypeId

    Long developerBusinessTypeId

    @ManyToOne
    DeveloperType getDeveloperType() {
        return developerType
    }

    void setDeveloperType(DeveloperType developerType) {
        this.developerType = developerType
    }

    @ManyToOne
    DeveloperBusinessType getDeveloperBusinessType() {
        return developerBusinessType
    }

    void setDeveloperBusinessType(DeveloperBusinessType developerBusinessType) {
        this.developerBusinessType = developerBusinessType
    }

    @Column(columnDefinition = "VARCHAR(500)" )
    String getCompany() {
        return company
    }

    void setCompany(String company) {
        this.company = company
    }

    @Column(columnDefinition = "VARCHAR(20)" )
    String getMobile() {
        return mobile
    }

    void setMobile(String mobile) {
        this.mobile = mobile
    }

    @Column(columnDefinition = "VARCHAR(20)" )
    String getContactName() {
        return contactName
    }

    void setContactName(String contactName) {
        this.contactName = contactName
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getEmail() {
        return email
    }

    void setEmail(String email) {
        this.email = email
    }

    @Column(columnDefinition = "VARCHAR(500)")
    String getAddress() {
        return address
    }

    void setAddress(String address) {
        this.address = address
    }

    @Transient
    Long getDeveloperTypeId() {
        return developerTypeId
    }

    void setDeveloperTypeId(Long developerTypeId) {
        this.developerTypeId = developerTypeId
    }

    @Transient
    Long getDeveloperBusinessTypeId() {
        return developerBusinessTypeId
    }

    void setDeveloperBusinessTypeId(Long developerBusinessTypeId) {
        this.developerBusinessTypeId = developerBusinessTypeId
    }
}
