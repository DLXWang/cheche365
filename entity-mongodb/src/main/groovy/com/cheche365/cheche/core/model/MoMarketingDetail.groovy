package com.cheche365.cheche.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by taichangwei on 2018/5/16.
 */
@Document(collection = "moMarketingDetail")
class MoMarketingDetail {
    @Id
    String id
    String marketingCode
    Object message
}
