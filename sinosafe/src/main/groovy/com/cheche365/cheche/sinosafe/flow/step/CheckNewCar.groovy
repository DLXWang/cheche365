package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV


/**
 * 新车检测接口
 */
@Slf4j
class CheckNewCar implements IStep {

    @Override
    run(context) {
        if (context.additionalParameters.supplementInfo?.newCarFlag) {
            getKnownReasonErrorFSRV '该地区暂不支持新车业务'
        } else {
            getContinueFSRV '流程继续'
        }
    }
}
