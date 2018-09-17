package com.cheche365.cheche.rest

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.service.IThirdPartyHandlerService2
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.parser.service.ConThirdPartyHandlerService
import com.cheche365.cheche.parser.service.Referenced2ThirdPartyHandlerService
import com.cheche365.cheche.rest.model.SPBroadCaster
import com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler
import com.cheche365.cheche.rest.processor.quote.QuoteProcessor
import com.cheche365.cheche.rest.util.CheckUtil
import com.cheche365.cheche.web.service.http.SessionScopeLogger
import org.apache.commons.lang3.SerializationUtils
import org.springframework.context.ApplicationContext

import static com.cheche365.cheche.core.model.InsuranceCompany.apiQuoteCompanies
import static com.cheche365.cheche.core.model.InsuranceCompany.referenceBaseCompanies
import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.core.model.QuoteSource.Enum.REFERENCED_7
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.core.model.QuoteSource.getQuoteSource
import static com.cheche365.cheche.core.service.SupplementInfoService.handleAutoModelOptionsQuoteSuccess
import static com.cheche365.cheche.core.util.CacheUtil.toJSONPretty
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._GET_QUOTING_SERVICE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._POST_QUOTE_RECORD_FAILED_RULE
import static com.cheche365.cheche.parser.ArtificialPolicyConstants._THIRD_PARTY_HANDLER_RULE_MAPPINGS_1
import static com.cheche365.cheche.rest.QuoterFactory.QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH

class SimplifiedQuoter {

    SPBroadCaster spBroadCaster
    QuoteRecord quoteRecord
    Map<String, Object> additionalParameters
    Map<String, Object> quoteContext
    QuoteRecordCacheService quoteRecordCacheService
    SessionScopeLogger logger
    RedisPublisher redisPublisher


    QuoteRecord doQuote() {

        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
        QuoteProcessor quoteProcessor = applicationContext.getBean(QuoteProcessor.class)
        QuoteConfigService quoteConfigService = applicationContext.getBean(QuoteConfigService.class)

        quoteProcessor.preCheck(quoteContext, this.quoteRecord)

        broadCachedQR(quoteProcessor, quoteConfigService)

        if (additionalParameters.quoteCompanies) {
            try {

                IThirdPartyHandlerService2 quoteService = getQuoteService(additionalParameters)
                Map<InsuranceCompany, Map> resultQuoteMap = quoteService.quotes(this.quoteRecord.clone(),
                    this.additionalParameters.clone().with {
                        it.supplementInfo = SerializationUtils.clone(it.supplementInfo)
                        it
                })

                CheckUtil.checkQuoteable(this.quoteRecord.channel, quoteContext.sessionAttrs)
                broadQuoteResult(resultQuoteMap, quoteProcessor)

            } catch (Exception e) {
                QuoteExceptionHandler.handleQuoteException(this, this.additionalParameters, e)
                spBroadCaster.doOnException(this.quoteRecord, this.additionalParameters, e, true)
            }

        } else {
            logger.debugQuote("无待报价的公司!")
        }

        spBroadCaster.quoteAllFinish()
        quoteProcessor.saveAuto(quoteRecord.auto, quoteRecord, additionalParameters)
        return null
    }


    private void broadCachedQR(QuoteProcessor quoteProcessor, QuoteConfigService quoteConfigService) {
        List<String> companyIds = quoteContext.pref.companyIds
        def quoteCompanies = []
        def quoteSourceMap = [:]

        companyIds.each {
            InsuranceCompany company = toInsuranceCompany(it)
            QuoteSource quoteSource = quoteConfigService.findQuoteSource(quoteRecord.channel, quoteRecord.area, company)
            QuoteRecord queryQuoteRecord = this.quoteRecord.clone().with {
                it.insuranceCompany = company
                it.type = quoteSource
                it
            }
            QuoteRecord cacheQuoteRecord = this.quoteRecordCacheService.getQuoteRecordFromCache(queryQuoteRecord, this.additionalParameters)
            if (cacheQuoteRecord) {
                logger.debugQuote("${company.name} 公司报价缓存命中!")
                Map cacheQuoteRecordParam = quoteRecordCacheService.getQuoteRecordParamByHashKey(cacheQuoteRecord.quoteRecordKey)
                Boolean quotable = CheckUtil.quoteable(this.quoteRecord.channel, quoteContext.sessionAttrs)
                if (quotable) {
                    quoteSuccess(quoteProcessor, cacheQuoteRecord, cacheQuoteRecordParam?.metaInfo, cacheQuoteRecordParam, true)
                }
            } else {
                quoteCompanies << company
                quoteSourceMap.put(company, quoteSource)
            }
        }
        additionalParameters.quoteCompanies = quoteCompanies
        additionalParameters.quoteSourceMap = quoteSourceMap
    }

    IThirdPartyHandlerService2 getQuoteService(additionalParameters) {
        List companies = additionalParameters.quoteCompanies
        Map quoteSourceMap = additionalParameters.quoteSourceMap
        def referredCompanies = mergeCompanies(companies, quoteSourceMap)

        additionalParameters.referredCompanies = referredCompanies
        def referredCompanyServices = referredCompanies.collectEntries { company ->
            [
                (company): quoteContext.services.find { name, service ->
                    service.isSuitable(["quoteSource": getQuoteSource(company, quoteSourceMap), "insuranceCompany": company.getParent()])
                }?.value
            ]
        }
        return new Referenced2ThirdPartyHandlerService(
            new ConThirdPartyHandlerService(referredCompanyServices, quoteContext.parserTaskPGroup),
            _POST_QUOTE_RECORD_FAILED_RULE,
            _THIRD_PARTY_HANDLER_RULE_MAPPINGS_1,
            _GET_QUOTING_SERVICE,
            quoteContext.services
        )
    }

    private mergeCompanies(List companiesFromClient, Map quoteSourceMap) {

        if(!RuntimeUtil.isProductionEnv()) {
            if(quoteContext.sessionAttrs?.get(WebConstants.SESSION_KEY_TURN_OFF_REFER_RULE_ENGINE_QUOTE)){
                logger.debugQuote('非生产环境关闭参考报价')
                return companiesFromClient as Set
            }
        }

        Boolean referenceBase = companiesFromClient.find { REFERENCED_7 == quoteSourceMap.get(it) }
        (
            companiesFromClient.findAll { REFERENCED_7 != quoteSourceMap.get(it) } +
                (referenceBase ? referenceBaseCompanies() : [])
        ).flatten() as Set
    }

    private void broadQuoteResult(Map<InsuranceCompany, Map> resultQuoteMap, QuoteProcessor quoteProcessor) {
        if (!quoteFailed(WEBPARSER_2, resultQuoteMap, additionalParameters.quoteSourceMap)) {
            def map = resultQuoteMap.groupBy { ic, result -> result.code == 0 }.collectEntries {
                groupId, results ->
                    [(groupId): results.collectEntries(Closure.IDENTITY)]
            }
            if (map[true]) {
                doQuoteSuccess(map[true], quoteProcessor)
            }
            if (map[false]) {
                logger.debugQuote("推送非参考保险公司报价失败消息!")
                quoteFailed(API_4, map[false], additionalParameters.quoteSourceMap)
            }
        } else {
            logger.debugQuote("参考基准报价全部失败，推送失败消息!")
        }
        logger.debugQuote("报价结束!")
    }

    private void doQuoteSuccess(Map<InsuranceCompany, Map> resultQuoteMap, QuoteProcessor quoteProcessor) {
        resultQuoteMap.keySet().toList().with {
            list ->
                list.sort(new Comparator<InsuranceCompany>() {

                    @Override
                    int compare(InsuranceCompany o1, InsuranceCompany o2) {
                        return o1.rank - o2.rank
                    }
                })
                list.each {
                    company ->
                        Map resultMap = resultQuoteMap.get(company)
                        if (resultMap.code == 0) {
                            QuoteRecord resultQuoteRecord = resultMap.data?.quoteRecord
                            Map parameters = resultMap.data?.additionalParameters
                            resultQuoteRecord.insuranceCompany = company
                            clearAnnotations(resultQuoteRecord, resultMap?.metaInfo?.referencedInsuranceCompany, company)
                            quoteSuccess(quoteProcessor, resultQuoteRecord, resultMap?.metaInfo, parameters, false)
                            logger.debugQuote("${resultMap?.metaInfo?.debugInfo}")
                        }
                }
        }

    }

    private boolean quoteFailed(QuoteSource quoteSource, Map<InsuranceCompany, Map> resultQuoteMap, quoteSourceMap) {
        boolean flag = false
        def failedResults
        def singleResult = (WEBPARSER_2 == quoteSource)
        if (singleResult) {
            failedResults = resultQuoteMap.find { key, value ->
                value.code != 0 &&
                    ValidationUtil.ableRuleQuote(this.quoteRecord.channel, getQuoteSource(key, quoteSourceMap)) &&
                    quoteSource == getQuoteSource(value?.metaInfo?.referencedInsuranceCompany, quoteSourceMap)
            }?.collect { it }
        } else {
            failedResults = resultQuoteMap.findAll { key, value -> value.code != 0 }
        }
        failedResults?.each {
            Exception e = it.value.error
            logger.debugQuote("${it.key.name} 报价失败,原因:${it.value.code}!")
            this.quoteRecord.insuranceCompany = it.key
            def parameters = it.value.data.additionalParameters
            QuoteExceptionHandler.handleQuoteException(this, parameters, e)
            spBroadCaster.doOnException(this.quoteRecord, parameters, e, true, singleResult)
            flag = true
            quoteRecordCacheService.saveQuoteRecordLog(this.quoteRecord)
        }
        flag
    }

    private void quoteSuccess(QuoteProcessor quoteProcessor, QuoteRecord resultQuoteRecord, Map metaInfo, Map parameters, Boolean quoteFromCache) {

        quoteProcessor.fillQuoteRecord(this.quoteRecord, resultQuoteRecord, parameters)

        parameters.metaInfo = metaInfo
        QuoteRecord queryQuoteRecord = this.quoteRecord.clone().with {
            it.insuranceCompany = resultQuoteRecord.insuranceCompany
            it.type = parameters.quoteSourceMap?.get(it.insuranceCompany)
            it
        }

        Boolean turnOffAutoModelMatch = parameters.get(QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH)
        handleAutoModelOptionsQuoteSuccess(resultQuoteRecord, parameters, quoteFromCache, turnOffAutoModelMatch)

        logger.debugQuote("缓存报价结果QuoteRecord", toJSONPretty(resultQuoteRecord))
        this.quoteRecordCacheService.cacheQuoteRecord(queryQuoteRecord, resultQuoteRecord, this.additionalParameters, parameters)

        quoteRecordCacheService.saveQuoteRecordLog(resultQuoteRecord ? resultQuoteRecord : queryQuoteRecord)
        spBroadCaster.doOnSuccess(resultQuoteRecord, parameters)
        spBroadCaster.quoteFinish(resultQuoteRecord)
    }

    private void clearAnnotations(QuoteRecord quoteRecord, InsuranceCompany referencedInsuranceCompany, InsuranceCompany currentInsuranceCompany) {
        if (apiQuoteCompanies().contains(referencedInsuranceCompany) && referencedInsuranceCompany?.id != currentInsuranceCompany?.id) {
            quoteRecord.annotations = null
        }
    }
}
