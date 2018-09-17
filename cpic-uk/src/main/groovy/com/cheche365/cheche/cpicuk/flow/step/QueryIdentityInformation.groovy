package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * Created by chukh on 2018/5/10.
 * 查询身份信息，身份信息采集可将该步骤查询的直接发送过去
 */
@Component
@Slf4j
class QueryIdentityInformation implements IStep {

    private static final _API_PATH_QUERY_ID_INFO = '/ecar/query/queryIdentityInformation'


    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_ID_INFO,
            body              : [
                meta  : [:],
                redata: [
                    quotationNo: context.quotationNo
                ]
            ]
        ]
        def result = client.post args, { resp, json -> json }
        log.debug '查询身份信息：{}', result
        //将投保人的手机号，保单中投保人手机号，存到上下文中，在身份信息的API更新手机时，可以再更新context中的手机号
        def holderTelephone = result?.result?.holderVo?.telephone
        context.holderTelephone = holderTelephone
        if (result.message.code == 'success') {
            context.queryIdentityInformation = result.result
            log.debug '查询身份信息：{}', result.result
            getContinueFSRV null
        } else {
            log.error '查询身份信息失败，请重试'
            getLoopContinueFSRV null, '查询身份信息失败,请重试'
        }

    }
}
