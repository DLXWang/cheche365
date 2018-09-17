package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 验证车辆信息
 */
@Component
@Slf4j
class SaveBaseInfo implements IStep {

    private static final _API_PATH_SAVE_BASE_INFO = 'Net/netCarInfoControl!saveBaseInfo.action'


    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_SAVE_BASE_INFO,
            body              : [
                'paraMap.licence'  : context.auto.licensePlateNo,
                'paraMap.contactor': context.auto.owner,
                'paraMap.mobileNum': randomMobile,
                'paraMap.idno'     : context.auto.identity,
                'paraMap.id'       : context.token,
                'paraMap.orgID'    : context.orgId,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        log.info 'SaveBaseInfo成功'
        getContinueFSRV result
    }

}
