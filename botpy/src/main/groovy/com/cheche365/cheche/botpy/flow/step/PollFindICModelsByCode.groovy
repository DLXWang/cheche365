package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 轮询品牌型号车型查询结果
 */
@Component
@Slf4j
class PollFindICModelsByCode extends PollFindICModels {

    @Override
    protected dealResultFsrv(context, result) {
        if (result.is_success) {
            if (result.models) {
                log.debug "品牌型号车型查询成功，车型列表 VehicleList: {} ", result.models
                context.optionsByCode = result.models
                context.resultByCode = result
            }
        }
        getContinueFSRV result
    }

}
