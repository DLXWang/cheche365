package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakWithIgnorableErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC


/**
 * 历史用户验证图片校验码并获取历史客户信息
 */
@Component
@Slf4j
class CheckReuse implements IStep {

    private static final _API_PATH_CHECK_REUSE = '/newecar/reuse/checkReuse'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_CHECK_REUSE,
            body              : [
                random  : context.captchaText,
                tokenNo : context.auto.insuredIdNo ?: context.auto.identity,
                uniqueID: context.uniqueID
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.resultFlag) {
            log.info '成功校验验证码'

            // TODO: 历史客户返回信息calculateTempDto.prptempinsureds可能包含投保人、被保人、车主的信息，就目前测试发现其三项信息均一致
            context.vehicleInfo = result.calculateTempDto.prptempcarinfo
            context.insuredInfo = result.calculateTempDto.prptempinsureds[1]
            context.commercialInfo = [
                startdatebi: result.calculateTempDto.prptempmaininfo1.startdatebi,
                enddatebi  : result.calculateTempDto.prptempmaininfo1.enddatebi
            ]
            if (context.vehicleInfo.carModelDetail || context.vehicleInfo.modelDesc) {
                getLoopBreakFSRV context.vehicleInfo
            } else {
                getLoopBreakWithIgnorableErrorFSRV false, '历史客户信息不完整，走转保流程'
            }
        } else {
            getLoopBreakWithIgnorableErrorFSRV false, '校验验证码失败'
        }
    }

}
