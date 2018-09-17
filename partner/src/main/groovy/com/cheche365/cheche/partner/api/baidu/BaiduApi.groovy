package com.cheche365.cheche.partner.api.baidu

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.partner.api.SyncOrderApi
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.api.client.filter.LoggingFilter
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.ws.rs.core.MediaType

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_ORDER_URL
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey

@Slf4j
@Service
abstract class BaiduApi extends SyncOrderApi {

    @Override
    ApiPartner apiPartner() {
        ApiPartner.Enum.BAIDU_PARTNER_2
    }

    @Override
    def getMediaType() {
        return MediaType.APPLICATION_FORM_URLENCODED
    }

    @Override
    def serializeBody(Object model) {
        return model
    }

    @Override
    def successCall(responseBody) {
        0 == responseBody?.err_no
    }

    @Override
    String apiUrl(apiInput) {
        return findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_URL)?.value
    }

    @Override
    WebResource apiClient(apiInput) {
        Client client = Client.create() //单例报错
        client.addFilter(new LoggingFilter(java.util.logging.Logger.getLogger(BaiduApi.class.getName())))
        client.resource(apiUrl(apiInput))
    }
}
