package com.cheche365.cheche.parser.dto

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import groovy.transform.Canonical

@Canonical
class InsuringRequestObject extends InsuranceRequestObject {

    PurchaseOrder order

    Insurance insurance

    CompulsoryInsurance compulsoryInsurance

    QuoteRecord quoteRecord

}
