package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map

/**
 * 车险保单状态查询
 */
@Slf4j
class AppNoStatus implements IStep {

    private static final _TRAN_CODE = 100007

    @Override
    run(context) {

        def result = sendAndReceive2Map(context, getRequestParams(context), log)
        def head = result.PACKET.HEAD
        if ('C00000000' == head.RESPONSECODE) {
            def body = result.PACKET.BODY
            def status = '1' == body.UDR_MRK ? 0 : ('1' == body.IS_IMAGE && !(body.IMAGE_STATE in ['010004'])) ? 1 : 2
            getContinueFSRV(status)
            // TODO:预核保010003, 需要人工线下审核直接,当做核保失败了
        } else {
            getFatalErrorFSRV head.ERRORMESSAGE
        }
    }


    private static getRequestParams(context) {
        def body = [
            [
                APP_INFO: [
                    PLY_APP_NO: context.SY_PLY_APP_NO
                ]
            ],
            [
                APP_INFO: [
                    PLY_APP_NO: context.JQ_PLY_APP_NO
                ]
            ]
        ]
        createRequestParams(context, _TRAN_CODE, body)
    }

}
