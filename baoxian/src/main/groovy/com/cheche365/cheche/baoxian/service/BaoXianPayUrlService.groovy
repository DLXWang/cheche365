package com.cheche365.cheche.baoxian.service

import com.cheche365.cheche.baoxian.model.PayInfo
import com.cheche365.cheche.core.service.IPayUrlService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.baoxian.flow.FlowMappings._FLOW_CATEGORY_PAYING_FLOW_MAPPINGS
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.baoxian.util.BusinessUtils.getCityProperty


/**
 * 泛华支付服务实现
 */
@Service
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class BaoXianPayUrlService extends ABaoXianBusinessService implements IPayUrlService<Void, PayInfo> {

    @Override
    Void pay(PayInfo payInfo) {
        def env = [env: env, area : payInfo.area]
        doBusinessService env, payInfo, 'paying', '泛华付款服务'
    }

    @Override
    protected createBusinessSpecialContext(env, requestObj) {
        [
            channelId             : getEnvProperty(env, getCityProperty(requestObj.area,'channelID')),
            channelSecret         : getEnvProperty(env, getCityProperty(requestObj.area,'channelSecret')),
            privateKey            : getEnvProperty(env,'baoxian.v2.private_key'),
            insuranceCompany      : requestObj.insuranceCompany,
            area                  : requestObj.area,
            cityPayingFlowMappings: _FLOW_CATEGORY_PAYING_FLOW_MAPPINGS,
            deliveryAddress       : requestObj.additionalParameters.deliveryAddress,
            taskId                : requestObj.taskId
        ]
    }

}
