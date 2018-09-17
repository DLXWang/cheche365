package com.cheche365.cheche.piccuk.flow.step

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
        def payload = context.proposalStatus == '需上传影像' ? '上传影像资料' : context.proposalStatus == '影像已上传等待人工审核' ?
            '查询审核结果以继续核保' : context.proposalStatus == '身份采集验证码发送失败' ? '身份采集验证码发送失败' : context.proposalStatus == '身份采集验证码发送成功' ? '身份采集验证码发送成功' : '去核保'
        log.debug '核保状态为：{}', payload
        getContinueFSRV payload
    }
}

