package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**上传照片后，需要再次申请核保：是为了获取最新人工审核的请求体
 * Created by chukh on 2018/6/8.
 */
@Component
@Slf4j
class SubmitInsureInfoAgain implements IStep {

    private static final _API_PATH_SUBMIT_INSURE_INFO = '/ecar/insure/submitInsureInfo'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = [
            path              : _API_PATH_SUBMIT_INSURE_INFO,
            requestContentType: JSON,
            contentType       : JSON,
            body              : generateRequestParameters(context, this)

        ]
        def result = client.post args, { resp, json ->
            json
        }
        if (result?.message?.code == 'success') {
            log.debug '上传影像后再次提交投保信息'
            context.insureUnderInfo = result.result //下一个step“人工审核”需要的请求体
            getContinueFSRV(null)
        } else {
            log.error '再次提交投保信息失败：{}', result
            getFatalErrorFSRV '再次提交投保信息失败'
        }
    }
}
