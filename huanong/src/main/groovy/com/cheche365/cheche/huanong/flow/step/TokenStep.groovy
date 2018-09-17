package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.huanong.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.huanong.flow.Constants._SUCCESS


/**
 * 登录,获取token
 * create by wangpeng
 *
 */
@Slf4j
class TokenStep implements IStep {

    private static final _TRAN_CODE = 'Login'//华农接口标识

    @Override
    run(Object context) {
        def result = sendParamsAndReceive context, getRequestParams(context), _TRAN_CODE, log

        if (result.head.responseCode == _SUCCESS) {
            context.token = result.token
            getContinueFSRV context.token
        } else {
            log.error 'token请求失败，后续步骤终止'
            getFatalErrorFSRV result.head.responseMsg
        }
    }

    private static getRequestParams(context) {
        def params = [
            UserCode    : context.user,
            ComCode     : context.comCode,
            AgentCode   : context.agentCode,
            AgreementNo : context.agreementNo,
            producerCode: ''
        ]
        createRequestParams context, _TRAN_CODE, params
    }

}
