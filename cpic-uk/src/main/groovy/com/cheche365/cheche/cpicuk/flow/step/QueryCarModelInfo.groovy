package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static groovyx.net.http.ContentType.JSON



/**
 *
 * 根据品牌型号查车型
 */
@Component
@Slf4j
class QueryCarModelInfo implements IStep {

    private static final _API_PATH_QUERY_CAR_MODEL = '/ecar/ecar/queryCarModel'

    @Override
    run(context) {
        RESTClient client = context.client
        def pageNo = 1
        def result = getResult(context, pageNo, client)
        def allCarModel = []
        while (result.result) {
            allCarModel += result.result
            pageNo++
            result = getResult(context, pageNo, client)
        }

        //有车并且老流程 ： 选车
        if (allCarModel && context.additionalParameters.referToOtherAutoModel) {
            log.debug '查询车型成功，车型信息 ：{}', allCarModel
            getSelectedCarModelFSRV context, allCarModel, result
        } else if (!context.additionalParameters.referToOtherAutoModel){
            //新流程 ：有没有车都往下走
            log.debug '查询车型成功，车型信息 ：{}', allCarModel
            context.optionsByCode = allCarModel
            getContinueFSRV context
        } else if(!allCarModel){
            //老流程没车 ：车架号查询
            getContinueFSRV '根据车牌和车架号查车型'
        } else{
            log.debug '查询车型失败'
            getFatalErrorFSRV '查询车型失败'
        }

    }


    private static getResult(context, pageNo, client) {
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_CAR_MODEL,
            body              : [
                meta  : [pageNo: pageNo],
                redata: [
                    name: context.auto.autoType?.code - '牌'
                ],
            ]
        ]
        client.post args, { resp, json ->
            json
        }
    }


}
