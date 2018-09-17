package com.cheche365.cheche.bihu.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical
import groovy.transform.ToString


/**
 * 壁虎报价对象
 * Created by suyq on 2017/11/22.
 */
@Canonical
@ToString(includeNames = true, ignoreNulls = true)
@JsonIgnoreProperties
class QuoteObject {

    def quoteRecord

    def additionalParameters

}


