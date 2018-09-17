package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.QuoteSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PollingServiceFactory {

    @Autowired
    private List<PaymentStatusPollingService> pollingServices

    PaymentStatusPollingService getService(QuoteSource quoteSource){
        pollingServices.find {it.support(quoteSource)}
    }
}
