package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 核对投保公司信息
 */
@Slf4j
class GetCheckUserMsg implements IStep {

    private static final _API_PATH_GET_CHECK_USER_MSG = '/prpall/business/getCheckUserMsg.do'

    @Override
    run(context) {

        RESTClient client = context.client

        def baseInfo = context.baseInfo
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CHECK_USER_MSG,
            body              : [
                'comCode'  : baseInfo.comCode,
                'agentCode': baseInfo.agentCode,
            ]
        ]

        def result = client.post args, { resp, json ->
            if (1 == json.totalRecords) {
                json.data[0]
            }
        }
        context.qualificationNo = result.permitNo
        log.info '获取保险公司代码：{}', result.permitNo

        getContinueFSRV result.permitNo
    }

}
