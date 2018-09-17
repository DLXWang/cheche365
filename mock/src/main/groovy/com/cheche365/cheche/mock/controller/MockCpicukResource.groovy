package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.externalpayment.service.AgentParserStatusService
import com.cheche365.cheche.mock.util.MockSessionUtil
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.counter.annotation.NonProduction
import com.cheche365.cheche.web.service.http.SpringHTTPContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by liushijie on 2018/6/6.
 *
 * 接收CpicUK（小鳄鱼）发送的查询请求，并返回相应的mock报文
 */
@RestController
@RequestMapping("/v1.6/mock/cpicuk")
class MockCpicukResource extends ContextResource {

    @Autowired
    AgentParserStatusService agentParserStatusService

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    SpringHTTPContext httpContext

    @NonProduction
    @RequestMapping(value = "payment/{orderNo}", method = RequestMethod.POST)
    def payment(@PathVariable(value = "orderNo")String orderNo){

        MockSessionUtil.addMockUrl(session, httpContext)

        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        def result = agentParserStatusService.checkAndHandlePayStatus(order, session.id)

        MockSessionUtil.removeMockUrl(session, httpContext)

        getResponseEntity(result)


    }
}
