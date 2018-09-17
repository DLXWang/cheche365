package com.cheche365.cheche.bihu.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.ISelfIncrementCountingCurrentLimiter
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

import static com.cheche365.cheche.bihu.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.bihu.flow.FlowMappings._FLOW_CATEGORY_INSURE_FLOW_MAPPINGS
import static com.cheche365.cheche.bihu.flow.FlowMappings._FLOW_CATEGORY_QUOTE_FLOW_MAPPINGS
import static com.cheche365.cheche.bihu.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.bihu.util.BusinessUtils.custKey
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BIHU_10
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT

/**
 * 壁虎服务实现
 */
@Service
@Slf4j
class BihuService extends AThirdPartyAPIHandlerService {

    private IContext globalContext
    private ISelfIncrementCountingCurrentLimiter findVehicleInfoCurrentLimiter
    private dbService
    private mongoTemplate

    BihuService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker,
                @Qualifier('bihuGlobalContext') IContext globalContext,
                @Qualifier('bihuAPIThrottleFindVehicleInfo') ISelfIncrementCountingCurrentLimiter findVehicleInfoCurrentLimiter,
                @Qualifier('doubleDBService') @Autowired(required = false) dbService,
                @Qualifier('mongoTemplate') @Autowired(required = false) mongoTemplate) {
        super(env, insuranceCompanyChecker)
        this.globalContext = globalContext
        this.findVehicleInfoCurrentLimiter = findVehicleInfoCurrentLimiter
        this.dbService = dbService
        this.mongoTemplate = mongoTemplate
    }

    @Override
    protected doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        [
            client                       : new RESTClient(MockUrlUtil.findBaseUrl(additionalParameters)  ?: env.getProperty('bihu.base_url')).with {
                client.params.setParameter(CONNECTION_TIMEOUT, env.getProperty('bihu.conn_timeout') as Integer)
                client.params.setParameter(SO_TIMEOUT, env.getProperty('bihu.so_timeout') as Integer)
                it
            },
            agent                        : getEnvProperty([env: env, area: quoteRecord.area], 'bihu.agent'),
            agentPwd                     : getEnvProperty([env: env, area: quoteRecord.area], 'bihu.agent_pwd'),
            custKey                      : custKey,
            insuranceCompany             : quoteRecord.insuranceCompany,
            insuranceCompanyCodes        : additionalParameters.insuranceCompanyCodes ?: [quoteRecord.insuranceCompany.code],
            cityQuotingFlowMappings      : _FLOW_CATEGORY_QUOTE_FLOW_MAPPINGS,
            cityInsuringFlowMappings     : _FLOW_CATEGORY_INSURE_FLOW_MAPPINGS,
            cityRpgMappings              : _CITY_RPG_MAPPINGS,
            mongoTemplate                : mongoTemplate,
            globalContext                : globalContext,
            findVehicleInfoCurrentLimiter: findVehicleInfoCurrentLimiter,
            dbService                    : dbService,
            getVehicleOption             : _VEHICLE_INFO_EXTRACTOR,
            iopAlone                     : true,
        ]
    }

    @Override
    def handleException(context, businessObjects, ex) {
        try {
            super.handleException context, businessObjects, ex
        } finally {
            persistState context, ex
        }
    }

    @Override
    boolean isSuitable(Map conditions) {
        PLATFORM_BIHU_10 == conditions.quoteSource
    }

}
