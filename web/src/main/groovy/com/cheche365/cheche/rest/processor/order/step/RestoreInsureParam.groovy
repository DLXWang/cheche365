package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 12/22/16.
 */

@Component
@Slf4j
class RestoreInsureParam implements TPlaceOrderStep {

    @Override
    def run(Object context) {

        QuoteRecord quoteRecord = context.quoteRecord
        InsuranceRepository insuranceRepository = context.insuranceRepository
        CompulsoryInsuranceRepository compulsoryInsuranceRepository = context.compulsoryInsuranceRepository

        context.insurance = insuranceRepository.findByQuoteRecordId(quoteRecord.id)
        context.compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId(quoteRecord.id)

        getContinueFSRV true
    }

}
