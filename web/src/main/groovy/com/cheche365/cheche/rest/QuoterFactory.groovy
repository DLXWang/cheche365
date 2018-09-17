package com.cheche365.cheche.rest

import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteFlowType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.service.SupplementInfoService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.rest.model.SPBroadCaster
import com.cheche365.cheche.web.ContextResource
import com.cheche365.cheche.web.service.http.SessionScopeLogger
import com.cheche365.cheche.web.service.security.throttle.QuoteLimitChecker
import com.cheche365.cheche.web.version.Version
import groovy.util.logging.Slf4j
import groovyx.gpars.group.PGroup
import org.apache.commons.lang3.SerializationUtils
import org.atmosphere.cpr.MetaBroadcaster
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.core.exception.Constants.getFIELD_PATH_MAPPING
import static com.cheche365.cheche.core.exception.Constants.getFIND_INDEX
import static com.cheche365.cheche.core.exception.Constants.getGET_VISIBLE_FIELD
import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany
import static com.cheche365.cheche.core.model.QuoteFlowType.Enum.GENERAL
import static com.cheche365.cheche.core.serializer.SerializerUtil.toMapExceptClass
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_SUPPORT_LIST_TEMPLATE

/**
 * Created by zhengwei on 4/29/15.
 */
@Component
@Slf4j
class QuoterFactory extends ContextResource {

    public static final String QUOTE_ATTRIBUTE_CLIENT_IDENTIFIER = "client_identifier"
    //客户端唯一标志，web情况是session id, IOS情况是UUID
    public static final String QUOTE_ATTRIBUTE_FLOW_TYPE = "flowType"
    public static final String QUOTE_ATTRIBUTE_DISCOUNT = "discount"
    public static final String QUOTE_ATTRIBUTE_URI = "uri" //用来判断API版本等因素
    public static final String SUPPLEMENT_INFO = "supplementInfo"
    public static final String QUOTE_ATTRIBUTE_QUOTE_FLAG = "quoteFlag"
    public static final String QUOTE_ATTRIBUTE_REFER_TO_OTHER_AUTO_MODEL = "referToOtherAutoModel"
    public static final String QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH = "turnOffAutoModelMatch"
    public static final String SUPPORT_COMPANIES = "supportCompanies"
    public static final String QUOTE_LIMIT_CHECKER = "quote_limit_checker"

    @Autowired
    private InsuranceServiceFinder serviceFinder
    @Autowired
    private AutoService autoService
    @Autowired
    private QuoteRecordCacheService cacheService
    @Autowired
    private SupplementInfoService supplementInfoService
    @Autowired
    private SessionScopeLogger logger
    @Autowired
    private QuoteConfigService quoteConfigService
    @Autowired
    QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    Map<String, IThirdPartyHandlerService> services

    @Autowired
    RedisPublisher redisPublisher

    @Autowired
    @Qualifier('parserTaskPGroup')
    PGroup parserTaskPGroup

    @Autowired
    QuoteLimitChecker quoteLimitChecker

    Quoter getQuoter(InsuranceCompany insuranceCompany, Map quoteContext) {

        //车辆、套餐、补充信息在调用报价服务时可能会修正，必须克隆
        Auto auto = quoteContext.auto.clone()
        Area area = quoteContext.insuranceArea
        InsurancePackage insurancePackage = quoteContext.insurancePackage?.clone()
        Map supplementInfo = SerializationUtils.clone(quoteContext.supplementInfo)
        QuoteRecord quoteRecord = this.assemblyQuoteRecord(auto, insuranceCompany, insurancePackage, quoteContext)
        List<InsuranceCompany> supportCompanies = quoteFlowConfigRepository.findByAreaAndChannel(area, quoteRecord.channel).insuranceCompany
        Boolean turnOffAutoModelMatch = (Version.getVersion(quoteContext.uri) >= new Version("1.9") && quoteRecord.channel.isStandardAgent())
        Boolean referToOtherModel = !(turnOffAutoModelMatch && quoteRecord.apiQuote())

        Map<String, Object> additionalParameters = [
            (SUPPLEMENT_INFO)                          : supplementInfo,
            (QUOTE_ATTRIBUTE_URI)                      : quoteContext.uri,
            (QUOTE_ATTRIBUTE_QUOTE_FLAG)               : quoteContext.quoteFlag,
            (QUOTE_ATTRIBUTE_FLOW_TYPE)                : quoteContext.pref.flowType,
            (QUOTE_ATTRIBUTE_DISCOUNT)                 : quoteContext.pref.discount,
            (QUOTE_ATTRIBUTE_CLIENT_IDENTIFIER)        : quoteContext.clientIdentifier,
            (SUPPLEMENT_INFO_SUPPORT_LIST)             : assembleSupplementInfo(),
            (SUPPORT_COMPANIES)                        : supportCompanies,
            (QUOTE_LIMIT_CHECKER)                      : quoteLimitChecker,
            (QUOTE_ATTRIBUTE_REFER_TO_OTHER_AUTO_MODEL): referToOtherModel,
            (QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH): turnOffAutoModelMatch
        ]

        !MockUrlUtil.additionalParameters() ?: additionalParameters << MockUrlUtil.additionalParameters()
        supplementInfoService.correctSupplementInfo(quoteRecord, additionalParameters)
        setQuoteFlowState(additionalParameters, quoteRecord)

        new Quoter().with {
            it.quoteRecord = quoteRecord
            it.quoteContext = quoteContext
            it.additionalParameters = additionalParameters.clone()
            it.quoteRecordCacheService = this.cacheService
            it.quoteService = serviceFinder.find(insuranceCompany, area, quoteRecord.type)
            it.redisPublisher = this.redisPublisher
            it
        }
    }

    SimplifiedQuoter getSimplifiedQuoter(MetaBroadcaster broadcaster, Map quoteContext) {

        List<String> companyIds = quoteContext.pref.companyIds

        //车辆、套餐、补充信息在调用报价服务时可能会修正，必须克隆
        Auto auto = quoteContext.auto.clone()
        Area area = quoteContext.insuranceArea
        InsurancePackage insurancePackage = quoteContext.insurancePackage?.clone()
        Map supplementInfo = SerializationUtils.clone(quoteContext.supplementInfo)
        QuoteRecord quoteRecord = this.assemblyQuoteRecord(auto, null, insurancePackage, quoteContext)

        if (companyIds.size() == 1) {
            InsuranceCompany insuranceCompany = toInsuranceCompany(quoteContext.pref.companyIds[0] as Long)
            quoteRecord.insuranceCompany = insuranceCompany
            quoteRecord.type = quoteConfigService.findQuoteSource(quoteRecord.channel, area, insuranceCompany)
        }
        List<InsuranceCompany> supportCompanies = quoteFlowConfigRepository.findByAreaAndChannel(area, quoteRecord.channel).insuranceCompany
        Boolean turnOffAutoModelMatch = (Version.getVersion(quoteContext.uri) >= new Version("1.9") && quoteRecord.channel.isStandardAgent())
        Boolean referToOtherModel = !(turnOffAutoModelMatch && quoteRecord.apiQuote())

        Map<String, Object> additionalParameters = [
            (SUPPLEMENT_INFO)                          : supplementInfo,
            (QUOTE_ATTRIBUTE_URI)                      : quoteContext.uri,
            (QUOTE_ATTRIBUTE_QUOTE_FLAG)               : quoteContext.quoteFlag,
            (QUOTE_ATTRIBUTE_FLOW_TYPE)                : quoteContext.pref.flowType,
            (QUOTE_ATTRIBUTE_DISCOUNT)                 : quoteContext.pref.discount,
            (QUOTE_ATTRIBUTE_CLIENT_IDENTIFIER)        : quoteContext.clientIdentifier,
            (SUPPLEMENT_INFO_SUPPORT_LIST)             : assembleSupplementInfo(),
            (SUPPORT_COMPANIES)                        : supportCompanies,
            (QUOTE_LIMIT_CHECKER)                      : quoteLimitChecker,
            (QUOTE_ATTRIBUTE_REFER_TO_OTHER_AUTO_MODEL): referToOtherModel,
            (QUOTE_ATTRIBUTE_TURN_OFF_AUTO_MODEL_MATCH): turnOffAutoModelMatch
        ]

        !MockUrlUtil.additionalParameters() ?: additionalParameters << MockUrlUtil.additionalParameters()
        supplementInfoService.correctSupplementInfo(quoteRecord, additionalParameters)
        setQuoteFlowState(additionalParameters, quoteRecord)

        SPBroadCaster spBroadCaster = new SPBroadCaster(
            broadcaster,
            quoteContext.clientIdentifier,
            additionalParameters.get(QUOTE_ATTRIBUTE_QUOTE_FLAG),
            quoteRecord.channel)

        quoteContext.parserTaskPGroup = parserTaskPGroup
        quoteContext.services = services

        new SimplifiedQuoter().with {
            it.spBroadCaster = spBroadCaster
            it.quoteRecord = quoteRecord
            it.quoteContext = quoteContext
            it.additionalParameters = additionalParameters.clone()
            it.quoteRecordCacheService = this.cacheService
            it.logger = this.logger
            it.redisPublisher = this.redisPublisher
            it
        }
    }

    def setQuoteFlowState(additionalParameters, quoteRecord) {
        String sessionId = additionalParameters.client_identifier
        String quoteReqKey = formatQuoteReqKey(quoteRecord, additionalParameters.supplementInfo)
        Boolean enoughToQuote = cacheService.getEnoughToQuoteFlag(sessionId)

        if (enoughToQuote) {
            cacheService.setEnoughToQuoteFlag(sessionId, false)
            cacheService.addToQuoteReqSet(sessionId, quoteReqKey)
            additionalParameters.flowState = [hasThrowLackOfSupplementInfo: true]
            log.debug("行驶证六要素不完整或已推补充信息,设置additionalParameters.flowState:true,quoteReqKey: {} 加入完整报价请求集合", quoteReqKey)
            logger.debugQuote("行驶证六要素不完整或已推补充信息,设置additionalParameters.flowState:true,quoteReqKey:${quoteReqKey}加入完整报价请求集合")
        } else {
            Boolean isMember = cacheService.isMemberQuoteReqSet(sessionId, quoteReqKey)
            if (isMember) {
                additionalParameters.flowState = [hasThrowLackOfSupplementInfo: true]
            }
            log.debug("报价请求quoteReqKey: {} {} 完整报价请求集合", quoteReqKey, isMember ? "命中" : "未命中")
            logger.debugQuote("报价请求quoteReqKey:${quoteReqKey}${isMember ? "命中" : "未命中"}完整报价请求集合")
        }
    }

    static String formatQuoteReqKey(quoteRecord, supplementInfo) {
        Map reqParam = mergeMaps(true,
            toMapExceptClass(quoteRecord.auto),
            toMapExceptClass(supplementInfo),
            toMapExceptClass(quoteRecord.auto?.autoType)
        ).findAll { it.value }

        FIELD_PATH_MAPPING.keySet()
            .sort { a, b -> FIND_INDEX(GET_VISIBLE_FIELD(quoteRecord.channel), a) <=> FIND_INDEX(GET_VISIBLE_FIELD(quoteRecord.channel), b) }
            .collect {
            it << ':' << reqParam.get(it)
        }.join(',') << quoteRecord.insurancePackage?.uniqueString
    }

    List<AsyncQuoter> getAsyncQuoterList(MetaBroadcaster broadcaster, Map quoteContext) {
        quoteContext.pref.companyIds.collect {
            this.getQuoter(InsuranceCompany.Enum.toInsuranceCompany(it), quoteContext)
        }.collect {
            SPBroadCaster spBroadCaster = new SPBroadCaster(
                broadcaster,
                it.quoteContext.clientIdentifier,
                it.additionalParameters.get(QUOTE_ATTRIBUTE_QUOTE_FLAG),
                it.quoteRecord.channel)

            new AsyncQuoter(it, spBroadCaster).with {
                it.metaClass.mdcContext = MDC.copyOfContextMap
                it
            }
        }
    }

    static Map<String, Boolean> assembleSupplementInfo() {
        return new LinkedHashMap<>(_SUPPLEMENT_INFO_SUPPORT_LIST_TEMPLATE)
    }

    QuoteRecord assemblyQuoteRecord(Auto auto, InsuranceCompany insuranceCompany, InsurancePackage insurancePackage, Map<String, Object> quoteContext) {
        Long quoteFlowId = (quoteContext.flowType ? quoteContext.flowType : GENERAL.id) as Long
        QuoteRecord quoteRecord = new QuoteRecord(
            auto: auto,
            area: quoteContext.insuranceArea,
            applicant: quoteContext.user,
            quoteFlowType: QuoteFlowType.toQuoteFlowType(quoteFlowId),
            channel: quoteContext.channel,
            insuranceCompany: insuranceCompany,
            insurancePackage: insurancePackage
        )
        if (insuranceCompany) {
            quoteRecord.type = quoteConfigService.findQuoteSource(quoteRecord.channel, quoteRecord.area, insuranceCompany)
        }

        if (session && session.getAttribute(SESSION_MOBILE)) {
            quoteRecord.setMobile(session.getAttribute(SESSION_MOBILE).toString())
        } else if (quoteContext.user && quoteContext.user.mobile) {
            quoteRecord.setMobile(quoteContext.user.mobile)
        }

        return quoteRecord
    }
}
