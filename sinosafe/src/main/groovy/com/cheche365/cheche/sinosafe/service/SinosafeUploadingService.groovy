package com.cheche365.cheche.sinosafe.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyUploadingService
import com.cheche365.cheche.core.service.TDescriptive
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.flow.core.service.TSimpleService
import groovy.util.logging.Slf4j
import org.springframework.core.env.Environment

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.sinosafe.flow.FlowMappings._FLOW_CATEGORY_UPLOADING_MAPPINGS



/**
 * 华安上传服务
 */
@Slf4j
class SinosafeUploadingService implements IThirdPartyUploadingService, TSimpleService, TDescriptive, ISuitability<Map> {

    SinosafeUploadingService(Environment env) {
        this.env = env
    }

    @Override
    upload(List contents, Map additionalParameters) {
        def context = createContext com_cheche365_flow_core_service_TSimpleService__env, contents, additionalParameters
        service context, "uploading", description
    }

    @Override
    boolean isSuitable(Map conditions) {
        SINOSAFE_205000 == conditions.insuranceCompany && (API_4 == conditions.quoteSource)
    }

    private static createContext(env, contents, additionalParameters) {
        def base_url = MockUrlUtil.findBaseUrl(additionalParameters)  ? MockUrlUtil.findBaseUrl(additionalParameters)  + '/sinosafe' : env.getProperty('sinosafe.api_base_url')

        [
            client                   : new RESTClient(base_url),
            user                     : env.getProperty('sinosafe.user'),
            password                 : env.getProperty('sinosafe.password'),
            extenterpcode            : env.getProperty('sinosafe.extenterpcode'),
            cityUploadingFlowMappings: _FLOW_CATEGORY_UPLOADING_MAPPINGS,
            CAL_APP_NO               : additionalParameters.CAL_APP_NO,
            uploadingImages          : contents
        ]
    }
}
