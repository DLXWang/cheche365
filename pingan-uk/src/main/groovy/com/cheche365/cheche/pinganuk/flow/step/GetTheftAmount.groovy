package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.getEnrollDate
import static groovyx.net.http.ContentType.JSON

/**
 * 获取盗抢险保额（即车辆折损价）
 * Created by liheng on 2016.9.6
 */
@Component
@Slf4j
class GetTheftAmount implements IStep {

    private static final _API_PATH_CALCULATE_STEAL_ROBINSURED_AMOUNT = '/icore_pnbs/do/app/calculate/defaultCalculate'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_CALCULATE_STEAL_ROBINSURED_AMOUNT,
            body              : [
                _cflag              : 'calculateStealRobInsuredAmount',
                purchasePriceToWrite: context.selectedCarModel.purchasePrice,
                dateFirstRegister   : getEnrollDate(context),
                monthDeprecition    : '0.006',
                insuranceBeginTime  : getCommercialInsurancePeriodTexts(context).first,
                calculateType       : 'calculateStealRobInsuredAmount'
            ]
        ]

        def result = client.post args, { resp, json ->
            json.calculateResult
        }

        if (result?.stealRobInsuredAmount) {
            context.theftAmount = result.stealRobInsuredAmount

            log.info '盗抢险保额，{}', result.stealRobInsuredAmount
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '获取盗抢险保额失败'
        }
    }

}
