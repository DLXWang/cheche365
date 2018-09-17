package com.cheche365.cheche.rest.service.vl.formatter

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.web.model.useragent.UserAgentHeader
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.exception.Constants.getFIELD_ORDER
import static com.cheche365.cheche.core.model.Channel.Enum.ANDROID_CHEBAOYI_222
import static com.cheche365.cheche.core.model.Channel.Enum.IOS_CHEBAOYI_221
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMapExceptClass
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.getEncryptionInsuranceBasicInfoKey
import static com.cheche365.cheche.core.util.AutoUtils.encrypt

@Service
@Order(3)
@Slf4j
class ToAOldVLFormatter extends DefaultVLFormatter {

    @Autowired
    private QuoteRecordCacheService cacheService

    @Override
    def support(context) {
        Channel channel = context.channel
        Boolean selfApp = Channel.selfApp().contains(channel)
        UserAgentHeader userAgent = ClientTypeUtil.getUserAgentHeaderByRequest(context.request)
        Boolean iosNewVersion = (IOS_CHEBAOYI_221 == channel) && userAgent?.getVer()?.compareTo("1.2.1") >= 0
        Boolean androidNewVersion = (ANDROID_CHEBAOYI_222 == channel) && userAgent?.getVer()?.compareTo("1.2.0") >= 0
        return channel.isStandardAgent() && (!selfApp || (selfApp && (iosNewVersion || androidNewVersion)))
    }

    @Override
    def needFilterPublicAuto(Object context) {
        false
    }

    @Override
    def needFillAutoModels(Object context) {
        (context.photeQuote) ? false : true
    }

    @Override
    def visibleFields() {
        FIELD_ORDER
    }

    @Override
    def quoteEnoughFields() {
        FIELD_ORDER
    }

    @Override
    def insuranceInfoToMap(Object context) {
        Map iInfoMap = super.insuranceInfoToMap(context)
        iInfoMap.vehicleLicenseInfo.find { it.key == 'identity' }?.with {
            it.fieldLabel = '证件号码'
        }

        def insuranceBasicInfo = null
        if (context.iInfo.insuranceBasicInfo) {
            cacheService.cacheEncryptionInsuranceBasicInfo getEncryptionInsuranceBasicInfoKey(context.request.session.id), context.iInfo.insuranceBasicInfo
            encrypt context.iInfo.insuranceBasicInfo

            def basicInfoMap = toMapExceptClass context.iInfo.insuranceBasicInfo
            def personnelInfo = basicInfoMap.subMap('applicantName', 'applicantIdNo', 'applicantIdentityType', 'applicantMobile', 'insuredName', 'insuredIdNo', 'insuredIdentityType', 'insuredMobile')
            insuranceBasicInfo = [
                personnelInfo   : personnelInfo.any {
                    !(it.key in ['applicantIdentityType', 'insuredIdentityType']) && it.value
                } ? personnelInfo : null,
                packages        : basicInfoMap.insurancePackage,
                insuranceCompany: basicInfoMap.insuranceCompany
            ]
        }

        iInfoMap.insuranceBasicInfo = insuranceBasicInfo.any { it.value } ? insuranceBasicInfo : null
        iInfoMap
    }

    @Override
    def enoughToQuote(Object context) {
        false
    }
}
