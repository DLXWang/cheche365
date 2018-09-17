package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.externalpayment.handler.PaymentStatusPollingHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import java.util.concurrent.ExecutorService

@Service
abstract class PaymentStatusPollingService {

    public static final POLLING_SUCCESS = 0
    public static final POLLING_CONTINUE = 1
    public static final POLLING_END = 2

    @Autowired
    @Qualifier("pollingExecutorService")
    private ExecutorService executorService

    abstract boolean support(QuoteSource quoteSource )

    abstract Map checkAndHandlePayStatus(PurchaseOrder order, String clientIdentifier)

    void poll(PurchaseOrder order, String clientIdentifier){
        executorService.execute(new PaymentStatusPollingHandler(order, this, clientIdentifier))
    }
}
