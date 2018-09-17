package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getDefaultInsurancePeriodText
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static groovyx.net.http.ContentType.JSON



/**
 * 获取城市信息
 * @author wangmz
 */
@Component
@Slf4j
class GetCarProposal implements IStep {
    private static final _URL_GET_CAR_PROPOSAL = '/online/saleNewCar/carProposalsaveTemporary.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            contentType       : JSON,
            path              : _URL_GET_CAR_PROPOSAL,
            body              : [
                'proposalAreaCode'                            : context.deptId,
                'quoteNo'                                     : '',
                'isOldCustomer'                               : '0',
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        context.carVerify = result.temporary.carVerify

        // 根据城市取起保日期T+1 or T+2
        def bsStartDateText = getDefaultInsurancePeriodText(context.carVerify?.UIBSStartDateMinMessage)
        def bzStartDateText = getDefaultInsurancePeriodText(context.carVerify?.UIBZStartDateMinMessage)
        if (bsStartDateText) {
            log.info '获取默认商业险起保日期{}', bsStartDateText
            setCommercialInsurancePeriodTexts context, bsStartDateText
        }
        if (bzStartDateText) {
            log.info '获取默认交强险起保日期{}', bzStartDateText
            setCommercialInsurancePeriodTexts context, bzStartDateText
        }

        getContinueFSRV context.businessManFlag
    }

}
