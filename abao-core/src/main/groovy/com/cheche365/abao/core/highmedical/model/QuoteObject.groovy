package com.cheche365.abao.core.highmedical.model

import com.cheche365.cheche.core.model.abao.InsurancePerson
import com.cheche365.cheche.core.model.abao.InsuranceProduct
import com.cheche365.cheche.core.model.abao.InsuranceQuoteField
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical
import groovy.transform.ToString

import static com.cheche365.cheche.common.util.ContactUtils.getAgeByBirthday


/**
 * 高端医疗报价对象
 * Created by suyq on 2016/12/21.
 */
@Canonical
@ToString(includeNames = true, ignoreNulls = true)
@JsonIgnoreProperties
class QuoteObject {

    private static final _AMOUNT_PACKAGE_MAPPINGS = [
        100_000D: '标准计划',
        200_000D: '升级计划',
        300_000D: '尊贵计划'
    ]

    InsuranceProduct insuranceProduct

    List<InsuranceQuoteField> insuranceQuoteFields

    InsurancePerson insurancePerson

    Map additionalParameters = [:]

    @JsonIgnore
    int getInsurancePersonAge() {
        getAgeByBirthday insurancePerson.birthday
    }

    /**
     * 根据投保的意外身故及伤残保额，判断投保计划类型
     * 标准计划:100,000；升级计划：200,000；尊贵计划：300,000；
     */
    @JsonIgnore
    String getPackageType() {
        def accidentalAmount = insuranceQuoteFields.find { field ->
            'accidentalMedical' == field.insuranceField.code
        }?.amount?.with { amount ->
            amount as double
        } ?: 0D


        _AMOUNT_PACKAGE_MAPPINGS[accidentalAmount]
    }

}


