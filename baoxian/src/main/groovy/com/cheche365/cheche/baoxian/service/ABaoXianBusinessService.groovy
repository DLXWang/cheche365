package com.cheche365.cheche.baoxian.service

import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.flow.core.service.TSimpleService
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.baoxian.util.BusinessUtils.getCityProperty
import static com.cheche365.cheche.baoxian.util.BusinessUtils._CITY_PROPERTIES_MAPPINGS

/**
 * 泛华抽象服务
 */
@Slf4j
abstract class ABaoXianBusinessService implements TSimpleService {

    private MoApplicationLogRepository logRepo

    protected ABaoXianBusinessService(
        MoApplicationLogRepository logRepo) {
        this.logRepo = logRepo
    }

    protected doBusinessService(env, requestObj, operationId, operationDesc) {

        def businessContext = createBusinessCommonContext(env, requestObj) + createBusinessSpecialContext(env, requestObj)

        service businessContext, operationId, operationDesc
    }

    protected abstract createBusinessSpecialContext(env, requestObj)


    private createBusinessCommonContext(env, requestObj) {
        [
            client              : new groovyx.net.http.RESTClient(getEnvProperty(env, getObjectByCityCode(env.area,_CITY_PROPERTIES_MAPPINGS))).with {
                headers.put('channelId', getEnvProperty(env, getCityProperty(requestObj.area,'channelID')))
                it
            },
            channelID           : getEnvProperty(env, 'baoxian.channel_id'),
            channelSecret       : getEnvProperty(env, 'baoxian.channel_secret'),
            logRepo             : logRepo,
            taskId              : requestObj.taskId,
            auto                : requestObj.additionalParameters.auto,
            additionalParameters: requestObj.additionalParameters,
            insuranceCompany    : requestObj.insuranceCompany
        ]
    }

}
