package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.generateRenewalPackage
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 快速检索信息反馈
 * Created by wangxiaofei on 2016.8.29
 */
@Component
@Slf4j
class QuickSearchVoucher implements IStep {

    private static final _API_QUICK_SEARCH_VOUCHER = '/icore_pnbs/do/app/quotation/quickSearchVoucher'

    @Override
    run(context) {

        RESTClient client = context.client
        // 快速检索可能得到多条历史信息，经查传统报价系统只允许选择两条信息（即商业与交强）且取最近历史信息，
        // 因此在此先对商业交强进行分类，再排序选择最近历史信息
        def selectedPolicies = context.policy?.groupBy { allPolicy ->
            allPolicy.planCode
        }?.collectEntries { planCode, policies ->
            def lastDatePolicy = policies.sort { policy ->
                _DATE_FORMAT3.parse(policy.insuranceBeginTime as String)
            }?.last()
            [(planCode): lastDatePolicy]
        }

        def policyParams = selectedPolicies.collectEntries { planCode, lastDatePolicy ->
            [("${lastDatePolicy.planCode.toLowerCase()}PolicyNo".toString()): lastDatePolicy.policyNo]
        }

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_QUICK_SEARCH_VOUCHER,
            body              : [
                isFromCNBS: '0',
                nbaHotshot: 'nbaHotshot'
            ] + policyParams
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            def voucher = result.voucher
            def c01BaseInfo = voucher?.c01BaseInfo
            def c51BaseInfo = voucher?.c51BaseInfo
            //不确定autoModelType与vehicleTarget什么关系，vehicleTarget是否包含autoModelType
            context.selectedCarModel = result.autoModelType
            context.voucher = voucher
            context.selectedPolicies = selectedPolicies

            if (c01BaseInfo?.insuranceBeginTime) {
                setCommercialInsurancePeriodTexts context, c01BaseInfo.insuranceBeginTime
            }
            if (c51BaseInfo?.insuranceBeginTime) {
                setCompulsoryInsurancePeriodTexts context, c51BaseInfo.insuranceBeginTime
            }

            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                context.insurancePackage = generateRenewalPackage voucher.c01DutyList
                afterGeneratedRenewalPackage context
                log.info '获取到续保套餐：{}', context.insurancePackage
            }

            log.info '快速检索信息成功，车型{}', context.selectedCarModel
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '快速检索信息失败'
        }
    }

}
