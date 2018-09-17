package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.generateRenewalPackage
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getNewStartDate
import static com.cheche365.cheche.chinalife.util.BusinessUtils.startDateInPeriod
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.util.FlowUtils.getInsurancesNotAllowedFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isDefaultStartDate
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.InsuranceUtils.afterGeneratedRenewalPackage
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * Created by suyq on 2015/9/25.
 * 续保判断
 */
@Component
@Slf4j
class GetPolicy implements IStep {

    private static final _URL_GET_POLICY = '/online/saleNewCar/carProposalgetPolicy.do'

    @Override
    run(Object context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_GET_POLICY,
            body              : [
                'temporary.licenseNo'                                  : context.auto.licensePlateNo,
                'temporary.proposalAreaCode'                           : context.deptId,
                'temporary.quoteMain.geQuoteCars[0].unCarLicenceNoFlag': '0'
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        def temporary = result?.temporary
        def policyEndDate = temporary?.policyEndDate ? getNewStartDate(temporary.policyEndDate).first() as String : null
        def bsStartDateText = result?.bsDate ? getNewStartDate(result.bsDate).first() as String : policyEndDate
        def bzStartDateText = result?.bzDate ? getNewStartDate(result.bzDate).first() as String : policyEndDate
        context.renewable = '0' == temporary?.resultType
        log.info '是否续保:{}', context.renewable
        if (context.renewable) {
            def useNatureCode = temporary?.quoteMain?.geQuoteCars[0]?.useNatureCode
            if (4 == result?.result && (!useNatureCode || '8A' != useNatureCode)) {
                log.info '非家庭自用车,无法提供网上自助投保服务'
                return getInsurancesNotAllowedFSRV('非家用车,无法续保')
            }

            if (4 == result?.result && (temporary?.quoteMain?.geQuoteCars[0]?.carKindCode == '摩托车')) {
                log.info '{}不允许投保', temporary?.quoteMain?.geQuoteCars[0]?.carKindCode
                return getKnownReasonErrorFSRV('摩托车不能投保')
            }

            if (bsStartDateText && context.carVerify && !startDateInPeriod(context, bsStartDateText, 'UIBSStartDateMaxMessage')) {
                log.info '续保开始日期 {} 不在投保期内', bsStartDateText
                return getInsurancesNotAllowedFSRV(bsStartDateText)
            }

            if (_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType && !context.insurancePackage) {
                context.insurancePackage = generateRenewalPackage temporary.quoteMain.geQuoteItemkinds
                log.info '续保套餐 insurancePackage：{}', context.insurancePackage
                afterGeneratedRenewalPackage context
            }

        }
        if (bsStartDateText && !isDefaultStartDate(bsStartDateText)) {
            setCommercialInsurancePeriodTexts context, bsStartDateText
        }
        if (bzStartDateText && !isDefaultStartDate(bzStartDateText)) {
            setCompulsoryInsurancePeriodTexts context, bzStartDateText
        }
        context.carRenewalInfo = temporary?.quoteMain?.geQuoteCars?.get(0)
        context.geQuoteItemkinds = temporary?.quoteMain?.geQuoteItemkinds
        context.geQuoteParties = temporary?.quoteMain?.geQuoteParties
        context.riskCode = temporary?.quoteMain?.riskCode
        context.transferBreakRuleRecordFlag = temporary?.carVerify?.UIoldCarTransferRecord == '1' ? '1' : '0'
        context.oldOriginalRiskCode = temporary?.oldOriginalRiskCode
        // 判断是否是过户车
        if (context.carRenewalInfo?.transferFlag) {
            context.transferFlag = context.carRenewalInfo?.transferFlag
        }
        getContinueFSRV result
    }


}
