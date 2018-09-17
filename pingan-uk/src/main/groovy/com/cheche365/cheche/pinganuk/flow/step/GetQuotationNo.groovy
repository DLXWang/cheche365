package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取询价单号
 * Created by liheng on 2016-09-02 0002.
 */
@Slf4j
class GetQuotationNo implements IStep {

    private static final _URL_MAINCTRL = '/icore_pnbs/mainCtrl.tpl'

    @Override
    run(context) {
        def baseInfo = context.baseInfo
        def bsDetailCode = baseInfo.subMap(
            ['businessSourceCode', 'businessSourceDetailCode', 'channelSourceCode', 'channelSourceDetailCode']
        ).values().join('-')
        def args = [
            requestContentType: URLENC,
            contentType       : HTML,
            path              : _URL_MAINCTRL,
            query             : [
                bsDetailCode: bsDetailCode,
                deptCode    : baseInfo.departmentCode
            ]
        ]

        RESTClient client = context.client
        def mainQuotationNo = client.get args, { resp, html ->
            def m = html =~ /mainQuotationNo = '(.*)';/
            m[0][1]
        }

        if (mainQuotationNo) {
            context.mainQuotationNo = mainQuotationNo
            log.info '询价单号：{}', mainQuotationNo
            getContinueFSRV mainQuotationNo
        } else {
            getFatalErrorFSRV '获取询价单号失败'
        }
    }

}
