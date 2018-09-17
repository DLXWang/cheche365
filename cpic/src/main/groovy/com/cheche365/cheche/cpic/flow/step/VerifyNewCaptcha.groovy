package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * 校验新验证码并获取车型信息
 * @author wangxiaofei on 2016-08-16
 */
@Slf4j
class VerifyNewCaptcha implements IStep {

    private static final _API_PATH_VEHICLE_QUERY_CONFIRM = '/cpiccar/salesNew/businessCollect/vehicleQueryConfirm'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_VEHICLE_QUERY_CONFIRM,
            body              : [
                branchCode   : context.branchCode,
                orderNo      : context.orderNo,
                random       : context.baseInfoResult?.random ?: '',
                verifyCodeP09: context.captchaText
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }
        //这里result只返回车辆基本信息,并没有返回车型列表，因此此处不必推送补充信息，这也与官网一致，但是为了保险起见仍保留，同时也不会影响下一步骤中random的取值

        if ('chkErr' != result.errorCode) {
            log.info '成功校验验证码'
            // TODO 处理车型列表 不能确认是只有一辆车
            getSelectedCarModelFSRV context, result.vehicleList, result, [
                updateContext: { ctx, res, fsrv ->
                    ctx.moldCharacterCode = fsrv[2].moldCharacterCode
                    ctx.vehicleInfo += fsrv[2]
                }, wrapFsrv  : { fsrv ->
                getLoopBreakFSRV result
            }]
        }
      else {
            getLoopContinueFSRV result, '校验验证码失败'
        }
    }

}

