package com.cheche365.cheche.baoxian.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IContextWithTTLSupport
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.util.logging.Slf4j
import groovyx.gpars.group.PGroup
import org.springframework.core.env.Environment

import static com.cheche365.cheche.baoxian.flow.Constants._BAOXIAN_ADDITIONAL_QUOTE_RECORD_INFO_EXTRACTOR
import static com.cheche365.cheche.baoxian.flow.Constants._BAOXIAN_GET_VEHICLE_OPTION
import static com.cheche365.cheche.baoxian.flow.Constants._BAOXIAN_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.baoxian.flow.Constants._BAOXIAN_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.baoxian.flow.Constants._STATUS_CODE_INSURE_SUCCESS
import static com.cheche365.cheche.baoxian.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.baoxian.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._CITY_ADVICE_POLICY_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils._CITY_PROPERTIES_MAPPINGS
import static com.cheche365.cheche.baoxian.util.BusinessUtils.getCityProperty
import static com.cheche365.cheche.common.util.AreaUtils.getProvincialCapitalCode
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.exception.BusinessException.Code.DOINSURANCE_FAILED
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLANTFORM_BX_6



/**
 * 泛华保险服务实现
 */
@Slf4j
class BaoXianService extends AThirdPartyAPIHandlerService {

    private static final _STATUS_HANDLER_INSURANCE_SUCCESS = { context, _businessObjects, fsrv, log ->
        def errorMsg = fsrv.last()
        log.info errorMsg
        throw new BusinessException(DOINSURANCE_FAILED, errorMsg)
    }

    private static final _INSURANCE_STATUS_HANDLER_MAPPINGS = [
        (_CHECK_STATUS_BASE.curry(_STATUS_CODE_INSURE_SUCCESS)): _STATUS_HANDLER_INSURANCE_SUCCESS,
    ]

    private IContextWithTTLSupport globalContext
    private messageHandler
    private MoApplicationLogRepository logRepo
    private PGroup parserTaskPGroup


    BaoXianService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IContextWithTTLSupport globalContext,
        messageHandler,
        MoApplicationLogRepository logRepo,
        PGroup parserTaskPGroup) {
        super(env, insuranceCompanyChecker)
        this.globalContext = globalContext
        this.messageHandler = messageHandler
        this.logRepo = logRepo
        this.parserTaskPGroup = parserTaskPGroup
    }


    @Override
    protected doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def area = quoteRecord.area
        def newEnv = [env: env, area: area]

        def baseUrl = MockUrlUtil.findBaseUrl(additionalParameters) ?: getEnvProperty(newEnv, getObjectByCityCode(area, _CITY_PROPERTIES_MAPPINGS))

        log.info '泛华服务URL：{}', baseUrl

        def quoteArea = area.clone()
        quoteArea.id = getCityCode(area.id)

        [
            client                            : new groovyx.net.http.RESTClient(baseUrl).with {
                headers.put('channelId', getEnvProperty(newEnv, getCityProperty(area, 'channelID')))
                it
            },
            channelId                         : getEnvProperty(newEnv, getCityProperty(area, 'channelID')),
            channelSecret                     : getEnvProperty(newEnv, getCityProperty(area, 'channelSecret')),
            privateKey                        : getEnvProperty(newEnv, 'baoxian.v2.private_key'),
            insuranceCompany                  : quoteRecord.insuranceCompany,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings           : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings          : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            getVehicleOption                  : _BAOXIAN_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            cityAdvicePolicyMappings          : _CITY_ADVICE_POLICY_MAPPINGS,
            loadPersistentState               : _BAOXIAN_LOAD_PERSISTENT_STATE,
            savePersistentState               : _BAOXIAN_SAVE_PERSISTENT_STATE,
            additionalQuoteRecordInfoExtractor: _BAOXIAN_ADDITIONAL_QUOTE_RECORD_INFO_EXTRACTOR,
            globalContext                     : globalContext,
            messageHandler                    : messageHandler,
            logRepo                           : logRepo,
            compulsoryAndAutoTaxAllowAlone    : true,
            area                              : quoteArea,
            parserTaskPGroup                  : parserTaskPGroup
        ]
    }

    @Override
    getValidStatusHandlerMappings() {
        super.validStatusHandlerMappings + _INSURANCE_STATUS_HANDLER_MAPPINGS
    }

    @Override
    boolean isServiceAvailable(context) {
        true
    }

    boolean isSuitable(Map conditions) {
        PLANTFORM_BX_6 == conditions.quoteSource
    }

    private static getCityCode(areaId) {
        if (areaId in [110000L, 120000L, 310000L, 500000L]) {
            getProvincialCapitalCode(areaId)
        } else {
            areaId
        }
    }

}
