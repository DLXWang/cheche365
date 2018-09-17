package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.mock.service.MockBotpyService
import groovy.util.logging.Slf4j
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

@RestController
@Slf4j
class MockBotpyServerResource {

    @Autowired
    MockBotpyService mockBotpyService

    //接收金斗云查询投保单号请求
    @RequestMapping("proposals/{proposalId}")
    def proposalsStatus(@PathVariable('proposalId')String proposalId, HttpServletResponse response){

        def responseBody = mockBotpyService.proposalStatusResponse(proposalId)

        response.contentType = 'application/json'
        response.writer.println(new ObjectMapper().writeValueAsString(responseBody))
        response.flushBuffer()
    }

    //接收金斗云查询支付状态请求
    @RequestMapping("proposals/{proposalId}/requests")
    def paymentStatus(@PathVariable('proposalId')String proposalId, @RequestBody Map param, HttpServletResponse response) {

        //异步响应
        if ('payment'== param?.type){
            mockBotpyService.asyncPaymentInfoCallback(proposalId)
        } else {
            mockBotpyService.asyncPaymentStatusCallback(proposalId)
        }

        //同步响应
        def responseBody = ["notification_id": UUID.randomUUID().toString().replaceAll('-','')]
        response.contentType = 'application/json'
        response.writer.println(new ObjectMapper().writeValueAsString(responseBody))
        response.flushBuffer()
    }


}
