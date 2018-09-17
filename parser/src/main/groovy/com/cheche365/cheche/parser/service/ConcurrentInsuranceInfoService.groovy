package com.cheche365.cheche.parser.service

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsuranceBasicInfo
import com.cheche365.cheche.core.model.InsuranceInfo
import com.cheche365.cheche.core.service.IInsuranceInfoService
import com.cheche365.flow.core.support.AsyncResultHandler
import com.cheche365.flow.core.service.TSimpleConcurrentService
import groovy.util.logging.Slf4j
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.group.PGroup
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import static com.cheche365.cheche.parser.util.AopUtils.logMergedInsuranceInfoStats
import static com.cheche365.flow.core.util.ConcurrencyUtils.selectAndHandleIndividualResultOneByOne



/**
 * 保险信息的并发包装服务
 * Created by Huabin on 2016/11/30.
 */
@Service
@Slf4j
class ConcurrentInsuranceInfoService implements IInsuranceInfoService, TSimpleConcurrentService {

    private static final _SERVICE_CONFIG_TEMPLATE_GET_INSURANCE_INFO = [
        0L: [
            priorityMappings: [
                bihuInsuranceInfoService        : 0L
            ],
            options: [
                maxAlternativeResultCount   : 1     // 目前只使用壁虎
            ]
        ],
        default: [
            options: [
                timeout                     : 10L,   // 获取到阶段性的行驶证信息超时最多5秒
            ]
        ]
    ]

    private static final _JOB_BLUEPRINT_GET_INSURANCE_INFO = { mdcContext, name, service, area, auto, additionalParameters ->
        MDC.contextMap = mdcContext
        [name, service.getInsuranceInfo(area, auto, additionalParameters)]
    }

    private static final _ENHANCE_JOB_PARAMETERS_GET_INSURANCE_INFO = { _classifiedConfig, jobParams, context ->
        def (area, auto, additionalParameters) = jobParams
        [
            area,
            auto,
            additionalParameters + [stagedResultHandler: new AsyncResultHandler(context.jobId, new DataflowVariable()) ]
        ]
    }

    private static final _GET_ADDITIONAL_OPTIONS_GET_INSURANCE_INFO = { _classifiedConfig, allJobParams ->
        [resultChannels: allJobParams*.last().stagedResultHandler.resultChannel]
    }

    private static final _HANDLE_ASYNC_RESULTS_GET_INSURANCE_INFO_BASE = { pgroup, log, desc, timeout, mdcContext, inGroup, externalAsyncResultHandler, internalAsyncResultHandler, _classifiedConfig, asyncResults ->
        pgroup.task {
            MDC.contextMap = mdcContext
            def insuranceInfos = []
            def mergedInsuranceInfo
            selectAndHandleIndividualResultOneByOne(asyncResults, pgroup, 0, timeout, { classifiedId, insuranceInfo ->

                log.debug '{}，获取到{}的异步保险信息：{}', desc, "${inGroup ? '外部服务': '分组'}$classifiedId", insuranceInfo
                // 维护一个有效保险信息的列表
                insuranceInfos << insuranceInfo

                if (!mergedInsuranceInfo) {
                    mergedInsuranceInfo = new InsuranceInfo()
                }

                mergedInsuranceInfo = mergeInsuranceInfo mergedInsuranceInfo, insuranceInfo

                log.debug '向外部异步结果处理器发送{}阶段性合并后的保险信息：{}', inGroup ? '组内' : '组间', mergedInsuranceInfo
                externalAsyncResultHandler?.sendMessage mergedInsuranceInfo

            }).with { _ ->
                log.debug '{}合并后的保险信息：{}', inGroup ? '组内' : '组间', mergedInsuranceInfo
                internalAsyncResultHandler?.sendMessage mergedInsuranceInfo
                if (inGroup) {
                    logMergedInsuranceInfoStats insuranceInfos*.insuranceCompanyCode, mergedInsuranceInfo
                }
                mergedInsuranceInfo
            }
        }
    }

    private static final _INSURANCE_BASIC_INFO_PROPS = ['effectiveDate', 'compulsoryEffectiveDate', 'insurancePackage']


    @Autowired(required = false)
    private Map<String, IInsuranceInfoService> services

    @Autowired
    @Qualifier('parserTaskPGroup')
    private PGroup parserTaskPGroup


    @Override
    InsuranceBasicInfo getInsuranceBasicInfo(Area area, Auto auto, Map additionalParameters) {
        throw new UnsupportedOperationException('暂不支持并发获取保险基本信息')
    }

    @Override
    Object getInsuranceInfo(Area area, Auto auto, Map additionalParameters) {
        log.debug '将要执行并发获取获取保险信息服务：{}，{}，{}，{}', services, area, auto, additionalParameters
        def mdcContext = MDC.copyOfContextMap
        service(
            services,
            _SERVICE_CONFIG_TEMPLATE_GET_INSURANCE_INFO,
            _JOB_BLUEPRINT_GET_INSURANCE_INFO.curry(mdcContext),
            [area, auto, additionalParameters],
            parserTaskPGroup,
            _ENHANCE_JOB_PARAMETERS_GET_INSURANCE_INFO,
            _GET_ADDITIONAL_OPTIONS_GET_INSURANCE_INFO,
            _HANDLE_ASYNC_RESULTS_GET_INSURANCE_INFO_BASE.curry(
                parserTaskPGroup,
                log,
                '并发服务',
                20L,
                mdcContext,
                true,
                additionalParameters.asyncResultHandler
            ),
            _HANDLE_ASYNC_RESULTS_GET_INSURANCE_INFO_BASE.curry(
                parserTaskPGroup,
                log,
                '组间合并',
                25L,
                mdcContext,
                false,
                additionalParameters.asyncResultHandler,
                null
            )
        ).with { results ->
            def vl = results ? results[0][1] : null
            log.debug '从并发服务中获取到的阶段性行驶证信息为：{}', vl
            vl
        }
    }


    private static mergeInsuranceInfo(merged, info) {
        def infoIsRenewable = info.metaInfo?.renewable &&
            info.insuranceBasicInfo?.compulsoryEffectiveDate > merged.insuranceBasicInfo?.compulsoryEffectiveDate
        // 检查VL
        if (info.vehicleLicense && (!merged.vehicleLicense || infoIsRenewable)) {
            merged.vehicleLicense = info.vehicleLicense
        }

        // 检查IBI
        if (info.insuranceBasicInfo) {
            if (!merged.insuranceBasicInfo) {
                merged.insuranceBasicInfo = info.insuranceBasicInfo
            } else if (infoIsRenewable) {
                mergeInsuranceBasicInfo merged.insuranceBasicInfo, info.insuranceBasicInfo
            }
        }
        merged
    }

    private static mergeInsuranceBasicInfo(merged, info) {
        _INSURANCE_BASIC_INFO_PROPS.inject merged, { m, propName ->
            if (info[propName]) {
                m[propName] = info[propName]
            }
            m
        }
    }

}
