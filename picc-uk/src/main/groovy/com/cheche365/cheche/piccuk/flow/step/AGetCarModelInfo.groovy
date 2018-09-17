package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.piccuk.util.BusinessUtils.getEnrollDateText
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取车型
 */
@Slf4j
abstract class AGetCarModelInfo implements IStep {

    private static final _API_PATH_GET_CAR_MODEL_INFO = '/prpall/carInf/getCarModelInfo.do'

    @Override
    run(context) {
        RESTClient client = context.client
        def carInfo = context.carInfo
        def auto = context.auto

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_GET_CAR_MODEL_INFO,
            body              : [
                'prpCitemCar.licenseNo'     : auto.licensePlateNo,
                'prpCitemCar.licenseType'   : '02',
                'prpCitemCar.engineNo'      : auto.engineNo ?: carInfo?.engineNo,
                'prpCitemCar.frameNo'       : auto.vinNo ?: carInfo?.rackNo,
                'prpCitemCar.enrollDate'    : getEnrollDateText(context),
                'prpCitemCar.modelCodeAlias': auto.autoType?.code ?: context.carInfo?.modelCode,
                'comCode'                   : context.baseInfo.comCode,
                'prpCitemCar.noNlocalFlag'  : '0',          // 是否外地车
                'prpCitemCar.newCarFlag'    : '0',          // 新旧车标志
            ]
        ]

        def carModelInfo = client.post args, { resp, json ->
            if (json.totalRecords > 0) {
                json.data
            }
        }
        handleCarModelInfoResult(context, carModelInfo)
    }

    abstract protected handleCarModelInfoResult(context, carModelInfo)
}
