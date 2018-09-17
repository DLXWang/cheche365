package com.cheche365.cheche.developer.vo

import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.serializer.converter.Field
import org.codehaus.jackson.annotate.JsonIgnoreProperties

/**
 * @Author shanxf
 * @Date 2018/4/19  15:30
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class SyncOrder {

    String uid
    String state
    String orderNo
    Double payableAmount
    Double paidAmount
    InsuranceCompany insuranceCompany
    Date expireTime
    String payUrl
    String detailUrl
    OrderStatus status
    InsuredPerson insuredPerson
    Insurance insurance
    CompulsoryInsurance compulsoryInsurance
    List<Field> fields
    Auto auto
    Applicant applicant
    Total total
    List<Payment> payments
    Address deliveryAddress
    /**
     * 订单更新时候存在的字段
     */
    Date operateTime
}
