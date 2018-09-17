package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取Unique ID：
 * 先获取http://www.epicc.com.cn/ecar/indextb.jsp?areaCode=110000&citySelected=110100，
 * 然后获取#UniqueId的text。
 */
@Component
@Slf4j
class GetUniqueId implements IStep {

    private static final _URL_PROPOSAL = '/ecar/proposal/normalProposal'
    private static final _NORMAL_PROPOSAL_NAMES = [
        'uniqueID',
        'renewalFlag',
        'sFlag', //再次投保的标志
        'newCarEnableFlag_stepone',
        'priceConfigKind',
        // 以下三个是从CityCodeParamsMappings中移除的
        'comCode',
        'handlerCode',
        'handlercode_uni',
        // 北京地区在计算续保全险套餐时需要
        'RunMiles'
    ]


    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        RESTClient client = context.client

        def args = createRequestParams context
        def result = client.post args, { resp, stream ->
            def inputs = htmlParser.parse(stream).depthFirst().INPUT

            inputs.findResults { input ->
                if (input.@id in _NORMAL_PROPOSAL_NAMES) {
                    [(input.@id): input.@value]
                }
            }.sum()
        }

        if (result.uniqueID) {
            log.info 'UniqueID：{}', result.uniqueID
            log.info '续保标志renewalFlag：{}', result.renewalFlag
            context << result

            getContinueFSRV result.uniqueID
        } else {
            log.error '尝试5次获取UniqueID失败，通常是HTML网页获取有误导致的，建议重试'
            getFatalErrorFSRV '连接失败，建议重试'
        }

    }

    private createRequestParams(context) {
        [
            requestContentType  : URLENC,
            contentType         : BINARY,
            path                : _URL_PROPOSAL,
            body                : generateRequestParameters(context, this)
        ]
    }

}
