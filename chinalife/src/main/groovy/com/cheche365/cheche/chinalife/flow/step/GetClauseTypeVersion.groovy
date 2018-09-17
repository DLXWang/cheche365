package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.getMapByPath
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取条款版本
 */
@Component
@Slf4j
class GetClauseTypeVersion implements IStep {
    private static final _URL_GET_CLAUSE_TYPE = '/online/saleNewCar/carProposalgetClauseTypeVersion.do'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _URL_GET_CLAUSE_TYPE,
            body              : [
                'temporary.proposalAreaCode'                              : context.deptId,
                'temporary.quoteMain.areaCode'                            : context.deptId,
                'temporary.quoteMain.geQuoteCars[0].ecdemicFlag'          : '0',
                'temporary.quoteMain.geQuoteCars[0].obtainCarModelV2XFlag': context.obtainCarModelV2XFlag,
                'temporary.carVerify.newCarTaxComCode'                    : context.carVerify.newCarTaxComCode,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if('1' == result.resultType){
            context.clauseTypeParams = getMapByPath(
                result.temporary,
                ['temporary'],
                ['quoteMain','geClauseTypeVersion','geItemkindInfoList','kindCode']
            ) + getMapByPath(
                result.temporary,
                ['temporary'],
                ['quoteMain','geClauseTypeVersion','clauseType'])

            log.debug '获取条款版本参数：{}', context.clauseTypeParams

            context.clauseType = context.clauseTypeParams.'temporary.quoteMain.geClauseTypeVersion[0].clauseType'
            getContinueFSRV result
        }else{
            getFatalErrorFSRV '获取条款失败'
        }
    }

}
