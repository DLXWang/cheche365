package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 检查是否可以续保。
 * 当续保标识renewable为true并且留牌换车标识changedCar为false时，走续保流程。
 */
@Component
@Slf4j
class CheckRenewal implements IStep {

    @Override
    run(context) {
        getContinueFSRV((context.renewable && !context.extendedAttributes?.transferFlag) ?: false)
    }

}
