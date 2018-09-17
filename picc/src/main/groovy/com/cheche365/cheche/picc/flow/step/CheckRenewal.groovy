package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 检查是否可以续保
 */
@Component
@Slf4j
class CheckRenewal implements IStep {

    @Override
    run(context) {

        def isChangedCar = context.extendedAttributes?.transferFlag
        if (isChangedCar) {
            log.info '选择了过户或者留牌换车，走转保流程'
            getContinueFSRV false
        } else if (context.renewalFlag != null) {
            def isRenewal = '1' == context.renewalFlag

            context.renewable = isRenewal
            log.info '是否可以续保：{}', isRenewal
            getContinueFSRV isRenewal
        } else {
            getFatalErrorFSRV '检查是否续保异常'
        }
    }

}
