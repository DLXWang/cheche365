package com.cheche365.cheche.zhongan.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.InsuranceBothNotAllowedException
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.parserapi.service.AThirdPartyAPIHandlerService
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.zhongan.flow.Constants._AUTOTYPE_EXTRACTOR
import static com.cheche365.cheche.zhongan.flow.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.Constants._INSURANCE_DATE_EXTRACTOR
import static com.cheche365.cheche.zhongan.flow.Constants._STATUS_CODE_ZHONGAN_CONFIRM_ORDER_AND_UNDERWRITING_FAILURE
import static com.cheche365.cheche.zhongan.flow.Constants._STATUS_CODE_ZHONGAN_CREATE_POLICY_FAILURE
import static com.cheche365.cheche.zhongan.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.zhongan.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.Constants._ZHONGAN_LOAD_PERSISTENT_STATE
import static com.cheche365.cheche.zhongan.flow.Constants._ZHONGAN_SAVE_PERSISTENT_STATE
import static com.cheche365.cheche.zhongan.flow.FlowMappings._FLOW_ADVICEPOLICY_Mappings_FLOW_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.FlowMappings._FLOW_CATEGORY_INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.FlowMappings._FLOW_CATEGORY_ORDERING_FLOW_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.FlowMappings._FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.zhongan.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.zhongan.util.BusinessUtils._GET_INSURANCECLAUSE
import static com.cheche365.cheche.zhongan.util.BusinessUtils._ZHONGAN_GET_VEHICLE_OPTION
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getCityCode
import static com.cheche365.cheche.zhongan.util.CityCodeMappings2._CITY_CODE_MAPPINGS


/**
 * 众安（保骉）服务实现
 */
@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class ZhonganService extends AThirdPartyAPIHandlerService {

    private IThirdPartyDecaptchaService decaptchaService

    private static final _STATUS_HANDLER_ORDERING_ADVICE = { context, businessObjects, fsrv, log ->
        def (_flag, _status, payload, errorMsg) = fsrv

        throw new InsuranceBothNotAllowedException(errorMsg, payload)
    }

    private static
    final _STATUS_HANDLER_CONFIRM_ORDER_AND_UNDERWRITING_ADVICE = { context, businessObjects, fsrv, log ->
        def (_flag, _status, payload, errorMsg) = fsrv

        throw new BusinessException(BusinessException.Code.DOINSURANCE_FAILED, errorMsg)
    }

    private static final _ORDERING_STATUS_HANDLER_MAPPING = [
        (_CHECK_STATUS_BASE.curry(_STATUS_CODE_ZHONGAN_CREATE_POLICY_FAILURE))                 : _STATUS_HANDLER_ORDERING_ADVICE,
        (_CHECK_STATUS_BASE.curry(_STATUS_CODE_ZHONGAN_CONFIRM_ORDER_AND_UNDERWRITING_FAILURE)): _STATUS_HANDLER_CONFIRM_ORDER_AND_UNDERWRITING_ADVICE,

    ]

    ZhonganService(Environment env) {
        this(env, null, null)
    }

    ZhonganService(
        Environment env, IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService
    ) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
    }


    @Override
    protected doCreateContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {


        def area = quoteRecord.area
        def base_url = MockUrlUtil.findBaseUrl(additionalParameters)  ? MockUrlUtil.findBaseUrl(additionalParameters) + 'zhongan' : getEnvProperty([env: env, area: quoteRecord.area], 'zhongan.api_base_url')
        def cityCodeMapping = getObjectByCityCode area, _CITY_CODE_MAPPINGS

        [
            client                           : new RESTClient(base_url),
            cityCode                         : getCityCode(area),
            provinceCode                     : getProvinceCode(area.id),
            districtCode                     : cityCodeMapping.districtCode,
            autoTypeExtractor                : _AUTOTYPE_EXTRACTOR,
            vehicleInfoExtractor             : _VEHICLE_INFO_EXTRACTOR,
            insuranceDateExtractor           : _INSURANCE_DATE_EXTRACTOR,
            insuranceCompany                 : InsuranceCompany.Enum.ZHONGAN_50000,
            cityRpgMappings                  : _CITY_RPG_MAPPINGS,
            cityRhMappings                   : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings          : _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings         : _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS,
            cityOrderingFlowMappings         : _FLOW_CATEGORY_ORDERING_FLOW_MAPPINGS,
            cityAdvicePolicyMappings         : _FLOW_ADVICEPOLICY_Mappings_FLOW_MAPPINGS,
            supplementInfoMapping            : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                 : _ZHONGAN_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping: _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            loadPersistentState              : _ZHONGAN_LOAD_PERSISTENT_STATE,
            savePersistentState              : _ZHONGAN_SAVE_PERSISTENT_STATE,
            payTradeNo                       : additionalParameters?.payTradeNo,
            tradeNo                          : additionalParameters?.tradeNo,
            outTradeNo                       : additionalParameters?.outTradeNo,
            getInsuranceClause               : _GET_INSURANCECLAUSE,   //保险条款
            decaptchaService                 : decaptchaService,
            decaptchaInputTopic              : 'decaptcha-in-type02'
        ]

    }

    @Override
    boolean isOrderingFlowEnabled() {
        return true
    }

    @Override
    boolean isSuitable(Map conditions) {
        ZHONGAN_50000 == conditions.insuranceCompany && (API_4 == conditions.quoteSource)
    }

    @Override
    def getValidStatusHandlerMappings() {
        super.getValidStatusHandlerMappings() + _ORDERING_STATUS_HANDLER_MAPPING
    }

}
