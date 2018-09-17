package com.cheche365.cheche.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "bihu_insurance_info")
class BihuInsuranceInfo {

    @Id
    String id

    @Field("UserInfo")
    Object userInfo

    @Field("SaveQuote")
    Object saveQuote

    @Field("CustKey")
    String custKey

    @Field("BusinessStatus")
    Integer businessStatus

    @Field("StatusMessage")
    String statusMessage

}
