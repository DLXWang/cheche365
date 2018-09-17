package com.cheche365.cheche.mock.service

import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.mock.api.botpy.MockBotpyCallBackAPI
import com.cheche365.cheche.web.service.http.SpringHTTPContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.OrderSourceType.Enum.PLANTFORM_BOTPY_8
import static com.cheche365.cheche.mock.util.DataFileParserUtil.BOTPY_PAYMENT_INFO_CALLBACK
import static com.cheche365.cheche.mock.util.DataFileParserUtil.BOTPY_PROPOSALS_STATUS
import static com.cheche365.cheche.mock.util.DataFileParserUtil.BOTOY_PAYMENT_STATUS_CALLBACK
import static com.cheche365.cheche.mock.util.DataFileParserUtil.BOTPY_STATUS_CHANGE_CALLBACK
import static com.cheche365.cheche.mock.util.DataFileParserUtil.model

@Service
class MockBotpyService {

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository

    @Autowired
    CompulsoryInsuranceRepository compulsoryInsuranceRepository

    @Autowired
    InsuranceRepository insuranceRepository

    @Autowired
    SpringHTTPContext httpContext

    def paymentStatusCallback(String orderNo, boolean paySuccess){
        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        def bodyData = model(BOTOY_PAYMENT_STATUS_CALLBACK)
        bodyData.proposal_id = order.orderSourceId
        bodyData.data.paid = paySuccess
        bodyData.notification_id = UUID.randomUUID().toString().replaceAll('-','')

        def result = new MockBotpyCallBackAPI().call(bodyData, httpContext)
        [callbackBody:bodyData, response:result]
    }

    def paymentInfoCallback(String orderNo){
        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        def bodyData = model(BOTPY_PAYMENT_INFO_CALLBACK)
        bodyData.proposal_id = order.orderSourceId
        bodyData.notification_id = UUID.randomUUID().toString().replaceAll('-','')
        bodyData.data.payment_trade_no = UUID.randomUUID().toString().replaceAll('-','')

        def result = new MockBotpyCallBackAPI().call(bodyData, httpContext)
        [callbackBody:bodyData, response:result]
    }


    def statusChangeCallback(String orderNo, String status = 'PAID'){
        PurchaseOrder order = purchaseOrderRepository.findFirstByOrderNo(orderNo)
        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.objId)
        CompulsoryInsurance ci = compulsoryInsuranceRepository.findByQuoteRecordId(order.objId)
        def bodyData = model(BOTPY_STATUS_CHANGE_CALLBACK)
        bodyData.proposal_id = ''
        bodyData.data.records[0].proposal_id = order.orderSourceId
        bodyData.data.records[0].ic_nos.biz_prop = insurance?.proposalNo ?: UUID.randomUUID().toString().replaceAll('-','')
        bodyData.data.records[0].ic_nos.biz_policy = insurance?.policyNo ?: UUID.randomUUID().toString().replaceAll('-','')
        bodyData.data.records[0].ic_nos.force_prop = ci?.proposalNo ?: UUID.randomUUID().toString().replaceAll('-','')
        bodyData.data.records[0].ic_nos.force_policy = ci?.policyNo ?: UUID.randomUUID().toString().replaceAll('-','')
        bodyData.data.records[0].proposal_status = status
        bodyData.data.records[0].old_proposal_status = null

        def result = new MockBotpyCallBackAPI().call(bodyData, httpContext)
        [callbackBody:bodyData, response:result]
    }



    def proposalStatusResponse(String proposalId){

        PurchaseOrder order = purchaseOrderRepository.findByOrderSourceId(proposalId, PLANTFORM_BOTPY_8).first()
        Insurance insurance = insuranceRepository.findByQuoteRecordId(order.objId)
        CompulsoryInsurance ci = compulsoryInsuranceRepository.findByQuoteRecordId(order.objId)

        def startDate = (new Date() + 1).format('yyyy-MM-dd')
        def endDate = (new Date() + 365).format('yyyy-MM-dd')

        def responseBody = model(BOTPY_PROPOSALS_STATUS)
        responseBody.proposal_id = proposalId
        responseBody.ic_nos.biz_prop = insurance?.proposalNo ?: UUID.randomUUID().toString().replaceAll('-','')
        responseBody.ic_nos.biz_policy = insurance?.policyNo ?: UUID.randomUUID().toString().replaceAll('-','')
        responseBody.biz_start_date = insurance?.effectiveDate ? insurance.effectiveDate.format('yyyy-MM-dd') : startDate
        responseBody.biz_end_date = insurance?.expireDate ? insurance.expireDate.format('yyyy-MM-dd'): endDate
        responseBody.ic_nos.force_prop = ci?.proposalNo ?: UUID.randomUUID().toString().replaceAll('-','')
        responseBody.ic_nos.force_policy = ci?.policyNo ?: UUID.randomUUID().toString().replaceAll('-','')
        responseBody.force_start_date = ci?.effectiveDate ? ci.effectiveDate.format('yyyy-MM-dd') : startDate
        responseBody.force_end_date = ci?.expireDate ? ci.expireDate.format('yyyy-MM-dd'): endDate

        responseBody
    }


    def asyncPaymentStatusCallback(String proposalId){
        PurchaseOrder order = purchaseOrderRepository.findByOrderSourceId(proposalId, PLANTFORM_BOTPY_8).first()
        sleep(2000)
        new Thread(new Runnable(){
            @Override
            void run() {
                paymentStatusCallback(order.orderNo, true)
            }
        }).start()
    }

    def asyncPaymentInfoCallback(String proposalId){
        PurchaseOrder order = purchaseOrderRepository.findByOrderSourceId(proposalId, PLANTFORM_BOTPY_8).first()
        sleep(2000)
        new Thread(new Runnable(){
            @Override
            void run() {
                paymentInfoCallback(order.orderNo)
            }
        }).start()
    }

}
