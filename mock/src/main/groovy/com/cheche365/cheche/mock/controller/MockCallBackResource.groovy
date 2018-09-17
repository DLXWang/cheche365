package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.externalpayment.service.ZaOrderQueryServices
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.payment.PaymentService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1.6/mock/callback")
@Slf4j
class MockCallBackResource extends ContextResource {

    @Autowired
    private PaymentService paymentService
    @Autowired
    ZaOrderQueryServices zaOrderQueryServices

    @NonProduction
    @RequestMapping(value = "query",method = RequestMethod.GET)
    persistApplicant(@RequestParam(value = "taskId") String taskId){
        paymentService.initOR { poRepo ->
            poRepo.findByOrderSourceId(taskId, OrderSourceType.Enum.PLANTFORM_BX_5)[0]
        }
    }

    @NonProduction
    @RequestMapping(value = "zaQuery",method = RequestMethod.POST)
    persistZaQuery(@RequestBody Map attrs){
        zaOrderQueryServices.query(new PurchaseOrder(policyNo:attrs.policyNo))
    }
}
