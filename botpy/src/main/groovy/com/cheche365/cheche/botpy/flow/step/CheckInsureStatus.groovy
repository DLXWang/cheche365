package com.cheche365.cheche.botpy.flow.step

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
        getContinueFSRV context.waitIdentityCaptcha ? '直接校对身份验证码': 'UW_SUCC' == context.proposal_status ? '人工核保成功待发送身份证验证码': context.isNeedUpdateImage ? '需要上传影像': '未创建投保单'
    }

}
