package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Constants.get_API_PATH_CREATE_RENEWAL_INFO
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 轮询续保查询结果
 */
@Component
@Slf4j
class PollRenewals extends APollResponse {

    private static final _API_PATH_POLL_RENEWAL_INFO = '/requests/renewals/'

    @Override
    protected dealResultFsrv(context, result) {
        if (result.is_success) {
            def vehicleModel = result?.renewal?.renewal?.vehicle?.model
            def vehicleId = vehicleModel?.vehicle_data_id
            def vehicleName = vehicleModel?.vehicle_name
            log.debug "金斗云续保查询成功，续保品牌型号：{}，续保车型：{}，续保结果: {} ", vehicleName, vehicleId, result
            if (vehicleName) {
                context.renewalsInfo = [
                    vehicleName: vehicleName
                ]
            }
            //当用户选择车型时，不使用续保车型，使用用户选择车型报价
            if (vehicleId && !context.additionalParameters.supplementInfo.autoModel) {
                context.renewable = 1
                context.autoModel = vehicleId
            }
        } else {
            log.info "续保查询失败， 错误原因: {}", result
        }
        getContinueFSRV result
    }

    @Override
    protected getApiPath() {
        _API_PATH_POLL_RENEWAL_INFO
    }

    @Override
    protected getRequestIdPath() {
        _API_PATH_CREATE_RENEWAL_INFO
    }

    protected getPollTimes() {
        10
    }
}
