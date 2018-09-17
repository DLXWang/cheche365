package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getStandardHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * Created by sufc on 2017/11/14.
 */
@Component
@Slf4j
class QuoteConfirm implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.quoteConfirm'

    @Override
    Object run(Object context) {


        def params = [
            insureFlowCode         : context.insureFlowCode,                  //投保流程编码
            isInsureCompelInsurance: 1,           //是否投保交强险
        ]


        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.info '报价确认result = {}', result

        if ('0' == result.result) {
            getContinueFSRV result
        } else {

//            def hints = [
//                    _VALUABLE_HINT_INSURED_ID_TEMPLATE_QUOTING.with {
//                        it.hints = [
//                                '输入错误',
//                                '不是上年被保人身份证'
//                        ]
//                        it
//                    }
//            ]
//            getValuableHintsFSRV context, hints
            log.error '报价确认失败 : {}', result.resultMessage
            getStandardHintsFSRV result
        }


    }
}
