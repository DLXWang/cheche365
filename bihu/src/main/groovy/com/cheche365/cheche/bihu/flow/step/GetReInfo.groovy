package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.bihu.flow.CityCodeMappings.getCityCode
import static com.cheche365.cheche.bihu.util.BusinessUtils.generateRenewalPackage
import static com.cheche365.cheche.bihu.util.BusinessUtils.getFindingVehicleInfoKey
import static com.cheche365.cheche.bihu.util.BusinessUtils.markFinishedVehicleInfo
import static com.cheche365.cheche.bihu.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.bihu.util.BusinessUtils.wasVehicleInfoFinished
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * 查询车型信息
 */
@Component
@Slf4j
class GetReInfo implements IStep {

    private static final _API_PATH_CHECK_RENEWAL = '/api/CarInsurance/getreinfo'

    @Override
    run(context) {
        def cityCode = getCityCode(context.area.id)
        if (!cityCode) {
            return getKnownReasonErrorFSRV("壁虎暂未开通该地区， ${context.area.name}")
        }

        def result = wasVehicleInfoFinished context

        if (!result) {
            def key = getFindingVehicleInfoKey context
            def licensePlateNo = context.auto.licensePlateNo

            def globalContext = context.globalContext
            def findingVehicleInfo = globalContext.exists key
            if (!findingVehicleInfo) {
                def successful = globalContext.bindIfAbsentWithTTL key, true, 30L, SECONDS
                if (!successful) {
                    Thread.sleep 2000L
                    return getLoopContinueFSRV(null, "其他流程刚刚开始查询${licensePlateNo}，稍后重试")
                }
            } else {
                Thread.sleep 2000L
                return getLoopContinueFSRV(null, "其他流程正在查询${licensePlateNo}，稍后重试")
            }

            def queryBody = [
                LicenseNo          : licensePlateNo,
                CityCode           : getCityCode(context.area.id),
                Group              : 1,
                CanShowNo          : 1,
                CanShowExhaustScale: 1,
                ShowXiuLiChangType : 1,
                TimeFormat         : 1,
                ShowAutoMoldCode   : 1,
                RenewalCarType     : 0
            ]

            result = sendAndReceive context, _API_PATH_CHECK_RENEWAL, queryBody, this.class.name

            def expireTime = (result.BusinessStatus > 0 ? 30 : 1) * 24 * 60 * 60
            markFinishedVehicleInfo context, result, expireTime

            dealResult context, result, false

        } else if (result.BusinessStatus > 0) {
            dealResult context, result, true
        } else {
            log.error result?.StatusMessage
            context.newMetaInfo = [isFromCache: true, rawResult: result, expireTTLInSeconds: context.expireTTLInSeconds]
            getFatalErrorFSRV '之前查询车辆信息已经失败，当天不再重试'
        }

    }

    private static dealResult(context, result, isFromCache) {
        context.newMetaInfo = [isFromCache: isFromCache, rawResult: result, expireTTLInSeconds: context.expireTTLInSeconds]

        def businessStatus = result.BusinessStatus
        if (businessStatus in [1, 2, 3]) {
            if (2 == businessStatus) {
                getLoopBreakFSRV false
            }

            def userInfo = result.UserInfo
            context.vehicleInfo = userInfo
            context.vlApplicationLog = result.vlApplicationLog
            if (1 == businessStatus) {
                setCommercialInsurancePeriodTexts context, userInfo.NextBusinessStartDate, _DATETIME_FORMAT2
                setCompulsoryInsurancePeriodTexts context, userInfo.NextForceStartDate, _DATETIME_FORMAT2

                context.renewable = true
                context.renewSource = result.SaveQuote.Source
                context.insurancePackage = generateRenewalPackage result.SaveQuote
            }

            getLoopBreakFSRV true

        } else {
            log.error "壁虎返回异常状态码：{}，详细信息：{}", businessStatus, result.StatusMessage
            getFatalErrorFSRV result.StatusMessage
        }
    }

}
