package com.cheche365.cheche.aibao.service

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.aibao.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.aibao.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_GET_VEHICLE_OPTION
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.aibao.flow.Constants.get_AIBAO_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.aibao.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.aibao.flow.FlowMappings._FLOW_AIBAO_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.parser.Constants.get_INSURANCE_DATE_EXTRACTOR
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static com.cheche365.cheche.aibao.flow.Constants._AIBAO_PREFIELD_STATUS_MAPPINGS
import static com.cheche365.cheche.aibao.flow.Constants._AIBAO_POSTFIELD_STATUS_MAPPINGS

@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class AiBaoService extends AThirdPartyAPIHandlerService {

    MoApplicationLogRepository logRepo
    private IThirdPartyDecaptchaService decaptchaService
    private IConfigService configService

    @Override
    protected Object doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        def prefixes = [quoteRecord.channel.apiPartner?.code, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        def newEnv = [env: env, configService: configService, namespace: 'aibao']
        def base_url = MockUrlUtil.findBaseUrl(additionalParameters) ?: getEnvPropertyNew(newEnv, 'base_url', null, prefixes)
        def client = new RESTClient(base_url)
        //设置本地fiddler代理查看请求参数明细，测试用
//        client.setProxy('localhost',8888,'http')
        def insuranceCompany = quoteRecord.insuranceCompany
        [
            client                           : client,
            channelIds                       : getEnvPropertyNew(newEnv, 'channelIds', null, prefixes),
            systemId                         : getEnvPropertyNew(newEnv, 'systemId', null, prefixes),
            encrykey                         : getEnvPropertyNew(newEnv, 'encrykey', null, prefixes),
            returnUrl                        : getEnvPropertyNew(newEnv, 'returnUrl', null, prefixes),
            notifyUrl                        : getEnvPropertyNew(newEnv, 'notifyUrl', null, prefixes),
            cityCode                         : quoteRecord.area.id,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_AIBAO_INSURING_FLOW_MAPPINGS,
            getVehicleOption                 : _AIBAO_GET_VEHICLE_OPTION,
            insuranceCompany                 : insuranceCompany,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            logRepo                          : logRepo,
            iopAlone                         : true,
            loadPersistentState              : _AIBAO_LOAD_PERSISTENT_STATE,
            savePersistentState              : _AIBAO_SAVE_PERSISTENT_STATE,
            preQuoteFieldStatusMappings      : _AIBAO_PREFIELD_STATUS_MAPPINGS,
            postQuoteFieldStatusMappings     : _AIBAO_POSTFIELD_STATUS_MAPPINGS,
            compulsoryAndAutoTaxAllowAlone   : true,
            defaultEmail                     : getEnvPropertyNew(newEnv, 'defaultEmail', null, prefixes),
            decaptchaService                 : this.decaptchaService,
            decaptchaInputTopicKey           : getEnvPropertyNew(newEnv, 'decaptchaInputTopicKey', null, prefixes),
            insuranceDateExtractor           : _INSURANCE_DATE_EXTRACTOR,
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        PICC_10000 == conditions.insuranceCompany && (API_4 == conditions.quoteSource)
    }

    AiBaoService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, MoApplicationLogRepository logRepo, IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
        super(env, insuranceCompanyChecker)
        this.logRepo = logRepo
        this.decaptchaService = decaptchaService
        this.configService = configService
    }

//    static getCityCode(area) {
//        area.id in [110000L, 500000L, 120000L, 310000L] ? getProvincialCapitalCode(area.id) : area.id
//    }

    @Override
    handleException(context, businessObjects, ex) {
        try {
            super.handleException context, businessObjects, ex
        } finally {
            if (!context.quoting && context.newQuoteRecordAndInsurances && context.updateBusinessObjects) {
                context.updateBusinessObjects context, businessObjects
            }
            persistState context, ex
        }
    }

}
