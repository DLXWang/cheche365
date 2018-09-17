package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Constants._API_PATH_CREATE_RENEWAL_INFO
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 创建续保查询请求
 */
@Component
@Slf4j
class PollRenewalInfo extends APollResponse {

    private static final _API_PATH_POLL_RENEWAL_INFO = '/requests/renewals'

    @Override
    protected dealResultFsrv(context, result) {
        def quoteResult = result.quotations.first()
        if (quoteResult.is_success) {
            log.debug "金斗云续保查询成功，续保结果: {} ", result
            getContinueFSRV(result)
        } else {
            log.info "续保失败， 错误原因: {}", quoteResult.message
            getFatalErrorFSRV quoteResult.message
        }
    }

    @Override
    protected getApiPath() {
        _API_PATH_POLL_RENEWAL_INFO
    }

    @Override
    protected getRequestIdPath() {
        _API_PATH_CREATE_RENEWAL_INFO
    }

}
