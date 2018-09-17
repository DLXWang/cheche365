package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.flow.Constants._STATUS_CODE_OK
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 判断已核保状态
 */
@Component
@Slf4j
class CheckInsureStatus implements IStep {

    @Override
    run(context) {
        if (context.proposal_status == '核保流程完成') {
            log.info '核保完成，请去支付'
            [_ROUTE_FLAG_DONE, _STATUS_CODE_OK, true, null]
        } else {
            def payload = context.waitIdentityCaptcha ? '直接校对身份验证码' : context.proposal_status == '核保成功' ? '采集客户的身份信息'
                : context.proposal_status == '需上传影像' ? '上传影像资料' : context.proposal_status == '影像已上传等待人工审核' ?
                '查询审核结果以继续核保' : '未创建投保单'
            log.debug '核保状态为：{}', payload
            getContinueFSRV payload
        }
    }
}

