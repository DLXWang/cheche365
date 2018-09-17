package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
/**
 * 快速检索
 * Created by wangxiaofei on 2016.8.29
 */
@Component
@Slf4j
class QuickSearch implements IStep {

    private static final _API_QUICK_SEARCH = '/icore_pnbs/do/quote/quickSearch'

    @Override
    run(context) {
        context.renewable = false
        log.info '是否可以续保：{}', context.renewable
        getLoopBreakFSRV context.renewable

//        RESTClient client = context.client
//
//        def baseInfo = context.baseInfo
//        def requestParams = [
//            departmentCode    : baseInfo.departmentCode,
//            employeeCode      : baseInfo.saleAgentCode,
//            vehicleLicenceCode: resolveAutoLicensePlate(context.auto.licensePlateNo),
//            isLoanVehicle     : 0
//        ]
//
//        client.request(POST, JSON) { req ->
//            uri.path = _API_QUICK_SEARCH
//            body = requestParams
//
//            response.success = { resp, json ->
//                if (json.policy) {
//                    context.policy = json.policy
//                    context.renewable = true
//                } else {
//                    context.renewable = false
//                }
//                log.info '是否可以续保：{}', context.renewable
//                getLoopBreakFSRV context.renewable
//            }
//
//            response.failure = { resp ->
//                log.warn '快速检索失败：{}', resp.status
//                getLoopContinueFSRV resp, '响应异常重试'
//            }
//        }
    }

}
