package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * Created by chukh on 2018/5/10.
 * 采集身份信息
 */
@Component
@Slf4j
class IDCardCollect implements IStep {

    private static final _API_PATH_ID_COLLECT = '/ecar/collect/IDCardCollect'

    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def updateArgsBody = context.updateArgsBody
        updateArgsBody.redata.saveOrUpload = 1
        def args = [
            path              : _API_PATH_ID_COLLECT,
            requestContentType: JSON,
            contentType       : JSON,
            body              : updateArgsBody
        ]
        log.debug '请求体：\n{}', args.body
        def result = client.post args, { resp, json ->
            json
        }
        log.debug '身份信息采集：{}', result
        def code = result.message.code
        def message = result.message.message
        if (code == 'success' && message == '信息采集成功') {
            log.debug '身份证信息采集成功,即将调用发送短信验证码接口'
            //流程分支控制去 发送短信验证码
//            context.proposal_status = '信息采集成功'
            getLoopBreakFSRV null
        } else {
            log.error '身份信息采集失败，太平洋返回的提示信息为：{}', message
            getLoopContinueFSRV null, message
        }
    }
}
