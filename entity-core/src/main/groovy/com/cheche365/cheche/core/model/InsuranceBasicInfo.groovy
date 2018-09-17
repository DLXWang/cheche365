package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical
import org.springframework.beans.BeanUtils

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import java.beans.PropertyDescriptor


/**
 * 保险基本信息
 */
@Entity
@JsonIgnoreProperties(value = ['id'])
@Canonical(excludes = ['id'])
class InsuranceBasicInfo {

    public static final PropertyDescriptor[] PROPERTIES = BeanUtils.getPropertyDescriptors(InsuranceBasicInfo.class)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    // 商业险生效日期
    @Column(name= "effectiveDate",columnDefinition = "DATE")
    Date commercialStartDate

    // 交强险生效日期
    @Column(name = "compulsoryEffectiveDate",columnDefinition = "DATE")
    Date compulsoryStartDate

    // 续保套餐
    @ManyToOne
    @JoinColumn(
        name = 'insurancePackage',
        foreignKey = @ForeignKey(
            name = 'FK_INSURANCE_BASIC_INFO_REF_INSURANCE_PACKAGE',
            foreignKeyDefinition = 'FOREIGN KEY (insurance_package) REFERENCES insurance_package(id)'))
    InsurancePackage insurancePackage

    @Column(columnDefinition = 'VARCHAR(45)')
    String applicantName
    @Column(columnDefinition = 'VARCHAR(45)')
    String applicantIdNo
    @ManyToOne
    @JoinColumn(
        name = 'applicantIdentityType',
        foreignKey = @ForeignKey(
            name = 'FK_INSURANCE_BASIC_INFO_REF_APPLICANT_IDENTITY_TYPE',
            foreignKeyDefinition = 'FOREIGN KEY (applicant_identity_type) REFERENCES identity_type(id)'))
    IdentityType applicantIdentityType
    @Column(columnDefinition = 'VARCHAR(45)')
    String applicantMobile

    @Column(columnDefinition = 'VARCHAR(45)')
    String insuredName
    @Column(columnDefinition = 'VARCHAR(45)')
    String insuredIdNo
    @ManyToOne
    @JoinColumn(
        name = 'insuredIdentityType',
        foreignKey = @ForeignKey(
            name = 'FK_INSURANCE_BASIC_INFO_REF_INSURED_IDENTITY_TYPE',
            foreignKeyDefinition = 'FOREIGN KEY (insured_identity_type) REFERENCES identity_type(id)'))
    IdentityType insuredIdentityType
    @Column(columnDefinition = 'VARCHAR(45)')
    String insuredMobile

    @ManyToOne
    @JoinColumn(
        name = "insuranceCompany",
        foreignKey = @ForeignKey(
            name = "FK_IBI_REF_INSURANCE_COMPANY",
            foreignKeyDefinition = "FOREIGN KEY (insurance_company) REFERENCES insurance_company(id)"))
    InsuranceCompany insuranceCompany

}
