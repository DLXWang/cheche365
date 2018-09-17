package com.cheche365.cheche.partner.api.eqiao

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.partner.api.SyncOrderApi
import com.sun.jersey.api.representation.Form
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartner.Enum.EQIAO_PARTNER_32
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.*
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static java.lang.System.currentTimeMillis
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED
import static org.apache.commons.codec.digest.DigestUtils.md5Hex

/**
 * Created by liheng on 2017/7/7 007.
 */
@Slf4j
@Service
abstract class EQiaoApi extends SyncOrderApi {

    @Override
    ApiPartner apiPartner() {
        EQIAO_PARTNER_32
    }

    @Override
    def apiUrl(apiInput) {
        findByPartnerAndKey(apiPartner(apiInput), SYNC_ORDER_URL)?.value
    }

    @Override
    def getMediaType() {
        APPLICATION_FORM_URLENCODED
    }

    @Override
    def serializeBody(Object model) {
        model
    }

    @Override
    def successCall(responseBody) {
        '0' == responseBody?.returnCode
    }

    static def assembleForm(model) {

        def   jsonStr = CacheUtil.doJacksonSerialize(model, true)
        log.info '易桥订单：{} 同步加密前报文：{}', model.orderNo, jsonStr
        def ts = currentTimeMillis()
        def sign = md5Hex([findByPartnerAndKey(EQIAO_PARTNER_32, SYNC_APP_SECRET)?.value, ts, jsonStr].join(''))
        log.info '易桥签名：{}，ts：{}', sign, ts
        def form = new Form()
        form.add('system_code', findByPartnerAndKey(EQIAO_PARTNER_32, SYNC_APP_ID)?.value)
        form.add('param', jsonStr)
        form.add('ts', ts)
        form.add('sign', sign)
        form
    }
}
