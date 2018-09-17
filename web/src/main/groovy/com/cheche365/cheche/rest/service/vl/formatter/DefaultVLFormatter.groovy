package com.cheche365.cheche.rest.service.vl.formatter

import com.cheche365.cheche.core.model.InsuranceBasicInfo
import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.serializer.SerializerUtil
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.exception.Constants.getFIELD_ORDER_TOC
import static com.cheche365.cheche.core.exception.Constants.getVL_CLIENT_VISIBLE_FIELD
import static com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler.formatFields
import static com.cheche365.cheche.core.util.CacheUtil.toJSONPretty

@Service
@Order(5)
@Slf4j
class DefaultVLFormatter extends VLFormatter {

    @Autowired
    QuoteRecordCacheService cacheService

    @Override
    def support(context) {
        context.apiVersion >= 'v1.6'
    }

    @Override
    def insuranceInfoToMap(context) {
        log.debug("支持简化报价流程 VL使用InsuranceInfo结构")

        InsuranceInfo iInfo = context.iInfo ?: new InsuranceInfo()
        VehicleLicense vehicleLicense = iInfo.vehicleLicense ?: new VehicleLicense()
        InsuranceBasicInfo insuranceBasicInfo = iInfo.insuranceBasicInfo ?: new InsuranceBasicInfo()

        def vlMap = SerializerUtil.toMapExceptClass(vehicleLicense)
        def extraFields = context.extraFields
        if (extraFields) {
            vlMap."$extraFields" = null
            log.debug('格式化参数额外字段', extraFields)
        }
        def original = [:]
        original << vlMap
        original << SerializerUtil.toMapExceptClass(insuranceBasicInfo)
        original << [code: vehicleLicense.brandCode]
        fillSingleSelectionOptions(iInfo, original)
        context.original = original
        log.debug('格式化参数前 {}', toJSONPretty(original))

        context.sortedVL = formatFields(original, visibleFields())

        Map iInfoMap = [
            vehicleLicenseInfo: context.sortedVL,
            enoughToQuote     : enoughToQuote(context)
        ]
        log.debug('格式化参数后 {}', toJSONPretty(iInfoMap))

        iInfoMap.enoughToQuote ?: cacheService.setEnoughToQuoteFlag(context.request.session.id, true)

        iInfoMap
    }

    @Override
    def needEncryptVL(Object context) {
        (context.photeQuote) ? false : true
    }

    def enoughToQuote(context){
        def notEnoughToQuote = context.sortedVL.findAll { quoteEnoughFields().contains(it.key) }.any { !it.originalValue }
        (!notEnoughToQuote && !context.original.containsKey("transferDate"))
    }

    def visibleFields() {
        FIELD_ORDER_TOC
    }

    def quoteEnoughFields() {
        VL_CLIENT_VISIBLE_FIELD
    }

    private static fillSingleSelectionOptions(InsuranceInfo info, LinkedHashMap original) {
        def autoModelOptions = info.metaInfo?.autoModel
        original.autoModel = [
            originalValue: autoModelOptions ? autoModelOptions[0].value : null,
            options      : autoModelOptions
        ]

        def dictionaries = AutoService.getDictionaries()
        original.fuelType = [
            originalValue: original.fuelType?.id,
            options      : dictionaries.fuelTypes
        ]
        original.useCharacter = [
            originalValue: original.useCharacter?.id,
            options      : dictionaries.useCharacters
        ]
        original.parentIdentityType = [
            originalValue: original.identityType?.parent?.id,
            options      : dictionaries.parentIdentityTypes
        ]
        original.identityType = [
            originalValue: original.identityType?.id,
            options      : dictionaries.identityTypes
        ]
    }
}
