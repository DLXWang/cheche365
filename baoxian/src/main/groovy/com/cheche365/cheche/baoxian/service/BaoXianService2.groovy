package com.cheche365.cheche.baoxian.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.IContextWithTTLSupport
import com.cheche365.cheche.core.service.IThirdPartyHandlerService2
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
import static com.cheche365.cheche.common.flow.Constants.get_ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.AreaUtils.getProvincialCapitalCode
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.exception.BusinessException.Code.DOINSURANCE_FAILED
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLANTFORM_BX_6
import static com.cheche365.cheche.insurance.core.util.FlowUtils.getQuotingFlow

/**
 * 泛华保险服务实现
 */
@Slf4j
class BaoXianService2 extends AThirdPartyAPIHandlerService implements IThirdPartyHandlerService2 {

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


    BaoXianService2(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        globalContext,
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

        def newEnv = [env: env, area: quoteRecord.area]

        def baseUrl = MockUrlUtil.findBaseUrl(additionalParameters) ?: getEnvProperty(newEnv, getObjectByCityCode(quoteRecord.area,_CITY_PROPERTIES_MAPPINGS))

        log.info '泛华服务URL：{}', baseUrl

        def quoteArea = quoteRecord.area.clone()
        quoteArea.id = getCityCode(quoteRecord.area.id)

        [
            client                           : new groovyx.net.http.RESTClient(baseUrl).with {
                headers.put('channelId', getEnvProperty(newEnv, getCityProperty(quoteRecord.area,'channelID')))
                it
            },
            channelId                        : getEnvProperty(newEnv, getCityProperty(quoteRecord.area,'channelID')),
            channelSecret                    : getEnvProperty(newEnv, getCityProperty(quoteRecord.area,'channelSecret')),
            privateKey                       : getEnvProperty(newEnv,'baoxian.v2.private_key'),
            insuranceCompany                 : additionalParameters.quoteCompanies,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            getVehicleOption                 : _BAOXIAN_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            cityAdvicePolicyMappings         : _CITY_ADVICE_POLICY_MAPPINGS,
            loadPersistentState              : _BAOXIAN_LOAD_PERSISTENT_STATE,
            savePersistentState              : _BAOXIAN_SAVE_PERSISTENT_STATE,
            additionalQuoteRecordInfoExtractor : _BAOXIAN_ADDITIONAL_QUOTE_RECORD_INFO_EXTRACTOR,
            globalContext                    : globalContext,
            messageHandler                   : messageHandler,
            logRepo                          : logRepo,
            parserTaskPGroup                 : parserTaskPGroup,
            compulsoryAndAutoTaxAllowAlone   : true,
            area                             : quoteArea
        ]
    }

    @Override
    Map<InsuranceCompany, Map> quotes(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        if (quotingFlowEnabled) {

            def businessSpecificContext = createQuotingSpecificContext quoteRecord
            def context = mergeMaps(
                createCommonContext(quoteRecord, businessSpecificContext, additionalParameters),
                businessSpecificContext,
                createContext(quoteRecord, businessSpecificContext, additionalParameters))
            def flow = getQuotingFlow context

            def businessObjects = getQuotingBusinessObjects context, quoteRecord

            service businessObjects, flow, context
            // 处理报价结果的地方变更为多个QR和多个additional
        }
    }

    @Override
    def postService(context, businessObjects, fsrv) {
        if (_ROUTE_FLAG_DONE == fsrv[0]) {
            context.insuranceCompany.collectEntries { ic ->
                try {
                    super.postService(context, businessObjects, fsrv)
                } catch (ex) {
                    [(ic): [ code: -1, data: [quoteRecord: null, additionalParameters: context.additionalParameters], error: ex ]]
                }
            }
        } else {
            context.quoteRecordMappings.collectEntries { ic, result ->
                try {
                    super.postService(context, businessObjects, result.fsrv)
                    [(ic): [ code: 0, data: [quoteRecord: result.quoteRecord, additionalParameters: context.additionalParameters], error: null ]]
                } catch (ex) {
                    [(ic): [ code: -1, data: [quoteRecord: result.quoteRecord, additionalParameters: context.additionalParameters], error: ex ]]
                }
            }
        }

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
