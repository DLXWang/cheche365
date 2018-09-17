package com.cheche365.cheche.rest.util

import static com.cheche365.cheche.common.flow.Constants.*

/**
 * Created by zhengwei on 12/20/16.
 */
class WebFlowUtil {

    static final  _STATUS_OK = 200
    static final  _STATUS__LAZY_END_FLOW_ERROR = 500

    /**
     * 返回业务预期错误
     * @param code：BusinessException的code
     * @param msg
     * @return
     */
    static getBusinessErrorFSRV(code, msg) {
        [_ROUTE_FLAG_DONE, code, null, msg]
    }

    /**
     * 返回需要后续流程处理的错误，当前流程继续执行
     * @param code
     * @param msg
     * @return
     */
    static getLazyEndFlowErrorFSRV(msg) {
        [_ROUTE_FLAG_CONTINUE, _STATUS__LAZY_END_FLOW_ERROR, null, msg]
    }
}
