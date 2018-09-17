package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Constants._API_PATH_CREATE_FIND_IC_MODELS
import static com.cheche365.cheche.botpy.util.BusinessUtils.getQuoteGroup
import static com.cheche365.cheche.botpy.util.BusinessUtils.getRequestIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setRequestIdForPath
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static groovyx.net.http.Method.POST



@Component
abstract class ACreateFindICModels implements IStep {

    @Override
    run(context) {
        def cityCodeMappings = context.cityCodeMappings

        def body = [
            prov_code  : cityCodeMappings.prov_code,
            city_code  : cityCodeMappings.city_code,
            ics        : getQuoteGroup(context),
            value_type : 'value',
            enroll_date: _DATE_FORMAT3.format(context.auto.enrollDate ?: new Date()),
//            renewal_request_id:''
        ] + vehicleModelConditions(context)

        def result = sendParamsAndReceive context, _API_PATH_CREATE_FIND_IC_MODELS, body, POST, log

        if (result.error) {
            log.error '创建保险公司车型查询请求失败， 后续步骤终止'
            getFatalErrorFSRV result.error
        } else {
            context.hasAlreadyQueriedICModels = true
            setRequestIdForPath context, result, _API_PATH_CREATE_FIND_IC_MODELS
            getContinueFSRV getRequestIdForPath(context, _API_PATH_CREATE_FIND_IC_MODELS)
        }


    }

    abstract protected vehicleModelConditions(context)

}
