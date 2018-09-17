package com.cheche365.cheche.partner.api.common

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.partner.api.SyncOrderApi
import com.sun.jersey.api.client.ClientResponse
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey

/**
 * Created by mahong on 06/03/2017.
 * 第三方订单同步-通用API
 */
@Service
@Slf4j
abstract class CommonApi extends SyncOrderApi {

    @Override
    ApiPartner apiPartner() {
        null
    }

    @Override
    String apiUrl(apiInput) {
        findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_URL)?.value
    }

    @Override
    def handleResponse(apiPartner, ClientResponse response) {
        def responseInString = response.getEntity(String.class)
        if (200 != response.status) {
            throw new IllegalStateException("response code非200: ${response.status} ${responseInString}")
        }

        log.debug("api call response $responseInString")
        def responseBody = new JsonSlurper().parseText(responseInString)

        def responseCode = (ApiPartner.Enum.RRYP_PARTNER_14 == apiPartner) ? "200" : 0
        if (responseCode != responseBody?.code) {
            throw new IllegalStateException(responseInString)
        }

        responseBody
    }

    @Override
    def successCall(Object responseBody) {
        return null
    }
}
