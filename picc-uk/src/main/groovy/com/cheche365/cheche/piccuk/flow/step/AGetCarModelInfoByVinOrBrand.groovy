package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.changeExhaust
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV


/**
 * 通过品牌型号获取车型信息
 */
@Slf4j
abstract class AGetCarModelInfoByVinOrBrand implements IStep {

    private static final _API_PATH_GET_CAR_MODEL_INFO = '/prpall/vehicle/vehicleQuery.do'

    @Override
    run(context) {
        if (context.carModelMsg.contains('未查到车辆对应的合格证信息')) {
            context.carModelMsg = ''
            return handleGetCarModelFailed(context)
        }

        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CAR_MODEL_INFO,
            body              : getBrandNameParameters(context)
        ]
        !context.options? context.options =[:] :''

        def result = client.post args, { resp, json ->
            if (json.totalRecords > 0) {
                json.data
            }
        }

        if (result) {
            //车架号没查出来 把用户选的车放到  context.autoModelByVIN
            if (context.autoModelByVIN == null) {
                def autoModel = context.additionalParameters?.supplementInfo?.autoModel
                context.autoModelByVIN = autoModel ? autoModel.tokenize('_')[0] : null
            }

            def referToOtherAutoModel = context.additionalParameters.referToOtherAutoModel
            //选车去，并且选的是用户选的那辆车
            if (result && result.totalRecord && referToOtherAutoModel) {
                //选车老流程；不动（查询成功）
                context.vehicleLists = changeExhaust(result)
                handleModelCarList(context, result)
                getSelectedCarModelFSRV context, context.vehicleLists, result, context.options
            } else if (result && result.totalRecord){
                handleModelCarList(context, result)
                getContinueFSRV null
            } else {
                log.warn '查询车型失败'
                getValuableHintsFSRV(context,
                    [
                        _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                        _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
                    ])
            }
        } else {
            handleGetCarModelFailed(context)
        }
    }

    abstract protected handleModelCarList(context, result)

    abstract protected getBrandNameParameters(context)

    abstract protected handleGetCarModelFailed(context)

}
