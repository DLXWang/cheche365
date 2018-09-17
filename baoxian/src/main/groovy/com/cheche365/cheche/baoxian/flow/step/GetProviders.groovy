package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 获取投保地区下开通的投保供应商列表，验证此次的投保供应商是否包含其中
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class GetProviders extends ABaoXianCommonStep {

    private static final _API_PATH_GET_PROVIDERS = '/getProviders'

    @Override
    run(context) {

        def params = [
            insureAreaCode: context.area.id,
        ]

        def result = send context,prefix + _API_PATH_GET_PROVIDERS, params

        log.info("支持投保的保险公司：{}", result.providers)

        if (('0' == result.code || '00' == result.respCode) && result.providers) {
            def companyIds = context.additionalParameters.supportCompanies.id
            def providers = _COMPANY_I2O_MAPPINGS.findAll { icId, prvIdPrefix ->
                icId in companyIds
            }.values().with { prvIdPrefixes ->
                result.providers.findResults { provider ->
                    if (provider.prvId[0..3] in prvIdPrefixes) {
                        [prvId: provider.prvId]
                    }
                }
            }
            def provider = providers.find { provider ->
                provider.prvId.startsWith(_COMPANY_I2O_MAPPINGS[(context.insuranceCompany.id)])
            }

            if (provider) {
                log.info '报价的保险公司信息：{}', provider
                context.providers = providers
                context.provider = provider
                getContinueFSRV provider
            } else {
                log.error '没有查找到相应的保险公司，{}不支持{}地区投保', context.insuranceCompany.name, context.area.name
                getFatalErrorFSRV '没有查找到相应的保险公司'
            }
        } else {
            log.error '错误消息：{}', result.msg
            getFatalErrorFSRV result.msg
        }
    }




}
