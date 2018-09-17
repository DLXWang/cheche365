package com.cheche365.cheche.bihu.service

import com.cheche365.cheche.bihu.model.BihuCustKey
import com.cheche365.cheche.common.http.RESTClient
import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.service.DoubleDBService
import com.cheche365.cheche.core.service.IContext
import com.cheche365.cheche.core.service.ISelfIncrementCountingCurrentLimiter
import com.cheche365.cheche.core.util.MockUrlUtil
import com.cheche365.cheche.parser.service.AThirdPartyInsuranceInfoService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import java.time.ZonedDateTime

import static com.cheche365.cheche.bihu.flow.Constants._PERSONNEL_INFO_EXTRACTOR
import static com.cheche365.cheche.bihu.flow.Constants._VEHICLE_INFO_EXTRACTOR
import static com.cheche365.cheche.bihu.flow.FlowMappings._FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS
import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.parser.util.BusinessUtils._INSURANCE_BASIC_INFO_EXTRACTOR
import static java.time.ZoneId.systemDefault
import static org.apache.commons.lang3.RandomUtils.nextInt as random
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT

/**
 * 壁虎行驶证服务
 */
@Service
@Slf4j
class BihuInsuranceInfoService extends AThirdPartyInsuranceInfoService {

    private IContext globalContext
    private ISelfIncrementCountingCurrentLimiter findVehicleInfoCurrentLimiter
    private DoubleDBService dbService


    BihuInsuranceInfoService(
        @Qualifier('bihuGlobalContext') IContext globalContext,
        @Qualifier('bihuAPIThrottleFindVehicleInfo') ISelfIncrementCountingCurrentLimiter findVehicleInfoCurrentLimiter,
        DoubleDBService dbService
    ) {
        this.globalContext = globalContext
        this.findVehicleInfoCurrentLimiter = findVehicleInfoCurrentLimiter
        this.dbService = dbService
    }


    @Override
    final protected doCreateContext(env, area, auto, additionalParameters) {
        def custKey = BihuCustKey.Enum.ALL.with { custKeys ->
            custKeys[random(0, custKeys.size())].custKey
        }
        [
            client                       : new RESTClient(MockUrlUtil.findBaseUrl(additionalParameters)  ?: env.getProperty('bihu.base_url')).with {
                client.params.setParameter(CONNECTION_TIMEOUT, env.getProperty('bihu.conn_timeout') as Integer)
                client.params.setParameter(SO_TIMEOUT, env.getProperty('bihu.so_timeout') as Integer)
                it
            },
            agent                        : additionalParameters.use1 ? env.getProperty('bihu.agent1') : env.getProperty('bihu.agent2'),
            agentPwd                     : additionalParameters.use1 ? env.getProperty('bihu.agent_pwd1') : env.getProperty('bihu.agent_pwd2'),
            custKey                      : custKey,
            globalContext                : globalContext,
            findVehicleInfoCurrentLimiter: findVehicleInfoCurrentLimiter,
            dbService                    : dbService,
            cityInsuranceInfoFlowMappings: _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS,
            insuranceBasicInfoExtractor  : _INSURANCE_BASIC_INFO_EXTRACTOR.curry(_DATETIME_FORMAT2, _PERSONNEL_INFO_EXTRACTOR),
            vehicleInfoExtractor         : _VEHICLE_INFO_EXTRACTOR,
            additionalParameters         : additionalParameters ?: [:]
        ]
    }

    @Override
    boolean isServiceAvailable(context) {
        ZonedDateTime.now().with { now ->
            now.toLocalDate().atStartOfDay(systemDefault()).with { today ->
                now >= today.plusHours(6).plusMinutes(30) && now <= today.plusHours(23).plusMinutes(30)
            }
        } && findVehicleInfoCurrentLimiter.allowed
    }

    @Override
    boolean isOperationAllowed(context) {
        // 判断行驶证服务支持的是否包含壁虎
//        def autoVehicleLicenseServiceItems = context.additionalParameters[CHANNEL_SERVICE_ITEMS]
//
//        autoVehicleLicenseServiceItems?.any {
//            BIHU_53.serviceName == it.serviceName
//        }
        true
    }

    @Override
    handleException(context, businessObjects, ex) {
        super.handleException context, businessObjects, ex
        new InsuranceInfo(metaInfo: [error: ex.toString()] + (context.newMetaInfo ?: [:]))
    }


}
