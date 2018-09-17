package com.cheche365.cheche.gshell.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.flow.core.service.TSimpleService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.gshell.flow.FlowMappings._FLOW_CATEGORY_ACQUISITION_FLOW_MAPPINGS



/**
 * 悟空采集服务
 */
@Service
@Slf4j
class AcquisitionService implements IOCRService, TSimpleService {

    private IThirdPartyDecaptchaService decaptchaService

    AcquisitionService(IThirdPartyDecaptchaService decaptchaService) {
        this.decaptchaService = decaptchaService
    }

    @Override
    Map getInformation(uriText, Map additionalParameters) {
        service createContext(com_cheche365_flow_core_service_TSimpleService__env, uriText, additionalParameters), 'acquisition', '悟空身份证信息采集接口'
    }

    private createContext(env, uriText, additionalParameters) {
        [
            client                        : new RESTClient(env.getProperty('gshell.base_url')),
            env                           : env,
            cityAcquisitionFlowMappings   : _FLOW_CATEGORY_ACQUISITION_FLOW_MAPPINGS,
            imageFile                     : uriText.collect {
                new File(new URI(it))
            },
            additionalParameters          : additionalParameters ?: [:],
            decaptchaService              : decaptchaService,
            decaptchaInputTopic           : 'decaptcha-in-type04'
        ]
    }

}
