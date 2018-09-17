package com.cheche365.cheche.mock.controller

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.mock.service.MockCpicukService
import com.cheche365.cheche.web.counter.annotation.NonProduction
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by liushijie on 2018/6/6.
 *
 * 接收CpicUK（小鳄鱼）发送的查询请求，并返回相应的mock报文
 */
@RestController
@Slf4j
class MockCpicukServerResource {

    @Autowired
    MockCpicukService service

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    InsuranceRepository insuranceRepository

    @NonProduction
    @RequestMapping(value = "/ecar/**")
    def cpicUKPaymentStatusPollingMessage(HttpServletRequest request,HttpServletResponse response){

        def mockResponseMessages = service.getMockResponseMessage()
        def servletPath = request.servletPath.replace('/','_')

        if(servletPath.contains('getCaptchaImage')){
            def binaryBytes = MockCpicukServerResource.getResourceAsStream(mockResponseMessages.get(servletPath).filePath).getBytes()
            response.contentType = "image/jpeg"
            response.outputStream.write(binaryBytes)
            service.mockDeCaptchaService()
        }else{
            if (servletPath.contains('paymentrecord_query')){
                def requestBody = request.reader.text
                String itpNo = new JsonSlurper().parseText(requestBody).redata.payNo
                Payment payment = paymentRepository.findFirstByItpNo(itpNo)
                Insurance insurance = insuranceRepository.findByQuoteRecordId(payment.purchaseOrder.objId)
                CompulsoryInsurance ci =  compulsoryInsuranceRepository.findByQuoteRecordId(payment.purchaseOrder.objId)

                def startDate = (new Date() + 1).format('yyyy-MM-dd')
                def endDate = (new Date() + 365).format('yyyy-MM-dd')
                def responseBody = mockResponseMessages.get(servletPath)
                responseBody.result[0].insuredNo = ci?.proposalNo ?: UUID.randomUUID().toString().replaceAll('-','')
                responseBody.result[0].policyNo = ci?.policyNo ?: UUID.randomUUID().toString().replaceAll('-','')
                responseBody.result[0].beginDate = ci?.effectiveDate ? ci.effectiveDate.format('yyyy-MM-dd') : startDate
                responseBody.result[0].endDate = ci?.expireDate ? ci.expireDate.format('yyyy-MM-dd'): endDate

                responseBody.result[1].insuredNo = insurance?.proposalNo ?: UUID.randomUUID().toString().replaceAll('-','')
                responseBody.result[1].policyNo = insurance?.policyNo ?: UUID.randomUUID().toString().replaceAll('-','')
                responseBody.result[1].beginDate = insurance?.effectiveDate ? insurance.effectiveDate.format('yyyy-MM-dd') : startDate
                responseBody.result[1].endDate = insurance?.expireDate ? insurance.expireDate.format('yyyy-MM-dd'): endDate

            }
            response.contentType = 'application/json'
            response.writer.print(new ObjectMapper().writeValueAsString(mockResponseMessages.get(servletPath)))
        }
    }
}
