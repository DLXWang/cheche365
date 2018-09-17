package com.cheche365.cheche.taikang.service

import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.core.env.Environment
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.TK_80000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl
import static com.cheche365.cheche.taikang.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_POST_FIELD_STATUS_MAPPING
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_PRE_FIELD_STATUS_MAPPING
import static com.cheche365.cheche.taikang.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.taikang.flow.Constants._TaiKang_GET_VEHICLE_OPTION
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.taikang.flow.Constants._TAIKANG_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.taikang.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.taikang.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static com.cheche365.cheche.taikang.flow.HandlerMappings._CITY_RPG_MAPPINGS


@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class TaiKangService extends AThirdPartyAPIHandlerService {

    private MoApplicationLogRepository logRepo
    private IThirdPartyDecaptchaService decaptchaService
    private IConfigService configService

    @Override
    protected Object doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        def prefixes = [quoteRecord.channel.apiPartner?.code, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        def newEnv = [env: env, configService: configService, namespace: 'taikang']
        def baseUrl = findBaseUrl(additionalParameters) ?: getEnvPropertyNew(newEnv, 'base_url', null, prefixes)
        log.info '泰康接出地址url为: {}', baseUrl
        def insuranceCompany = quoteRecord.insuranceCompany
        [
            client                           : new RESTClient(baseUrl),
            channelId                        : getEnvPropertyNew(newEnv, 'channelId', null, prefixes),
            version                          : getEnvPropertyNew(newEnv, 'version', null, prefixes),
            signType                         : getEnvPropertyNew(newEnv, 'sign_type', null, prefixes),
            channelKey                       : getEnvPropertyNew(newEnv, 'channelKey', null, prefixes),
            iopAlone                         : true,
            cityCode                         : quoteRecord.area.id,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            getVehicleOption                 : _TaiKang_GET_VEHICLE_OPTION,
            insuranceCompany                 : insuranceCompany,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            logRepo                          : logRepo,
            loadPersistentState              : _TAIKANG_LOAD_PERSISTENT_STATE,
            savePersistentState              : _TAIKANG_SAVE_PERSISTENT_STATE,
            preQuoteFieldStatusMappings      : _TAIKANG_PRE_FIELD_STATUS_MAPPING,
            postQuoteFieldStatusMappings     : _TAIKANG_POST_FIELD_STATUS_MAPPING,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            compulsoryAndAutoTaxAllowAlone   : true,
            defaultEmail                     : getEnvPropertyNew(newEnv, 'defaultEmail', null, prefixes),
            decaptchaService                 : decaptchaService,
            decaptchaInputTopicKey           : getEnvPropertyNew(newEnv, 'decaptchaInputTopicKey', null, prefixes),
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        TK_80000 == conditions.insuranceCompany && (API_4 == conditions.quoteSource)
    }

    TaiKangService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, MoApplicationLogRepository logRepo, IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
        super(env, insuranceCompanyChecker)
        this.logRepo = logRepo
        this.decaptchaService = decaptchaService
        this.configService = configService
    }

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
