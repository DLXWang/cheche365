package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * 承保
 */
@Component
@Slf4j
class CreatePolicy implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.createPolicy'

    @Override
    def run(Object context) {
        def params = [
            outTradeNo     : context.outTradeNo,                  //商户唯一订单号
            businessApplyNo: context.insurance?.proposalNo,           //商业险投保单号
            compelApplyNo  : context.compulsoryInsurance?.proposalNo, //交强险投保单号
            payTradeNo     : context.payTradeNo,                      //收银台交易流水号
            tradeNo        : context.tradeNo                          //众安唯一订单号
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        context.additionalParameters.result = result
        if ('0' == result.result) {
            context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances context, result.businessApplyNo, result.businessPolicyNo, context.compulsoryInsurance?.proposalNo, result.compelPolicyNo
            log.info '承保newQuoteRecordAndInsurances = {}', context.newQuoteRecordAndInsurances
            getContinueFSRV result
        } else {
            log.info '众安-->承保返回的异常信息 = {}', result
            getFatalErrorFSRV result
        }
    }

}
