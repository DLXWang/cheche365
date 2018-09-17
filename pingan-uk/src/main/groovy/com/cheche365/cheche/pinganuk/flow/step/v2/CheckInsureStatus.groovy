package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV


/**
 * 判断已核保状态
 */
@Component
@Slf4j
class CheckInsureStatus implements IStep {

    @Override
    run(context) {
        getContinueFSRV context.waitIdentityCaptcha ? '直接校对身份验证码' : context.applyPolicyNo && !context.waitIdentityCaptcha ? '核保成功未发送采集验证码' : '未创建投保单'
    }

}
