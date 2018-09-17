package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map

/**
 * 北京短信接口
 */
@Slf4j
class SendVerificationCode implements IStep {

    private static final _TRAN_CODE = 100020

    @Override
    run(context) {

        def result = sendAndReceive2Map(context, getRequestParams(context), log)
        def head = result.PACKET.HEAD
        if ('C00000000' == head.RESPONSECODE) {
            log.info '北京地区身份采集成功'
            getContinueFSRV('北京地区身份采集成功')
        } else {
            getFatalErrorFSRV head.ERRORMESSAGE
        }
    }



    private static getRequestParams(context) {
        def body = [
            BASE: [
                PLY_APP_NO_SY: context.JQ_PLY_APP_NO,
                PLY_APP_NO_JQ: context.SY_PLY_APP_NO
            ]
        ]
        createRequestParams(context, _TRAN_CODE, body)
    }

}
