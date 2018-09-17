package com.cheche365.cheche.partner.api.bdinsur

import com.baidu.callback.DTO.response.baidu.Message
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import com.cheche365.cheche.partner.api.SyncOrderApi
import com.sun.jersey.api.client.ClientResponse
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.baidu.tool.JAXBTool.unmarshal
import static com.cheche365.cheche.core.model.ApiPartner.Enum.BDINSUR_PARTNER_50
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.PING_PLUS_BFB_25
import static javax.ws.rs.core.MediaType.TEXT_XML_TYPE

/**
 * Created by liheng on 2018/3/15 015.
 */
@Slf4j
@Service
abstract class BDInsurApi extends SyncOrderApi {

    @Autowired
    private PartnerOrderRepository partnerOrderRepository

    @Override
    ApiPartner apiPartner() {
        BDINSUR_PARTNER_50
    }

    @Override
    def getMediaType() {
        TEXT_XML_TYPE
    }

    @Override
    def serializeBody(model) {
        model
    }

    @Override
    def successCall(responseBody) {
        return null
    }

    @Override
    def handleResponse(apiPartner, ClientResponse response) {
        def responseInString = response.getEntity(String.class)
        if (200 != response.status) {
            throw new IllegalStateException("response code非200: ${response.status} ${responseInString}")
        }

        log.debug "api call response $responseInString"
        def message = unmarshal responseInString, Message.class

        if ('000000' != message?.Head?.ResponseCode) {
            throw new IllegalStateException(responseInString)
        }
        message
    }

    @Override
    List<PaymentChannel> getPaymentChannels(PurchaseOrder purchaseOrder) {
        def partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(purchaseOrder.id)
        partnerOrder?.state && '1' == new JsonSlurper().parseText(partnerOrder.state).payType ? [PING_PLUS_BFB_25] : []
    }

}
