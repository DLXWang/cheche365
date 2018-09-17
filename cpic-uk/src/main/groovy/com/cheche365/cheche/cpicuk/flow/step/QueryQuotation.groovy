package com.cheche365.cheche.cpicuk.flow.step

import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static groovyx.net.http.ContentType.JSON



/**
 * @author: lp
 * @date: 2018/6/20 11:38
 * 查询报价单状态
 */
@Component
@Slf4j
class QueryQuotation {

    private static final _API_PATH_QUERY_QUOTATION_POLICY = '/ecar/quotationPolicy/queryQuotationPolicy'

    /**
     * 发送请求
     * @param context
     * @param quotationNo
     * @return
     */
    protected static postRequest(context, quotationNo) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_QUOTATION_POLICY,
            body              : [
                meta  : [:],
                redata: [
                    //根据报价单号是多条；投保单查询只有一条结果
                    quotationNo: quotationNo
                ]
            ]
        ]
        def result = client.post args, { resp, json -> json }
        log.debug '报价单的结果：{}', result
        result
    }
}
