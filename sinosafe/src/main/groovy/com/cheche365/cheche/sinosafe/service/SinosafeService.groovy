package com.cheche365.cheche.sinosafe.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.exception.BusinessException.Code.DOINSURANCE_FAILED
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.sinosafe.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.sinosafe.flow.Constants._SINOSAFE_GET_VEHICLE_OPTION
import static com.cheche365.cheche.sinosafe.flow.Constants._SINOSAFE_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.sinosafe.flow.Constants._SINOSAFE_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.sinosafe.flow.Constants._STATUS_CODE_SINOSAFE_CONFIRM_INSURE_FAILURE
import static com.cheche365.cheche.sinosafe.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.sinosafe.flow.FlowMappings._FLOW_ADVICE_POLICY_MAPPINGS_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosafe.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosafe.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.CityCodeMappings._CITY_CODE_MAPPINGS
import static com.cheche365.flow.core.util.ServiceUtils.persistState
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT



/**
 * 华安服务实现
 */
@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class SinosafeService extends AThirdPartyAPIHandlerService {

    /**
     * 第三方API服务基类
     *
     */
    SinosafeService(Environment env) {
        this(env, null)
    }

    SinosafeService(
        Environment env, IInsuranceCompanyChecker insuranceCompanyChecker) {
        super(env, insuranceCompanyChecker)
    }

    private static final _STATUS_HANDLER_CONFIRM_INSURE_ADVICE = { context, businessObjects, fsrv, log ->
        def (_flag, _status, payload, errorMsg) = fsrv

        throw new BusinessException(DOINSURANCE_FAILED, errorMsg)
    }

    private static final _VALID_STATUS_HANDLER_MAPPINGS_MAPPING = [
        (_CHECK_STATUS_BASE.curry(_STATUS_CODE_SINOSAFE_CONFIRM_INSURE_FAILURE)): _STATUS_HANDLER_CONFIRM_INSURE_ADVICE
    ]


    @Override
    protected Object doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {

        def area = quoteRecord.area

        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS

        def base_url = MockUrlUtil.findBaseUrl(additionalParameters) ? MockUrlUtil.findBaseUrl(additionalParameters) + '/sinosafe' : env.getProperty('sinosafe.api_base_url')
        log.debug("sinosafe request url:[{}]" + base_url)
        def channel = quoteRecord.channel.apiPartner?.code //获取渠道
        [
            client                           : new RESTClient(base_url).with {
                client.params.setParameter(CONNECTION_TIMEOUT, env.getProperty('sinosafe.conn_timeout') as Integer)
                client.params.setParameter(SO_TIMEOUT, env.getProperty('sinosafe.so_timeout') as Integer)
                it
            },
            user                             : env.getProperty('sinosafe.user'),
            password                         : env.getProperty('sinosafe.password'),
            extenterpcode                    : env.getProperty('sinosafe.extenterpcode'),
            proxyCode                        : getEnvProperty([env: env, area: quoteRecord.area], 'sinosafe.cmpny_agt_cde'),
            slsCode                          : getEnvProperty([env: env, area: quoteRecord.area], 'sinosafe.sls_code'),
            cityCode                         : area.id as String,
            instituteCode                    : cityCodeMapping?.instituteCode,
            provinceCode                     : getProvinceCode(area.id),
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            cityAdvicePolicyMappings         : _FLOW_ADVICE_POLICY_MAPPINGS_FLOW_MAPPINGS,
            getVehicleOption                 : _SINOSAFE_GET_VEHICLE_OPTION,
            insuranceCompany                 : quoteRecord.insuranceCompany,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            loadPersistentState              : _SINOSAFE_LOAD_PERSISTENT_STATE,
            savePersistentState              : _SINOSAFE_SAVE_PERSISTENT_STATE,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            compulsoryAndAutoTaxAllowAlone   : true,
            quotePriceCount                  : 0,
            channel                          : channel,
            carQuotePriceCount               : 0
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        SINOSAFE_205000 == conditions.insuranceCompany && (API_4 == conditions.quoteSource)
    }

    @Override
    def getValidStatusHandlerMappings() {
        super.getValidStatusHandlerMappings() + _VALID_STATUS_HANDLER_MAPPINGS_MAPPING
    }

    @Override
    def handleException(context, businessObjects, ex) {
        if (!context.quoting && context.newQuoteRecordAndInsurances && context.updateBusinessObjects) {
            context.updateBusinessObjects context, businessObjects
        }
        persistState context
        super.handleException context, businessObjects, ex
    }
}
