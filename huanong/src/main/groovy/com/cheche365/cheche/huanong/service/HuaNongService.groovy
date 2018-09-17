package com.cheche365.cheche.huanong.service

import com.cheche365.cheche.core.exception.BusinessException
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
import static com.cheche365.cheche.core.exception.BusinessException.Code.DOINSURANCE_FAILED
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.HN_150000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.core.util.MockUrlUtil.findBaseUrl
import static com.cheche365.cheche.huanong.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.huanong.flow.Constants._HUANONG_GET_VEHICLE_OPTION
import static com.cheche365.cheche.huanong.flow.Constants._HUANONG_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.huanong.flow.Constants._HUANONG_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.huanong.flow.Constants._STATUS_CODE_HUANONG_CONFIRM_INSURE_FAILURE
import static com.cheche365.cheche.huanong.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.huanong.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.huanong.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.huanong.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.flow.core.util.ServiceUtils.persistState



/**
 * 华农服务实现
 */
@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class HuaNongService extends AThirdPartyAPIHandlerService {

    private static final _STATUS_HANDLER_CONFIRM_INSURE_ADVICE = { context, businessObjects, fsrv, log ->
        def (_flag, _status, _payload, errorMsg) = fsrv
        throw new BusinessException(DOINSURANCE_FAILED, errorMsg)
    }

    private static final _VALID_STATUS_HANDLER_MAPPINGS = [
        (_CHECK_STATUS_BASE.curry(_STATUS_CODE_HUANONG_CONFIRM_INSURE_FAILURE)): _STATUS_HANDLER_CONFIRM_INSURE_ADVICE
    ]

    private IConfigService configService
    private MoApplicationLogRepository logRepo
    private IThirdPartyDecaptchaService decaptchaService


    @Override
    protected Object doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        def area = quoteRecord.area
        def prefixes = [quoteRecord.channel.apiPartner?.code, quoteRecord.insuranceCompany.id, quoteRecord.area.id].toArray()
        log.info('华农报价的渠道：{}，保险公司：{}，地区：{}', prefixes[0], prefixes[1], prefixes[2])
        def newEnv = [env: env, configService: configService, namespace: 'huanong']

        [
            client                           : new RESTClient(findBaseUrl(additionalParameters) ?: getEnvPropertyNew(newEnv, 'baseUrl', null, prefixes)),
            user                             : getEnvPropertyNew(newEnv, 'userCode', null, prefixes),
            comCode                          : getEnvPropertyNew(newEnv, 'comCode', null, prefixes),
            agentCode                        : getEnvPropertyNew(newEnv, 'agentCode', null, prefixes),
            agreementNo                      : getEnvPropertyNew(newEnv, 'agreementNo', null, prefixes),
            publicKey                        : getEnvPropertyNew(newEnv, 'publicKey', null, prefixes),
            interfaceCode                    : getEnvPropertyNew(newEnv, 'interfaceCode', null, prefixes),
            transType                        : getEnvPropertyNew(newEnv, 'transType', null, prefixes),
            email                            : getEnvPropertyNew(newEnv, 'email', null, prefixes),
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityCode                         : area.id,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _HUANONG_GET_VEHICLE_OPTION,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            insuranceCompany                 : quoteRecord.insuranceCompany,
            loadPersistentState              : _HUANONG_LOAD_PERSISTENT_STATE,
            savePersistentState              : _HUANONG_SAVE_PERSISTENT_STATE,
            compulsoryAndAutoTaxAllowAlone   : true,
            countKindCode                    : 0,
            decaptchaService                 : this.decaptchaService,
            decaptchaInputTopicKey           : getEnvPropertyNew(newEnv, 'decaptchaInputTopicKey', null, prefixes),
            logRepo                          : logRepo,
            insuranceDateExtractor           : _INSURANCE_DATE_EXTRACTOR,
        ]
    }


    @Override
    boolean isSuitable(Map conditions) {
        HN_150000 == conditions.insuranceCompany && (API_4 == conditions.quoteSource)
    }

    HuaNongService(Environment env, IInsuranceCompanyChecker insuranceCompanyChecker, MoApplicationLogRepository logRepo, IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
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

    @Override
    getValidStatusHandlerMappings() {
        super.validStatusHandlerMappings + _VALID_STATUS_HANDLER_MAPPINGS
    }
}
