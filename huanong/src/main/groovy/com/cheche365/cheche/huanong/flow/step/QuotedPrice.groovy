package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.huanong.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.parser.util.BusinessUtils.getNotQuotedPolicyCauseFSRV
import static com.cheche365.cheche.huanong.util.BusinessUtils.postProcessQuoting

/**
 * 报价
 * create by wangpeng
 * 报价时候验证码处理,由于华农不区分商业险和交强险验证码，当包含商业和交强险时，推送交强验证码，单险时候推送对应险种校验码
 */
@Slf4j
class QuotedPrice implements IStep {

    private static final _TRAN_CODE = 'QuotedPrice'

    private static final _RESULT_HANDLER = { context, result ->
        //报价后处理
        postProcessQuoting context, result, log
    }

    private static final _BRANCHES = [
        _RESULT_HANDLER,
    ]

    @Override
    run(Object context) {
        //检查交强险、商业险都能投保
        def fsrv = getNotQuotedPolicyCauseFSRV context
        if (fsrv) {
            return fsrv
        }

        log.info '华农报价'
        def parameter = createRequestParams context, _TRAN_CODE, generateRequestParameters(context, this)//获取报价报文
        context.firstQuotePriceReqJSON = parameter//保存报价报文,在核保时用该报文重新报价

        def result = sendParamsAndReceive context, parameter, _TRAN_CODE, log

        context.orderNo = result.orderNo //订单号
        _BRANCHES.findResult { handler ->
            handler context, result
        }
    }
}
