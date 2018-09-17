package com.cheche365.abao.core.highmedical.model

import com.cheche365.cheche.core.model.abao.InsuranceQuote
import groovy.transform.Canonical
import groovy.transform.ToString


/**
 * 高端医疗报价结果
 * Created by suyq on 2016/12/21.
 */
@Canonical
@ToString(includeNames = true, ignoreNulls = true)
class QuoteResult {

    List<InsuranceQuote> insuranceQuotes

    Map metaInfo

}
