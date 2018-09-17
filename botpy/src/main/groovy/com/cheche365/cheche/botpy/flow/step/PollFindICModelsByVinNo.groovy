package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 轮询车架号车型查询结果
 */
@Component
@Slf4j
class PollFindICModelsByVinNo extends PollFindICModels {

    @Override
    protected dealResultFsrv(context, result) {
        if (result.is_success) {
            if (result.models) {
                log.debug "车架号车型查询成功，车型列表 VehicleList: {} ", result.models
                context.optionsByVinNo = result.models
                context.resultByVinNo = result
            }
        }
        getContinueFSRV result
    }

}
