package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getResponseResult
import static groovyx.net.http.ContentType.JSON



/**
 * 初始化详细信息提交表单 新步骤，目前只有重庆使用
 * Created by xushao on 2015/7/21.
 */
@Component
@Slf4j
class InitVehicleDetailInfo implements IStep {

    private static final _URL_PATH_INITVEHICLEDETAILINFO = 'cpiccar/salesNew/businessCollect/initVehicleDetailInfo'

    @Override
    run(context) {
        RESTClient client = context.client
        def bodyContent = [
            orderNo         : context.orderNo,
            random          : context.baseInfoResult?.random ?: '',
            otherSource     : '02',
            requestSource   : ''
        ]
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _URL_PATH_INITVEHICLEDETAILINFO,
            body              : bodyContent
        ]
        def result
        try {
            result = client.post args, { resp, json ->
                context.vehicleInfo += json.PolicyBaseInfo + json.VehicleInfo
                if(json.insurancePageStatus)
                    context.vehicleInfo.insurancePageStatus = json.insurancePageStatus
            }
        } catch (ex) {
            log.warn '初始化详细信息提交表单异常：{}，尝试重试', ex.message
            return getLoopContinueFSRV(null, '初始化详细信息提交表单异常')
        }

        if (context.vehicleInfo.productKind) {
            log.info "初始化车辆详细信息表单成功，获取到信息为：{}", context.vehicleInfo
            getResponseResult result, context, this
        } else {
            log.error '初始化车辆详细信息表单失败，请稍候重试'
            getFatalErrorFSRV '初始化车辆详细信息表单失败，请稍候重试'
        }
    }

}
