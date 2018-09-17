package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_AUTO_TYPE_CODE_ERROR
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.flow.core.util.FlowUtils.getAutoTypeCodeErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 根据VINNO和 licensePlateNo （車牌）查车型
 */
@Component
@Slf4j
class QueryCarModelByVINNO implements IStep {

    private static final _API_PATH_QUERY_CAR_MODEL_BY_VINNO = '/ecar/quickoffer/queryVehicleInfoByVinOther'

    @Override
    run(context) {
        RESTClient client = context.client
        def result = getResult context, client
        def allCarModel = result.result.models
        def referToOtherAutoModel = context.additionalParameters.referToOtherAutoModel
        //有车老流程: 选车
        if (allCarModel && referToOtherAutoModel) {
            log.debug '查询车型成功，车型信息 ：{}', allCarModel
            getSelectedCarModelFSRV context, allCarModel, result
        } else if ((allCarModel || context.optionsByCode) && !referToOtherAutoModel) {
            //有车新流程 ：走下一步自动选车
            context.optionsByVinNo = allCarModel
            context.options = [byCode : context.optionsByCode,
                               byVinNo: context.optionsByVinNo]
            getContinueFSRV context
        } else if (!referToOtherAutoModel) {
            //新流程没有车 出异常
            getAutoTypeCodeErrorFSRV result, _ERROR_MESSAGE_AUTO_TYPE_CODE_ERROR
        } else {
            log.debug '查询车型失败'
            getKnownReasonErrorFSRV '查询车型失败'
        }
    }

    private static getResult(context, client) {
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_CAR_MODEL_BY_VINNO,
            body              : [
                meta  : [:],
                redata: [
                    searchFlag       : '0',
                    plateNo          : context.auto.licensePlateNo,  //车牌
                    carVIN           : context.auto.vinNo,  //车架
                    moldCharacterCode: '',
                ]
            ]
        ]
        client.post args, { resp, json ->
            json
        }
    }


}
