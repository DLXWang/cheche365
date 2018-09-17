package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC



/**
 * 保存
 */
@Component
@Slf4j
abstract class AInsertUpdate implements IStep {

    def _INSERT_URL_PATH_BY_CITY_CODE = [
        110000L: '/prpall/business/insert4S.do',
        default: '/prpall/business/insert.do'
    ]

    @Override
    run(context) {
        RESTClient client = context.client
        def result = client.request(Method.POST) { req ->
            requestContentType = URLENC
            contentType = HTML
            uri.path = getObjectByCityCode(context.area, _INSERT_URL_PATH_BY_CITY_CODE)
            body = getUpdateParameters(context)
            response.success = { resp, html ->
                html
            }
            response.failure = { resp, html ->
                log.error '更新保存报价失败'
                null
            }
        }
        log.debug '再次保存报价{} ：{}', context.proposalNos, result ? '成功' : '失败'
        getFsrv(result)
    }

    abstract protected getUpdateParameters(context)

    abstract protected getFsrv(result)

}
