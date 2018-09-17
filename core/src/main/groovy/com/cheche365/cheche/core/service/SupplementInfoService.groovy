package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.Constants
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSupplementInfo
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.SerializationUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

import static com.cheche365.cheche.core.exception.Constants.getEXCEPTION_TEMPLATE_NEW
import static com.cheche365.cheche.core.exception.Constants.getFIND_INDEX
import static com.cheche365.cheche.core.exception.Constants.getNON_SELF_EXPRESS_TYPES
import static com.cheche365.cheche.core.exception.Constants.getCAPTCHA_IMAGE_FIELD
import static com.cheche365.cheche.core.exception.Constants.getPERSIST_FIELD_JSON
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.captchaImageFlagKey

/**
 * Created by chenqiuchang on 2017/4/5.
 */

@Service
@Slf4j
class SupplementInfoService {

    private static String OPTIONS_SOURCE_BY_CODE = 'byCode'
    private static String OPTIONS_SOURCE_BY_VIN_NO = 'byVinNo'
    private static String ESCAPE_CHAR = '_#_'
    private QuoteRecordCacheService quoteRecordCacheService

    SupplementInfoService(QuoteRecordCacheService quoteRecordCacheService) {
        this.quoteRecordCacheService = quoteRecordCacheService
    }

    static def formatSelectedAutoModel(def parameters, def company, def quoteSource) {
        def autoModel = parameters.autoModel
        def selectedOption = null
        if (parameters.optionsSource) {
            selectedOption = autoModel.options[parameters.optionsSource]?.find { it.value.contains(autoModel.selected) }
        }

        if (!selectedOption) {
            autoModel.options.any { optionsSource, options ->
                options?.any {
                    if (it.value.contains(autoModel.selected)) {
                        parameters.optionsSource = optionsSource
                        selectedOption = it
                        return true
                    }
                }
            }
        }

        if (selectedOption) {
            log.debug("quote success, format selected auto model, optionsSource:{}, value:{}", parameters.optionsSource, selectedOption.value)
            autoModel.selected = [
                optionsSource: parameters.optionsSource,
                text         : selectedOption.text,
                shortText    : selectedOption.shortText,
                value        : selectedOption.value
            ]
        } else {
            log.error("format selected auto model error, can not find value:{} from options", autoModel.selected)
        }
    }

    static def formatAutoModelSupplementInfo(def company, def autoModelOptions, Boolean turnOffAutoModelMatch, def quoteSource = null) {
        if (!turnOffAutoModelMatch) {
            formatAutoModelOptions([optionsSource: OPTIONS_SOURCE_BY_CODE], autoModelOptions, company, quoteSource)
        } else {
            def extraMeta = [:]
            Boolean extraProcessForParser = !(autoModelOptions instanceof Map)
            if (extraProcessForParser) {
                extraMeta << [extraProcessForParser: extraProcessForParser]
                autoModelOptions = [
                    (OPTIONS_SOURCE_BY_CODE)  : autoModelOptions,
                    (OPTIONS_SOURCE_BY_VIN_NO): []
                ]
            }

            QuoteRecordCacheService cacheService = ApplicationContextHolder.getApplicationContext().getBean(QuoteRecordCacheService.class)
            cacheService.cacheAutoModelOptions(extraMeta, autoModelOptions)

            autoModelOptions?.each {
                extraMeta << [optionsSource: it.key]
                formatAutoModelOptions(extraMeta, it.value, company, quoteSource)
            }
        }
        autoModelOptions
    }

    private static Object formatAutoModelOptions(extraMeta, autoModelOptions, insuranceCompany, quoteSource) {
        autoModelOptions?.each {
            it.value = formatValueOfOption(it, extraMeta, insuranceCompany, quoteSource)
        }
    }

    private static Object formatValueOfOption(option, extraMeta, insuranceCompany, quoteSource) {
        option.meta << extraMeta
        if (option.value.contains(ESCAPE_CHAR)) {
            return option.value
        }
        return CacheUtil.doJacksonSerialize([
            value      : option.value,
            text       : option.text,
            meta       : option.meta,
            companyId  : insuranceCompany.id,
            quoteSource: quoteSource
        ]).replaceAll('"', ESCAPE_CHAR)
    }

    void correctSupplementInfo(quoteRecord, additionalParameters) {
        correctAutoModel(quoteRecord, additionalParameters)
        correctCaptchaImage(quoteRecord, additionalParameters)
    }

    static void correctAutoModel(quoteRecord, additionalParameters) {
        def supplementInfo = additionalParameters.supplementInfo
        supplementInfo?.autoModel?.with {
            def optionInfo = CacheUtil.doJacksonDeserialize(supplementInfo.autoModel.replaceAll(ESCAPE_CHAR, '"'), Map.class)

            supplementInfo.selectedAutoModel = optionInfo
            additionalParameters.optionsSource = optionInfo.meta.optionsSource
            log.debug("quote request, format selectedAutoModel:{}, optionsSource:{}", optionInfo, optionInfo.meta.optionsSource)

            if ((optionInfo.companyId == quoteRecord.insuranceCompany?.id)) {
                supplementInfo.autoModel = optionInfo.value
                log.debug("quote request, format supplementInfo.autoModel, autoModel value:{}", optionInfo.value)
            } else {
                log.debug("remove auto model , autoModel : {} , insuranceCompany :{} ,licensePlateNo : {} ", supplementInfo.autoModel, quoteRecord.insuranceCompany?.id, quoteRecord.auto.licensePlateNo)
                supplementInfo.remove("autoModel")
            }

        }
    }

    static void handleAutoModelOptionsQuoteSuccess(QuoteRecord quoteRecord, Map parameters, Boolean quoteFromCache, Boolean turnOffAutoModelMatch) {
        if (!turnOffAutoModelMatch || quoteFromCache) {
            log.debug("handle auto model options after quote success, do nothing return")
            return
        }
        def optionInfo = parameters.supplementInfo.selectedAutoModel
        if (!parameters.autoModel && optionInfo?.meta?.extraProcessForParser) {
            fillAutoModel(optionInfo, parameters)
        }

        Boolean modelOptionsNull = !(parameters.autoModel?.options?.any { it.value })
        if (modelOptionsNull) {
            log.debug("model options is null,remove parameters.autoModel")
            parameters.remove('autoModel')
        }
        parameters.supplementInfo.autoModel = (optionInfo?.value) ? optionInfo.value : parameters.autoModel?.selected?.value

        if (parameters.autoModel) {
            log.debug("formatSelectedAutoModel,formatAutoModelSupplementInfo,set supplementInfo.selectedAutoModel,selected:{}", parameters.autoModel.selected)
            formatAutoModel(parameters, quoteRecord, turnOffAutoModelMatch)
            if (parameters.autoModel.selected instanceof Map) {
                parameters.supplementInfo.selectedAutoModel = CacheUtil.doJacksonDeserialize(parameters.autoModel.selected.value.replaceAll(ESCAPE_CHAR, '"'), Map.class)
            } else {
                log.warn('所选车型不在车型列表中，selected数据格式未变更，为原始String类型，selected={}', parameters.autoModel.selected)
            }
        }

    }

    static void fillAutoModel(optionInfo, Map parameters) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
        QuoteRecordCacheService cacheService = applicationContext.getBean(QuoteRecordCacheService.class)
        parameters.autoModel = [
            options : cacheService.getAutoModelOptions(optionInfo.meta.autoModelHashKey),
            selected: optionInfo.value
        ]
        log.debug("fill up parameters.autoModel, autoModelHashKey:{}, selected.value:{}, optionsSource:{}", optionInfo.meta.autoModelHashKey, optionInfo.value, optionInfo.meta.optionsSource)
    }

    static void formatAutoModel(Map parameters, QuoteRecord quoteRecord, boolean turnOffAutoModelMatch) {
        formatAutoModelSupplementInfo(quoteRecord.insuranceCompany, parameters.autoModel?.options, turnOffAutoModelMatch, quoteRecord.type)
        formatSelectedAutoModel(parameters, quoteRecord.insuranceCompany, quoteRecord.type)
    }

    static void handleCaptchaImageSupplementInfo(LackOfSupplementInfoException e, quoter) {
        QuoteRecordCacheService cacheService = quoter.quoteRecordCacheService
        String sessionId = quoter.quoteContext.clientIdentifier
        def quoteSource = quoter.quoteRecord.type ?: quoter.additionalParameters.quoteSourceMap?.get(quoter.quoteRecord.insuranceCompany)

        def captchaImageSupplementInfo = e.errorObject?.fieldPath?.
            collect { it.substring(it.lastIndexOf('.') + 1) }?.
            findAll { CAPTCHA_IMAGE_FIELD.contains(it) }?.
            collectEntries { [(it): true] }
        if (AGENTPARSER_9 == quoteSource && captchaImageSupplementInfo) {
            def key = captchaImageFlagKey(sessionId, quoter.quoteRecord.auto)
            cacheService.cacheCaptchaImageFlag(key, captchaImageSupplementInfo)
        }
    }

    void correctCaptchaImage(quoteRecord, additionalParameters) {
        def supplementInfo = additionalParameters.supplementInfo
        if (AGENTPARSER_9 != quoteRecord.type) {
            return
        }
        def key = captchaImageFlagKey(additionalParameters.client_identifier, quoteRecord.auto)
        def map = quoteRecordCacheService.getCaptchaImageFlag(key)
        CAPTCHA_IMAGE_FIELD.each {
            if (map?.get(it)) {
                quoteRecordCacheService.cacheCaptchaImageFlag(key, [:])
            } else if (supplementInfo?.get(it)) {
                log.debug("supplementInfo remove : {}, key : {}", it, key)
                supplementInfo.remove(it)
            }
        }
    }

    static List<QuoteSupplementInfo> assembleQuoteSupplementInfo(Auto auto, QuoteRecord quoteRecord, Map additionalParam) {
        additionalParam = additionalParam ?: [:]
        def supplementInfo = additionalParam.supplementInfo ?: [:]
        supplementInfo.metaInfo = additionalParam.metaInfo
        Constants.PERSIST_FIELD.findAll {
            supplementInfo.containsKey(it) && supplementInfo.get(it)
        }.collect { code ->
            new QuoteSupplementInfo().with {
                it.setFieldPath(code)
                if (PERSIST_FIELD_JSON.contains(code)) {
                    it.setValue(CacheUtil.doJacksonSerialize(supplementInfo.get(code)))
                } else if ('date' == LackOfSupplementInfoHandler.findByCode(code)?.fieldType) {
                    try {
                        def value = supplementInfo.get(code)
                        it.setValue(new SimpleDateFormat("yyyy-MM-dd").format(value instanceof Date ? value : new Date(value)))
                    } catch (IllegalArgumentException e) {
                        log.debug("保存补充信息日期格式转换错误 code: $code value: ${supplementInfo.get(code)}")
                        ExceptionUtils.printRootCauseStackTrace(e)
                    }
                } else {
                    it.setValue(supplementInfo.get(code) as String)
                }

                it.setAuto(auto)
                it.setQuoteRecord(quoteRecord)
                it.setCreateTime(new Date())

                if ('autoModel' == code) {
                    it.valueName = supplementInfo.get('selectedAutoModel')?.text
                }
                it
            }
        }
    }

    static List addNullValue(List formattedParams, visibleFields) {
        def keys = formattedParams.collect { it.key }
        def nullParams = EXCEPTION_TEMPLATE_NEW.findAll {
            visibleFields.contains(it.key) && !NON_SELF_EXPRESS_TYPES.contains(it.value.fieldType) && !keys.contains(it.key)
        }.collect { mappingEntry ->
            SerializationUtils.clone(mappingEntry.value).with {
                it.key = mappingEntry.key
                it.fieldPath = LackOfSupplementInfoHandler.addPrefix(it.fieldPath)
                it.originalValue = null
                it.remove('hints')
                it
            }
        }

        (formattedParams + nullParams).findAll {
            it.key != 'insuredIdNo'
        }.sort { a, b -> FIND_INDEX(visibleFields, a.key) <=> FIND_INDEX(visibleFields, b.key) }
    }

    static QuoteRecord updateQuoteRecord(QuoteRecord quoteRecord, Map quoteRecordParam) {
//        quoteRecord.effectiveDate = quoteRecord.effectiveDate ?: quoteRecordParam?.supplementInfo?.commercialStartDate?.with {
//            it instanceof Date ? it : new Date(it)
//        }
//        quoteRecord.compulsoryEffectiveDate = quoteRecord.compulsoryEffectiveDate ?: quoteRecordParam?.supplementInfo?.compulsoryStartDate?.with {
//            it instanceof Date ? it : new Date(it)
//        }
        quoteRecord
    }

}
