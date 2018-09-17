package com.cheche365.cheche.parser.dto

import com.cheche365.cheche.core.model.QuoteRecord
import groovy.transform.Canonical

@Canonical
class QuotingRequestObject extends InsuranceRequestObject {

    QuoteRecord quoteRecord

}
