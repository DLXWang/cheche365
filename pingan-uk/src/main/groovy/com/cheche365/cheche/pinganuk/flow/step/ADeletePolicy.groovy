package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.POST



/**
 * 删除单号
 * Created by wangmz on 2016.10.31
 */
@Component
@Slf4j
abstract class ADeletePolicy implements IStep {

    @Override
    run(Object context) {
        if (!context.voucherNo && !context.applyPolicyList) {
            return context.lastDoneFSRV ?: getContinueFSRV(null)
        }

        def requestParams = getRequestParams context
        RESTClient client = context.client

        log.debug '删除单号的请求参数为：{}', requestParams

        client.request(POST, TEXT) { req ->
            uri.path = getApiPath()
            uri.query = requestParams

            response.success = { resp, reader ->
                log.info '删除单号成功'
                context.voucherNo = null
                context.applyPolicyList ? context.applyPolicyList.remove(0) : null
                context.lastDoneFSRV ?: getContinueFSRV(null)
            }

            response.failure = { resp, reader ->
                def errorMsg = htmlParser.parse(reader).depthFirst().ERRORMSG.first().text() ?: '非预期异常'
                log.error '删除单号失败，错误信息：{}', errorMsg

                getFatalErrorFSRV errorMsg
            }
        }
    }

    protected abstract getRequestParams(context)
    protected abstract getApiPath()

}
