package com.cheche365.cheche.core.serializer.converter

import com.cheche365.cheche.core.model.Area
import com.fasterxml.jackson.annotation.JsonIgnore

import static com.cheche365.cheche.core.util.BeanUtil.copyBeanToMap

/**
 * Created by liheng on 2018/9/7 0007.
 */
class APIArrayQuoteRecord extends ArrayQuoteRecord {

    @Override
    def getInsuranceCompany() {
        def insuranceCompany = [:]
        copyBeanToMap super.insuranceCompany, insuranceCompany, ['id', 'code', 'name']
        insuranceCompany
    }

    @Override
    def getInsurancePackage() {
        def insurancePackage = [:]
        copyBeanToMap super.insurancePackage, insurancePackage, ['autoTax', 'compulsory', 'damage', 'damageIop', 'designatedRepairShop', 'driverAmount', 'driverIop', 'engine', 'engineIop', 'glass', 'glassType', 'glassTypeId', 'iopTotal', 'passengerAmount', 'passengerIop', 'scratchAmount', 'scratchIop', 'spontaneousLoss', 'spontaneousLossIop', 'theft', 'theftIop', 'thirdPartyAmount', 'thirdPartyIop', 'unableFindThirdParty']
        insurancePackage
    }

    @Override
    def getAuto() {
        def auto = [:]
        def identityType = [:]
        def area = [:]
        copyBeanToMap super.auto, auto, ['engineNo', 'enrollDate', 'identity', 'identityType', 'licensePlateNo', 'owner', 'vinNo']
        copyBeanToMap super.area, area, ['id', 'name', 'shortCode']
        copyBeanToMap super.auto.identityType, identityType, ['id', 'name', 'description']
        auto << [area: area, autoType: super.auto.autoType.code, identityType: identityType]
    }

    @JsonIgnore
    @Override
    Area getArea() {
        super.area
    }

    @Override
    Map getAnnotations() {
        super.annotations?.collectEntries { key, value ->
            [(key): value.payload]
        }
    }

    @JsonIgnore
    @Override
    Long getId() {
        super.id
    }

    @JsonIgnore
    @Override
    int getQuotedFieldsNum() {
        super.quotedFieldsNum
    }

    @JsonIgnore
    @Override
    List<Map<String, Object>> getDiscounts() {
        super.discounts
    }

    @JsonIgnore
    @Override
    Double getPaidAmount() {
        super.paidAmount
    }

    @JsonIgnore
    @Override
    String getOwnerMobile() {
        super.ownerMobile
    }

    @JsonIgnore
    @Override
    Double getDiscount() {
        super.discount
    }
}
