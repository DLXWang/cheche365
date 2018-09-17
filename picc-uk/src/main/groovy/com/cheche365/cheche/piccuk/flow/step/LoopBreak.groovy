package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV



/**
 * 用于流程控制
 */
@Component
@Slf4j
class LoopBreak implements IStep {

    @Override
    run(context) {
        getLoopBreakFSRV '结束循环'
    }
}

