package com.cheche365.cheche.cpic.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.parser.service.AThirdPartyHandlerService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.cpic.flow.Constants._AUTOTYPE_EXTRACTOR
import static com.cheche365.cheche.cpic.flow.Constants._AUTO_INFO_EXTRACTOR
import static com.cheche365.cheche.cpic.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.cpic.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.cpic.flow.FlowMappings._INSURING_FLOW_MAPPINGS
import static com.cheche365.cheche.cpic.flow.FlowMappings._QUOTING_FLOW_MAPPINGS
import static com.cheche365.cheche.cpic.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.cpic.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static com.cheche365.cheche.cpic.util.BusinessUtils._CPIC_GET_VEHICLE_OPTION
import static com.cheche365.cheche.cpic.util.BusinessUtils.normalizeBusinessObjects
import static com.cheche365.cheche.cpic.util.CityUtils.getCityCode
import static com.cheche365.cheche.parser.Constants._INSURANCE_DATE_EXTRACTOR
import static org.apache.commons.lang3.RandomUtils.nextInt as random
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT



/**
 * 太平洋服务实现
 */
@TupleConstructor(
    includeSuperFields = true,
    includeFields = true
)
@Slf4j
class CpicService extends AThirdPartyHandlerService {

    private IThirdPartyDecaptchaService decaptchaService

    CpicService(Environment env) {
        this(env, null, null)
    }

    CpicService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService) {
        super(env, insuranceCompanyChecker)
        this.decaptchaService = decaptchaService
    }

    @Override
    protected createContext(QuoteRecord quoteRecord, businessSpecificContext, additionalParameters) {
        normalizeBusinessObjects quoteRecord
        def cityCode = getCityCode quoteRecord.area.id
        def provinceCode = getProvinceCode cityCode
        def fakeIp = "121.40.13${random(0, 9)}.${random(10, 230)}"
        [
            client                              : new RESTClient(env.getProperty('cpic.base_url')).with {
                headers.put('X-Forwarded-For', fakeIp)
                headers.put('X-LocalAddress', fakeIp)
                client.params.setParameter(CONNECTION_TIMEOUT, env.getProperty('cpic.conn_timeout') as Integer)
                client.params.setParameter(SO_TIMEOUT, env.getProperty('cpic.so_timeout') as Integer)
                it
            },
            provinceCode                        : provinceCode as String,
            cityCode                            : cityCode as String,
            autoTypeExtractor                   : _AUTOTYPE_EXTRACTOR,
            autoInfoExtractor                   : _AUTO_INFO_EXTRACTOR,
            insuranceCompany                    : CPIC_25000,
            cityRpgMappings                     : _CITY_RPG_MAPPINGS,
            cityRhMappings                      : _CITY_RH_MAPPINGS,
            cityQuotingFlowMappings             : _QUOTING_FLOW_MAPPINGS,
            cityInsuringFlowMappings            : _INSURING_FLOW_MAPPINGS,
            vehicleInfoExtractor                : _VEHICLE_INFO_EXTRACTOR,
            getVehicleOption                    : _CPIC_GET_VEHICLE_OPTION,
            vehicleModelSupplementInfoMapping   : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            insuranceDateExtractor              : _INSURANCE_DATE_EXTRACTOR,
            decaptchaService                    : decaptchaService,
            decaptchaInputTopic               : 'decaptcha-in-type02'
        ]
    }

    @Override
    boolean isSuitable(Map conditions) {
        CPIC_25000 == conditions.insuranceCompany && (WEBPARSER_2 == conditions.quoteSource)
    }

}
