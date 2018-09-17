package com.cheche365.cheche.aibao.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 是否校验身份.
 */
@Slf4j
class CheckInsureStatus implements IStep {

    @Override
    run(context) {
        getContinueFSRV context.waitIdentityCaptcha ? '直接校对身份验证码' : '未创建投保单'
    }

}
