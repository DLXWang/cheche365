package com.cheche365.cheche.piccuk.service

import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import com.cheche365.cheche.parser.service.THttpClientGenerator
import groovy.util.logging.Slf4j
import groovyx.net.http.EncoderRegistry
import org.apache.http.HttpHost
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICCUK_10500
import static com.cheche365.cheche.parser.Constants._CITY_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.service.THttpClientGenerator.getHttpClient
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR
import static com.cheche365.cheche.piccuk.flow.Constants._PICCUK_GET_VEHICLE_OPTION_TYPE
import static com.cheche365.cheche.piccuk.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.piccuk.flow.Constants._VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.FlowMappings._FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.HandlerMappings._CITY_RH_MAPPINGS
import static com.cheche365.cheche.piccuk.flow.HandlerMappings._CITY_RPG_MAPPINGS
import static org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY



/**
 * 获取续保车辆信息
 */
@Service
@Slf4j
class PiccUKInsuranceInfoService extends AThirdPartyInsuranceInfoService implements THttpClientGenerator {

    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {
        def minContext = [env: env, area: area]

        [
            client                            : getHttpClient(
                (getEnvProperty(minContext, 'piccuk.casserver_host')), [suptPro : ['TLSv1'] as String[]]
            ).with {
                it.encoderRegistry = new EncoderRegistry(charset: 'GBK')
                it.client.params.setParameter(
                    DEFAULT_PROXY,
                    new HttpHost(
                        getEnvProperty(minContext, 'piccuk.http_proxy_host'),
                        getEnvProperty(minContext, 'piccuk.http_proxy_port') as int)
                )
                it
            },
            area                              : area,
            insuranceCompany                  : PICCUK_10500,
            cityRpgMappings                   : _CITY_RPG_MAPPINGS,
            cityRhMappings                    : _CITY_RH_MAPPINGS,
            supplementInfoMapping             : _CITY_SUPPLEMENT_INFO_MAPPINGS,
            getVehicleOption                  : _PICCUK_GET_VEHICLE_OPTION_TYPE,
            vehicleModelSupplementInfoMapping : _VEHICLE_MODEL_SUPPLEMENT_INFO_MAPPINGS,
            functionalDone                    : true,
            cityInsuranceBasicInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS,
            cityInsuranceInfoFlowMappings     : _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            cityPaymentInfoFlowMappings       : _FLOW_CATEGORY_GET_PAYMENT_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor       : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT3),
            vehicleInfoExtractor              : _VEHICLE_INFO_EXTRACTOR,
            extendedAttributes                : auto.autoType?.supplementInfo,
            supplementInfoSupportList         : [:],
        ]
    }

}
