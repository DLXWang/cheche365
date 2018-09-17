package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取手机验证码（北京地区）
 */
@Component
@Slf4j
class GetIssueCode implements IStep {

    private static final _URL_PATH_GET_ISSUE_CODE = '/cpiccar/salesNew/businessCollect/getIssueCode'

    @Override
    run(context) {
        RESTClient client = context.client
        context.insuredphoneNo = randomMobile
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_PATH_GET_ISSUE_CODE,
            body              : [
                insuredphoneNo: context.insuredphoneNo
            ]
        ]

        def result = null
        try {
            result = client.post args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn '获取手机验证码非预期异常：{}。稍后重试', ex.message
            getLoopContinueFSRV null, '无法报价'
        }


        if (result) {
            log.info "获取手机验证码：{}", result
            context.issueCode = result
            getLoopBreakFSRV result
        } else {
            getLoopContinueFSRV null, '获取获取手机验证码失败'
        }
    }

}
