package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 报价前获取已经确认的信息
 */
class QueryInsureInfo implements IStep {

    private static final _API_PATH_QUERY_INSURE_INFO = '/ecar/insure/queryInsureInfo'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_INSURE_INFO,
            body              : [
                redata: [quotationNo: context.quotationNo]
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }
        if (result && 'success' == result.message.code) {
            context.ecarvo = result.result.ecarvo
            if (context.carVo) {
                context.ecarvo = context.ecarvo + context.carVo
            }
            getContinueFSRV result.result
        } else {
            getFatalErrorFSRV '获取报价前信息失败'
        }

    }
}
