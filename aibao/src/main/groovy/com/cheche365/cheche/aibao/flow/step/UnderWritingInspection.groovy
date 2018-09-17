package com.cheche365.cheche.aibao.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.aibao.util.BusinessUtils.sendParamsAndReceive



/**
 * Created by LIU GUO on 2018/8/24.
 */
@Slf4j
class UnderWritingInspection implements IStep {

    private static final interfaceID = '100069'

    @Override
    run(context) {
        def apply_context = [cityCode     : context.cityCode as String,
                             licenseNoFlag: '0',                    // 0-非新车，1-新车
                             licenseNo    : context.auto.licensePlateNo,
        ]
        def result = sendParamsAndReceive context, apply_context, log, interfaceID
        if (result.head.errorCode == '0000') {
            context.aiBaoTransactionNo = result.head.aiBaoTransactionNo
            getContinueFSRV context.aiBaoTransactionNo
        } else {
            log.error '承保检查请求失败， 后续步骤终止'
            getFatalErrorFSRV result.head.errorMsg
        }
    }

}
