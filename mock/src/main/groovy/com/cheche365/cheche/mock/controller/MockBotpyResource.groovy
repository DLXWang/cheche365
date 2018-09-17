package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.externalapi.api.botpy.BotpyCitiesAPI
import com.cheche365.cheche.externalapi.api.botpy.BotpyGetAccountAPI
import com.cheche365.cheche.externalapi.api.botpy.BotpyProposalStatusAPI
import com.cheche365.cheche.mock.service.MockBotpyService
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.response.RestResponseEnvelope
import com.cheche365.cheche.web.service.http.SpringHTTPContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by wenling on 3/16/18.
 */

@RestController
@RequestMapping("/v1.6/mock/botpy")
class MockBotpyResource extends ContextResource {

    @Autowired
    BotpyProposalStatusAPI proposalStatusAPI

    @Autowired
    BotpyCitiesAPI botpyCitiesAPI

    @Autowired
    SpringHTTPContext springHTTPContext

    @Autowired
    BotpyGetAccountAPI botpyGetAccountAPI

    @Autowired
    MockBotpyService mockBotpyService


    @NonProduction
    @RequestMapping(value="/{proposalId}", method= RequestMethod.GET)
    HttpEntity<RestResponseEnvelope> proposalStatus(@PathVariable(value = "proposalId") String proposalId){
        def statusResult = proposalStatusAPI.call(proposalId)
        getResponseEntity(statusResult)
    }

    @NonProduction
    @RequestMapping(value="/cities",method= RequestMethod.GET)
    HttpEntity<RestResponseEnvelope> getCities(){
        def cities = botpyCitiesAPI.call()
        getResponseEntity(cities)
    }

    @NonProduction
    @RequestMapping(value="account/{accountId}", method= RequestMethod.GET)
    HttpEntity<RestResponseEnvelope> getAccount(@PathVariable(value = "accountId") String accountId){
        def statusResult = botpyGetAccountAPI.call(accountId)
        getResponseEntity(statusResult)
    }

    @NonProduction
    @RequestMapping(value = "paymentStatus/{orderNo}/{paid}",method = RequestMethod.POST)
    def paymentStatusCallback(@PathVariable("orderNo") String orderNo, @PathVariable("paid") Boolean paid){
        def result = mockBotpyService.paymentStatusCallback(orderNo, paid)
        getResponseEntity(result)
    }

    @NonProduction
    @RequestMapping(value = "statusChange/{orderNo}/{status}",method = RequestMethod.POST)
    def statusChangeCallback(@PathVariable("orderNo")String orderNo, @PathVariable("status")String status){
        def result = mockBotpyService.statusChangeCallback(orderNo, status)
        getResponseEntity(result)
    }

}
