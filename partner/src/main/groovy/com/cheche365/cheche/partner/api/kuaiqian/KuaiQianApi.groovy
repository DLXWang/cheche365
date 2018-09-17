package com.cheche365.cheche.partner.api.kuaiqian

import com.alibaba.fastjson.JSONObject
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.partner.api.SyncOrderApi
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_ID
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_SECRET
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey

/**
 * Created by shanxf on 2017/7/20.
 */
@Slf4j
@Service
abstract class KuaiQianApi extends SyncOrderApi {


    @Override
    ApiPartner apiPartner() {
        return ApiPartner.Enum.KUAIQIAN_PARTNER_33
    }


    @Override
    def successCall(responseBody) {
        responseBody?.errCode == "01"
    }

    def signParam(rawData, converter) {
        converter = encodeIdentityMobile(converter)
        converter.appKey = findByPartnerAndKey(apiPartner(), SYNC_APP_ID)?.value
        converter.timestamp = System.currentTimeMillis()
        if (OrderStatus.Enum.memberCodeStatus().contains(rawData?.purchaseOrder?.status) || isPartialRefund(rawData)) {
            converter.memberCode = rawData?.partnerUser?.partnerId
        }
        converter.sign = SignUtil.addSign(JSONObject.parse(serializeBody(converter)), findByPartnerAndKey(apiPartner(), SYNC_APP_SECRET)?.value)
        converter
    }

    def encodeIdentityMobile(converter) {
        if (converter?.insuredPerson?.insuredIdNo) {
            converter.insuredPerson.insuredIdNo = DesEncryptUtil.encode(converter?.insuredPerson?.insuredIdNo)
            converter.auto.identity = DesEncryptUtil.encode(converter?.auto?.identity)
            converter.deliveryAddress.mobile = DesEncryptUtil.encode(converter?.deliveryAddress?.mobile)
            converter.applicant.applicantMobile = DesEncryptUtil.encode(converter?.applicant?.applicantMobile)
            converter.applicant.applicantIdNo = DesEncryptUtil.encode(converter?.applicant?.applicantIdNo)
            converter.insuredPerson.insuredMobile = DesEncryptUtil.encode(converter?.insuredPerson?.insuredMobile)
        }
        converter
    }

    Boolean isPartialRefund(Object partnerOrder) {
        Boolean.FALSE
    }
}
