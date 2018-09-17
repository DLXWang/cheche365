package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.taikang.util.BusinessUtils.*



/**
 * 获取token
 * Created by xuecl on 2018/06/5.
 */
@Slf4j
class TokenStep implements IStep {

    private static final _FUNCTION = 'getToken'

    @Override
    run(Object context) {
        def applyContent = [:]
        def result = sendParamsAndReceive context, _FUNCTION, applyContent, log

        if (result.apply_content.reponseCode == '200') {
            context.token = result.apply_content.data.proposalFormToken
            getContinueFSRV context.token
        } else {
            log.info 'token请求失败， 后续步骤终止'
            getFatalErrorFSRV result.apply_content.messageBody
        }
    }
}
