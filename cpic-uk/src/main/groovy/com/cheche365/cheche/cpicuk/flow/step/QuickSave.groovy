package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 生成报价单并获取quotationNo
 */
@Component
@Slf4j
class QuickSave implements IStep {

    private static final _API_PATH_QUERY_QUOTATIONNO = '/ecar/ecar/quickSave'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_QUOTATIONNO,
            body              : generateRequestParameters(context, this)
        ]


        def saveResult = client.post args, { resp, json ->
            json
        }

        if (saveResult.result) {
            log.debug '报价单 quotationNo ：{}', saveResult.result.quotationNo
            context.quotationNo = saveResult.result.quotationNo
            getContinueFSRV saveResult.result
        } else {
            getFatalErrorFSRV '初始化报价单失败'
        }


    }

}
