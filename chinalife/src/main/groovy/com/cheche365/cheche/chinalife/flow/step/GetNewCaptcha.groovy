package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取新验证码
 */
@Component
@Slf4j
class GetNewCaptcha implements IStep {

    private static final _URL_OBTAIN_CHECK_CODE = '/online/saleNewCar/carProposalobtainCheckCode.do'

    @Override
    run(context) {
        RESTClient client = context.client

        Auto auto = context.auto

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_OBTAIN_CHECK_CODE,
            body              : [
                'temporary.quoteMain.areaCode'                         : context.deptId,
                'temporary.quoteMain.geQuoteCars[0].frameNo'           : getAutoVinNo(context),
                'temporary.quoteMain.geQuoteCars[0].licenseNo'         : auto.licensePlateNo,
                'temporary.quoteMain.geQuoteCars[0].licenseNoQueryFlag': '0'
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.temporary?.quoteMain?.geQuoteCars?.checkCode) {
            log.info '成功获得验证码'
            context.checkNo = result.temporary.quoteMain.geQuoteCars[0].checkNo
            context.imageBase64 = result.temporary.quoteMain.geQuoteCars[0].checkCode
            getContinueFSRV result
        } else {
            log.error '获取验证码失败：{}', result
            getLoopContinueFSRV result, '获取验证码失败'
        }
    }

}
