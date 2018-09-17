package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**提交人工审核--》待核保
 * Created by chukh on 2018/6/6.
 */
@Component
@Slf4j
class InsureUnderInfo implements IStep {

    private static final _API_PATH_INSURE_UNDER_INFO = '/ecar/insure/insureUnderInfo'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_INSURE_UNDER_INFO,
            body              : [
                meta  : [:],
                redata: context.insureUnderInfo
            ]
        ]
        def result = client.post args, { resp, json ->
            json
        }
        log.debug '提交人工审核：{}', result
        if (result?.message?.code == 'success') {
            log.debug '影像已上传等待人工审核'
            // 上传图片成功，等待审核意见
            context.proposal_status = '影像已上传等待人工审核'
            getDoInsuranceFailedFSRV([quotationNo: context.quotationNo, type: this.class.name], '努力核保中，大约10分钟后查看结果')
        } else {
            getKnownReasonErrorFSRV '提交人工审核失败'
        }
    }


}
