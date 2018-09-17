package com.cheche365.cheche.core.model

import org.springframework.data.mongodb.core.mapping.Document

import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * @Author shanxf
 * @Date 2018/4/21  11:18
 */
@Document(collection = "moSyncOrderLog")
class MoSyncOrderLog {

    @Id
    String id
    Date createTime
    @ManyToOne
    OrderStatus orderStatus
    String orderNo
    Object requestBody
    String signHeader

}
