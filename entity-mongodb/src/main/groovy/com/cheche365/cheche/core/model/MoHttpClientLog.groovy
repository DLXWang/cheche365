package com.cheche365.cheche.core.model

import groovy.transform.Canonical
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Canonical
@Document(collection = "moHttpClientLog")
class MoHttpClientLog {

    @Id
    String id
    LogType logType
    Object logMessage
    String objTable
    String objId
    Date createTime

}
