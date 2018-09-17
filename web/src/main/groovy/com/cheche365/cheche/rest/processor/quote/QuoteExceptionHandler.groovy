package com.cheche365.cheche.rest.processor.quote

import com.cheche365.cheche.core.exception.BadQuoteParameterException
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.IllegalOperationException
import com.cheche365.cheche.core.exception.KnownReasonException
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.exception.NonFatalBusinessException
import com.cheche365.cheche.core.exception.ShowInsuranceChangeAdviceException
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.util.NotifyMessageUtils
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils

import static com.cheche365.cheche.core.exception.BusinessException.Code.COMMON_KNOWN_REASON_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.AUTO_TYPE_CODE_ERROR
import static com.cheche365.cheche.core.service.SupplementInfoService.formatAutoModelSupplementInfo
import static com.cheche365.cheche.core.service.SupplementInfoService.formatAutoModel
import static com.cheche365.cheche.core.service.SupplementInfoService.fillAutoModel
import static com.cheche365.cheche.core.service.SupplementInfoService.handleCaptchaImageSupplementInfo
import static com.cheche365.cheche.core.util.ValidationUtil.containChinese
import static com.cheche365.cheche.rest.QuoterFactory.QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH
import static com.cheche365.cheche.rest.model.SPResult.Channel.RESULT
import static com.cheche365.cheche.rest.model.SPResult.Status.FAIL

@Slf4j
class QuoteExceptionHandler {

    static void handleQuoteException(def quoter, Map parameters, Exception e) {
        QuoteRecordCacheService cacheService = quoter.quoteRecordCacheService
        String sessionId = quoter.quoteContext.clientIdentifier
        def quoteSource = quoter.quoteRecord.type ?: parameters.quoteSourceMap?.get(quoter.quoteRecord.insuranceCompany)
        def turnOffAutoModelMatch = parameters.get(QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH)

        if (e instanceof LackOfSupplementInfoException) {
            def autoModelSupplementInfo = e.errorObject?.find { it.get("fieldPath")?.endsWith("autoModel") }
            autoModelSupplementInfo?.with {
                it.options = formatAutoModelSupplementInfo(quoter.quoteRecord.insuranceCompany, it.options, turnOffAutoModelMatch, quoteSource)
                parameters.autoModel = null //清空车型列表，A端不再次重复推车型列表
            }

            handleCaptchaImageSupplementInfo(e, quoter)
        }

        Boolean fillAutoModelFromCache = turnOffAutoModelMatch && !parameters.autoModel &&
            parameters.supplementInfo.selectedAutoModel?.meta?.autoModelHashKey &&
            !(e instanceof LackOfSupplementInfoException)
        if(fillAutoModelFromCache){
            fillAutoModel(parameters.supplementInfo.selectedAutoModel, parameters)
        }

        if (turnOffAutoModelMatch && parameters.autoModel) {
            formatAutoModel(parameters, quoter.quoteRecord, turnOffAutoModelMatch)
        }

        if (e instanceof LackOfSupplementInfoException || e instanceof BadQuoteParameterException) {
            cacheService.setEnoughToQuoteFlag(sessionId, true)
        }
        if(e instanceof BusinessException && BusinessException.Code.NOTIFICATION == e.code){
            quoter.redisPublisher.publish(NotifyMessageUtils.getNotifyMessage(e.message,String.valueOf(e.errorObject)))
        }
    }

    static def formatAsyncQuoteException(QuoteRecord quoteRecord, Exception ex) {
        def company = quoteRecord.insuranceCompany
        log.debug("异步报价失败，保险公司: {} 异常信息:\n {}", company?.name, ExceptionUtils.getFullStackTrace(ex))
        def data = [insuranceCompany: company]

        if (ex instanceof ShowInsuranceChangeAdviceException) {
            return [data, RESULT, ex.code.codeValue, ex.errorObject]
        } else if (ex instanceof NonFatalBusinessException) {
            if (ex.errorObject) {
                if (ex instanceof LackOfSupplementInfoException || ex instanceof BadQuoteParameterException) {
                    ex.errorObject = LackOfSupplementInfoHandler.writeResponse(ex.errorObject, quoteRecord.channel)
                }
                data.supplementInfo = (ex.errorObject instanceof List) ? ex.errorObject : null
            }
            return [data, RESULT, ex.code.codeValue, ex.message]
        } else if (ex instanceof IllegalOperationException) {
            return [data, RESULT, ex.code.codeValue, ex.message]
        } else if (ex instanceof KnownReasonException) {
            return [data, RESULT, COMMON_KNOWN_REASON_ERROR.codeValue, ex.message]
        } else if (quoteRecord.channel.isStandardAgent() && checkKnownReasonError(ex)) {
            return knownReasonError(data, ex)
        } else {
            return [data, RESULT, FAIL, null]
        }
    }

    static def formatSimplifiedQuoteException(QuoteRecord quoteRecord, Exception ex) {
        def company = quoteRecord.insuranceCompany
        log.debug("simplified异步报价失败，保险公司: {} 异常信息:\n {}", company?.name, ExceptionUtils.getFullStackTrace(ex))
        def data = [insuranceCompany: company]

        if (ex instanceof NonFatalBusinessException) {
            if (ex.errorObject) {
                if (ex instanceof LackOfSupplementInfoException || ex instanceof BadQuoteParameterException) {
                    ex.errorObject = LackOfSupplementInfoHandler.writeResponse(ex.errorObject, quoteRecord.channel)
                }
                data.supplementInfo = (ex.errorObject instanceof List) ? ex.errorObject : null
            }
            return [data, RESULT, ex.code.codeValue, ex.message]
        } else if (quoteRecord.channel.isStandardAgent() && ex instanceof IllegalOperationException) {
            return [data, RESULT, ex.code.codeValue, ex.message]
        } else if (quoteRecord.channel.isStandardAgent() && ex instanceof KnownReasonException) {
            def codeValue = (AUTO_TYPE_CODE_ERROR.codeValue == ex.code.codeValue) ? AUTO_TYPE_CODE_ERROR.codeValue : COMMON_KNOWN_REASON_ERROR.codeValue
            return [data, RESULT, codeValue, ex.message]
        } else if (quoteRecord.channel.isStandardAgent() && checkKnownReasonError(ex)) {
            return knownReasonError(data, ex)
        } else {
            return [data, RESULT, FAIL, null]
        }
    }

    static checkKnownReasonError(ex) {
        (ex instanceof BusinessException) && (ex.code == INTERNAL_SERVICE_ERROR) && containChinese(ex.errorObject?.errorData ?: ex.message)
    }

    static knownReasonError(data, ex) {
        [data, RESULT, COMMON_KNOWN_REASON_ERROR.codeValue, ex.errorObject?.errorData ?: ex.message]
    }

}
