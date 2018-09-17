package com.cheche365.cheche.botpy.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Constants._API_PATH_CREATE_FIND_IC_MODELS
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants.get_VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants.get_VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV



/**
 * 轮询保险公司车型查询结果
 */
@Component
@Slf4j
class PollFindICModels extends APollResponse {

    private static final _API_PATH_POLL_FIND_IC_MODELS = '/requests/ic-models/'

    @Override
    protected dealResultFsrv(context, result) {
        if (result.is_success) {
            if (result.models) {
                log.debug "车型查询成功，进入选车阶段 VehicleList: {} ", result.models
                getSelectedCarModelFSRV(context, result.models, result, [updateContext: { ctx, res, fsrv ->
                    ctx.carInfo = fsrv[2]
                }])
            } else {
                if (context.hasAlreadyQueriedICModels) {
                    context.hasAlreadyQueriedICModels = false
                    getContinueFSRV '未查到车型列表'
                } else {
                    def hints = [
                        _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                            it.originalValue = context.auto.vinNo
                            it
                        },
                        _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                            it.originalValue = context.auto.autoType?.code
                            it
                        }
                    ]
                    getProvideValuableHintsFSRV { hints }
                }
            }
        } else {
            log.error "车型查询失败： {}", result
            getFatalErrorFSRV result
        }
    }

    @Override
    protected getApiPath() {
        _API_PATH_POLL_FIND_IC_MODELS
    }

    @Override
    protected getRequestIdPath() {
        _API_PATH_CREATE_FIND_IC_MODELS
    }

}
