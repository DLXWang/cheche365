package com.cheche365.cheche.baoxian.flow.step.v2

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import com.cheche365.cheche.common.flow.IStep
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getStartDateSupplementInfo
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static groovyx.net.http.ContentType.JSON

/**
 * @author wangxin
 */
@Component
@Slf4j
class AccessToken implements IStep {

    private static final _API_PATH_TOKEN = '/cm/channelService/getToken'

    @Override
    run(context) {

        def startDateSupplementInfo = getStartDateSupplementInfo(context)
        if (startDateSupplementInfo) {
            log.info '需要补充起保日期：{}', startDateSupplementInfo
            return getSupplementInfoFSRV(startDateSupplementInfo)
        }

        def params = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_TOKEN,
            body              : [
                channelId    : context.channelId,
                channelSecret: context.channelSecret
            ]
        ]

        def result = context.client.post params, { resp, json ->
            json
        }

        if ('00' == result?.respCode) {
            context.token = result.accessToken
            log.info '成功获取token:{}', context.token
            getContinueFSRV result.accessToken
        } else {
            getFatalErrorFSRV '获取token失败'
        }
    }
}
