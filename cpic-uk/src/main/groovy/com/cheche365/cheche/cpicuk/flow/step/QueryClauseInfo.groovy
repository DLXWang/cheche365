package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**根据报价单号查询报价条款信息
 * Created by chukh on 2018/5/8.
 */
@Component
@Slf4j
class QueryClauseInfo implements IStep {

    private static final _API_PATH_QUERY_CLAUSE_INFO = '/ecar/insure/queryClauseInfo'

    @Override
    run(Object context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_CLAUSE_INFO,
            body              : [
                meta  : [:],
                redata: [
                    quotationNo: context.quotationNo
                ]
            ]
        ]
        //发送请求获取结果json数据
        def result = client.post args, { resp, json -> json }
        log.debug '查询报价信息：{}', result
        if (result?.message?.code == 'success') {
            def clauseInfo = result.result
            log.debug '根据报价单号查询的报价单：{}', clauseInfo
            context.clauseInfo = clauseInfo
            getContinueFSRV null
        } else {
            log.error '根据报价单号：{}查询报价失败：{}', context.quotationNo, result.message.message
            getKnownReasonErrorFSRV result.message.message
        }
    }
}

