package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.huanong.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static com.cheche365.cheche.huanong.flow.Constants._SUCCESS


/**
 * 核保状态查询接口
 * Created by LIU GUO on 2018/8/13.
 */
@Slf4j
class ProposalStatusQry implements IStep {

    private static final _TRAN_CODE = 'UndwrtQuery'

    @Override
    run(Object context) {
        def result = sendParamsAndReceive context, getRequestParams(context), _TRAN_CODE, log

        if (result.head.responseCode == _SUCCESS) {
            if (result.status == '1') {
                getContinueFSRV '核保通过，直接支付'
            } else if (result.status in ['2', '4']) {
                getDoInsuranceFailedFSRV([orderNo: context.orderNo, type: this.class.name], '努力核保中，大约10分钟后查看结果')
            } else {
                log.error '该笔订单审核失败，失败原因{}', result.head.responseMsg
                getFatalErrorFSRV result.head.responseMsg
            }
        } else {
            log.error '核保状态查询接口失败，失败原因{}', result.head.responseMsg
            getFatalErrorFSRV result.head.responseMsg
        }
    }

    private static getRequestParams(context) {
        def params = [
            orderNo: context.orderNo,
        ]
        createRequestParams context, _TRAN_CODE, params
    }
}
