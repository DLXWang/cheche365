package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC



/**
 * 进入“产险业务新核心系统”
 */
@Component
@Slf4j
class SystemTransfer implements IStep {

    private static final _URL_QUOTE_ORG_INFO = '/icore_pnbs/do/usermanage/systemTransfer'

    @Override
    run(context) {
        RESTClient client = context.client
        client.uri = getEnvProperty(context, 'pinganuk.pnbs_host')

        def baseInfo = context.baseInfo
        def args = [
            headers           : [
                Referer: 'https://icore-pts.pingan.com.cn/ebusiness/auto/newness/toibcswriter.do'
            ],
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _URL_QUOTE_ORG_INFO,
            body              : baseInfo.subMap([
                'systemId',
                'cypherText',
                'timestamp',
                'umCode',
                'userName',
                'dataSource',
                'saleAgentCode',
                'brokerCode',
                'departmentCode',
                'isUseCyberArk',
                'partnerType',
                'agentCode',
                'partnerWorkNetCode',
                'businessSourceCode',
                'businessSourceDetailCode',
                'channelSourceCode',
                'channelSourceDetailCode',
                'transitSystemSource',
                'saleAgentName',
                'relationQueryFlag'
            ]) + [
                transferId: baseInfo.transferId ?: 'apply'
            ]
        ]

        def result = client.post args, { resp, stream ->
            true
        }

        if (result) {
            log.info '进入产险业务新核心系统成功'
            getContinueFSRV result
        } else {
            getFatalErrorFSRV '进入产险业务新核心系统成功失败'
        }
    }

}
