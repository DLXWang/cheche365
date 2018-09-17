package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import groovy.util.logging.Slf4j



/**
 * 校验保单状态
 * Created by LIU GUO on 2018/6/13.
 */
@Slf4j
class CheckInsureStatus implements IStep {

    @Override
    run(context) {
        getContinueFSRV context.waitIdentityCaptcha ? '直接校对身份验证码' : '未创建投保单'
    }

}
