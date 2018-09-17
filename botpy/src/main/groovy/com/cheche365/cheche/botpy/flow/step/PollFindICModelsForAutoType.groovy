package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo



/**
 * 轮询保险公司车型查询结果
 */
@Component
@Slf4j
class PollFindICModelsForAutoType extends PollFindICModels {

    @Override
    protected dealResultFsrv(context, result) {
        if (result.is_success) {
            if (result.models) {
                log.debug "车型查询成功，进入选车阶段 VehicleList: {} ", result.models
                def fsrv = checkVehicleSupplementInfo context, result.models, context.getVehicleOption, false, { ctx, item -> item }, true
                context.newAutoTypes = fsrv[-1]()[0].options
                getContinueFSRV context.newAutoTypes
            } else {
                if (context.hasAlreadyQueriedICModels) {
                    getContinueFSRV '未查到车型列表'
                } else {
                    getFatalErrorFSRV '查询车型失败'
                }
            }
        } else {
            log.error "车型查询失败： {}", result
            getFatalErrorFSRV result
        }
    }

}
