package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getValueById


/**
 * 获取续保参数
 */
@Component
@Slf4j
class EditRenewalCopy implements IStep {

    private static final _API_PATH_EDIT_RENEWAL_COPY = '/prpall/business/editRenewalCopy.do'

    @Override
    Object run(Object context) {

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : TEXT,
            path              : _API_PATH_EDIT_RENEWAL_COPY,
            query             : [
                bizNo: context.bizNo
            ]
        ]

        def renewalBaseInfo = client.get args, { resp, reader ->
            htmlParser.parse(reader).depthFirst().INPUT
        }

        if (renewalBaseInfo) {
            context.renewallMap = getValueById(['ciStartDate', 'ciEndDate', 'ciStartHour', 'ciEndHour', 'prpCmain.operateDate', 'riskCode'], renewalBaseInfo)
            log.debug '获取续保请求参数 ： {}', context.renewallMap
            getContinueFSRV '获取续保请求参数 :{}' + context.renewallMap
        } else {
            getFatalErrorFSRV '获取续保请求参数异常'
        }

    }

}
