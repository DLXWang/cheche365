package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.changeExhaust
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 经过交管平台校验后
 * 再次获取车型信息
 */
@Slf4j
class GetCarModelInfoAgain implements IStep {

    private static final _API_PATH_GET_CAR_MODEL_INFO = '/prpall/vehicle/vehicleCodeQuery.do'

    @Override
    run(context) {
        RESTClient client = context.client

        log.debug '再次获取车型信息'
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CAR_MODEL_INFO,
            query             : [
                vehicleCode: context.vehicleCode,
            ]
        ]

        def result = client.post args, { resp, json ->
            if (json.totalRecords > 0) {
                json.data
            }
        }
        //referToOtherAutoModel == true 和以前一样
        def referToOtherAutoModel = context.additionalParameters.referToOtherAutoModel
        if (result && result.totalRecord && !referToOtherAutoModel) {
            //走到这里都是要调整车型的,
            result = changeExhaust(result)
            adjustCarMessage(context, result)
            getContinueFSRV context
        } else if (result && result.totalRecord) {
            result = changeExhaust(result)
            context.optionsByVinNo = result
            context.optionsByCode = null
            getSelectedCarModelFSRV context, result, result, context.options
        } else {
            log.warn '查询车型失败'
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
                ])
        }
    }
    /**
     * 由于车型不一致调选车前的参数
     * @param context
     */
    private adjustCarMessage(context, result){
        context.autoModel = null
        context.selectedCarModel = null
        context.optionsByVinNo = context.optionsByVinNo.findAll {
            it.vehicleId in result.vehicleId
        }
        context.optionsByCode = context.optionsByCode.findAll {
            it.vehicleId in result.vehicleId
        }
        context.resultByVinNo = context.optionsByVinNo
        context.resultByCode = context.optionsByCode
        context.additionalParameters.optionsSource = context.resultByVinNo ? 'byVinNo' : 'byCode'
        //过滤了两个list
        context.options =  [byCode : context.resultByCode ,  byVinNo: context.resultByVinNo]

    }



}
