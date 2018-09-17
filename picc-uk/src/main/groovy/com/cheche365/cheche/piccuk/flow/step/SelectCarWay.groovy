package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV


/**
 * 选择自动选车/选车 流程
 */
@Component
@Slf4j
class SelectCarWay implements IStep {

    @Override
    Object run(Object context) {
        if (context.additionalParameters.referToOtherAutoModel) {
            log.debug '人保小鳄鱼：集成层提示走原选车流程'
            getContinueFSRV '选车'
        } else {
            log.debug '人保小鳄鱼：集成层提示走自动选车流程'
            getContinueFSRV '自动选车'
        }
    }


}
