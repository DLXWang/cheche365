package com.cheche365.cheche.partner.api

import com.cheche365.cheche.core.message.NotifyEmailMessage
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderSyncRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.partner.service.order.PartnerOrderService
import com.cheche365.cheche.partner.utils.EmailTemplate
import com.cheche365.cheche.partner.web.ClientFactory
import com.cheche365.cheche.partner.web.filter.MoLoggingFilter
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientHandler
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.filter.ClientFilter
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.core.MediaType

/**
 * Created by zhengwei on 7/11/16.
 */
@Slf4j
abstract class PartnerApi {

    @Autowired
    private PartnerOrderSyncRepository partnerOrderSyncRepository

    @Autowired
    private RedisPublisher redisPublisher

    @Autowired
    protected PartnerOrderService partnerOrderService

    @Autowired
    public PaymentRepository paymentRepository

    abstract ApiPartner apiPartner()

    abstract ApiPartner apiPartner(apiInput)

    abstract apiUrl(apiInput)

    abstract prepareData(rawData)

    abstract successCall(responseBody)

    WebResource apiClient(apiInput) {
        Client client = ClientFactory.getInstance(apiPartner(apiInput))
        client.resource(apiUrl(apiInput))
        configLogging(client, apiInput).resource(apiUrl(apiInput))
    }

    def call(dataMap) {
        WebResource apiClientResource
        def afterConvertToJson
        ClientResponse clientResponse
        PartnerOrder apiInput
        try {
            apiInput=dataMap.partnerOrder
            apiClientResource = apiClient(apiInput)
            def afterConvert
            if(dataMap.containsKey("payments")){
                afterConvert = prepareData(dataMap)
            }else {
                afterConvert=prepareData(apiInput)
            }
            afterConvertToJson = serializeBody(afterConvert)

            clientResponse = apiClientResource.type(getMediaType()).post(ClientResponse.class, afterConvertToJson)
            handleResponse(apiPartner(apiInput), clientResponse)
            Boolean.TRUE
        } catch (Exception e) {
            sendEmail(e, apiInput)
            log.error "同步第三方订单出错, 第三方code:{}, 订单号:{}, 错误信息:{}", apiPartner(apiInput)?.code, apiInput.purchaseOrder?.orderNo, e.message
        } finally {
            if (clientResponse) {
                clientResponse.close()
            }
        }
    }

    def prepareDataNew(rawData){
        prepareData(rawData)
    }

    def newCall(dataMap) {
        WebResource apiClientResource
        def afterConvertToJson
        ClientResponse clientResponse
        PartnerOrder partnerOrder
        try {
            partnerOrder=dataMap.partnerOrder

            apiClientResource = apiClient(partnerOrder)

            def afterConvert = prepareDataNew(dataMap)
            afterConvertToJson = serializeBody(afterConvert)

            clientResponse = apiClientResource.type(getMediaType()).post(ClientResponse.class, afterConvertToJson)
            handleResponse(apiPartner(partnerOrder), clientResponse)
            Boolean.TRUE
        } catch (Exception e) {
            sendEmail(e, partnerOrder)
            log.error "同步第三方订单出错, 第三方code:{}, 订单号:{}, 错误信息:{}", apiPartner(partnerOrder)?.code, partnerOrder.purchaseOrder?.orderNo, e.message
        } finally {
            if (clientResponse) {
                clientResponse.close()
            }
        }
    }

    def getMediaType() {
        return MediaType.APPLICATION_JSON_TYPE
    }

    def serializeBody(Object model) {

        return CacheUtil.doJacksonSerialize(model, true)

    }

    def handleResponse(apiPartner, ClientResponse response) {
        def responseInString = response.getEntity(String.class)
        if (200 != response.status) {
            throw new IllegalStateException("response code非200: ${response.status} ${responseInString}")
        }

        log.debug("api call response $responseInString")
        def responseBody = new JsonSlurper().parseText(responseInString)
        if (!successCall(responseBody)) {
            throw new IllegalStateException(responseInString)
        }
        responseBody
    }

    abstract Map toLogParam(apiInput)

    def configLogging(Client client, apiInput) {
        ClientHandler handler = client.getHeadHandler();
        while (handler != null) {
            if (!(handler instanceof ClientFilter)) {
                break;
            }
            ClientFilter filter = (ClientFilter) handler;

            if (filter instanceof MoLoggingFilter) {
                ((MoLoggingFilter) filter).setAdditionalParam(toLogParam(apiInput));
                break;
            }
            handler = filter.getNext();
        }
        return client
    }

    def sendEmail(Exception e, apiInput) {
        def bindingPublic = [
            l1: RuntimeUtil.getEvnProfile(),
            l2: InetAddress.getLocalHost().getHostName().toString()
        ]
        def engine = new SimpleTemplateEngine()
        def templatePublic = engine.createTemplate(EmailTemplate.textPublic).make(bindingPublic)
        String emailMessage = templatePublic.toString() + genApiInputDetailInfo(apiInput) + ExceptionUtils.getStackTrace(e).toString()
        redisPublisher.publish(new NotifyEmailMessage(emailMessage));
    }

    def genApiInputDetailInfo(apiInput) {
        ''
    }

    public List<Payment> getPayments(rawData) {
        List<Payment> payments
        if (rawData instanceof PartnerOrder) {
            payments = paymentRepository.findByPurchaseOrder(rawData.purchaseOrder)
        } else {
            payments =CacheUtil.doListJacksonDeserialize(CacheUtil.doJacksonSerialize(rawData.payments),Payment)
        }
        payments
    }

    List<PaymentChannel> getPaymentChannels(PurchaseOrder purchaseOrder) {
        []
    }

}
