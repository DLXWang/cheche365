package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getBaseKindItems
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCarOwner
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getNewStartDate
import static com.cheche365.cheche.common.util.CollectionUtils.getMapByPath
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.isDefaultStartDate
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.time.LocalDate.now as today



/**
 *  Created by suyq on 2015/9/14.
 *  获取基础报价
 */
@Component
@Slf4j
class BasePremium implements IStep{

    private static final _URL_CUSTOM_PREMIUM = '/online/saleNewCar/carProposaldriverNext.do'

    @Override
    run(Object context) {
        def client = context.client

        Auto auto = context.auto

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_CUSTOM_PREMIUM,
            body              : generateRequestParameters(context, this)
        ]

        log.info '报价信息 {}，{}，{}，{}，{}', getCarOwner(context), auto.licensePlateNo, getAutoVinNo(context), getAutoEngineNo(context), auto.identity
        def result = client.post args, { resp, json ->
            json
        }

        // 后续步骤需要的参数
        context.bsDemandNo = result?.temporary?.quoteMain?.demandNo
        context.bzDemandNo = result?.temporary?.quoteMain?.bzDemandNo

        // 与转保平台校验获取验证码
        if ('4' == result?.temporary?.resultType && result?.temporary?.quoteMain?.busCheckCode) {
            context.imageBase64 = result?.temporary?.quoteMain?.busCheckCode
            log.info '获取保险平台商业险验证码'
            // 获取验证码成功，需要校验验证码
            context.nonRenewalCaptcha = true
            return getContinueFSRV(2)
        }
        // 北京resultCode为1
        if ('1' == result?.temporary?.resultCode || '3' == result?.temporary?.resultCode) {
            if ('0' == result?.temporary?.resultType && !context.dateChanged) {
                def lastBsPolicyEndDate = result?.temporary?.quoteMain?.geQuoteCars?.get(0)?.lastBusPolicyEndDate
                if (checkStartDate(context, lastBsPolicyEndDate)) {
                    def newBsStartDate = getNewStartDate(lastBsPolicyEndDate).first() as String
                    if (newBsStartDate && !isDefaultStartDate(newBsStartDate)) {
                        setCommercialInsurancePeriodTexts context, newBsStartDate
                    }
                    context.dateChanged = true
                    return getContinueFSRV(1)
                }
            }

            def sourceKindItems = result?.temporary?.pageFreeComboKindItem
            if (sourceKindItems) {
                context.pageFreeComboKindItem = ['attachKinds', 'deductKinds', 'mainKinds'].collectEntries { kind ->
                    def attachKinds = ['codeType', 'isComboFlag', 'kindflag', 'orderNo', 'nodeductflag', 'validInd'].collectEntries { item ->
                        getMapByPath(result.temporary, ['temporary'], ['pageFreeComboKindItem', kind, item])
                    }
                    def deductKinds = getMapByPath(result.temporary, ['temporary'], ['pageFreeComboKindItem', kind, 'id', 'kindCode'])
                    def mainKinds = getMapByPath(result.temporary, ['temporary'], ['pageFreeComboKindItem', kind, 'id', 'riskCode'])
                    attachKinds + deductKinds + mainKinds
                }

                context.allKindItems = getBaseKindItems(context, sourceKindItems)
                log.info '基础套餐 baseKindItems：{}', context.allKindItems
                getLoopBreakFSRV true
            } else {
                getLoopBreakWithIgnorableErrorFSRV true, '获取基础保价套餐失败'
            }
        } else if ('5' == result?.temporary?.resultType) {
            log.error '转保验证码校验失败，稍后重试'
            // 校验验证码失败，需要重新获取验证码
            context.nonRenewalCaptcha = false
            return getLoopContinueFSRV(true, '转保验证码校验失败，稍后重试')
        } else {
            getFatalErrorFSRV '获取基础报价失败'
        }
    }

    private checkStartDate(context, lastBsPolicyEndDate) {
        def paramsMapping     = context.carVerify
        def minStartDateAdded = paramsMapping.UIBSStartDateMinMessage as int
        def dateType          = paramsMapping.UIBSStartDateMax
        def earlyValue        = paramsMapping.UIBSStartDateMaxMessage as int

        def today = today()
        def minStartDate = today.plusDays(minStartDateAdded)
        def maxStartDate = today
        if ('dd' == dateType) {
            maxStartDate = today.plusDays(earlyValue)
        } else if ('MM' == dateType) {
            maxStartDate = today.plusMonths(earlyValue)
        } else if ('YY' == dateType) {
            maxStartDate = today.plusYears(earlyValue)
        }

        def correctStartDate
        if (lastBsPolicyEndDate) {
            correctStartDate = getLocalDate(_DATE_FORMAT3.parse(lastBsPolicyEndDate)).plusDays(1)
        } else {
            correctStartDate = minStartDate
        }

        minStartDate < correctStartDate && maxStartDate >= correctStartDate
    }

}
