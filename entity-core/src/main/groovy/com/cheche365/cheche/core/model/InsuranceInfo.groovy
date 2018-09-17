package com.cheche365.cheche.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Transient


/**
 * 保险信息
 * Created by Huabin on 2016/11/16.
 */
@Entity
@JsonIgnoreProperties(value = ['id'])
@Canonical(excludes = ['id'])
class InsuranceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id

    // 行驶证信息
    @ManyToOne
    @JoinColumn(
        name = 'vehicleLicense',
        foreignKey = @ForeignKey(
            name = 'FK_INSURANCE_INFO_REF_VEHICLE_LICENSE',
            foreignKeyDefinition = 'FOREIGN KEY (vehicle_license) REFERENCES vehicle_license(id)'))
    VehicleLicense vehicleLicense

    // 保险基本信息
    @ManyToOne
    @JoinColumn(
        name = 'insuranceBasicInfo',
        foreignKey = @ForeignKey(
            name = 'FK_INSURANCE_INFO_REF_INSURANCE_BASIC_INFO',
            foreignKeyDefinition = 'FOREIGN KEY (insurance_basic_info) REFERENCES insurance_basic_info(id)'))
    InsuranceBasicInfo insuranceBasicInfo

    // 元信息
    @Transient
    Map metaInfo

}
