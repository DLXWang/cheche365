package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoVinNo
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 *
 * 精友地区根据品牌型号获取车型列表
 */
abstract class AFindCarModelJYQuery implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL_JY_QUERY = '/newecar/car/findCarModelJYQuery'


    @Override
    run(context) {

        RESTClient client = context.client

        Auto auto = context.auto
        def uniqueID = context.uniqueID
        def areaCode = context.areaCode
        def queryCarModelCode = context.auto?.autoType?.code

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_FIND_CAR_MODEL_JY_QUERY,
            body              : [
                'carModelJYQuery.uniqueId'   : uniqueID,
                'carModelJYQuery.areaCode'   : areaCode,
                'carModelJYQuery.vehicleName': queryCarModelCode,
                'carModelJYQuery.pageSize'   : 50
            ]
        ]

        log.info '用如下信息查找车型：{}、{}、{}、{}、{}', auto.licensePlateNo, auto.owner, getAutoVinNo(context), getAutoEngineNo(context), auto.identity

        def result = client.post args, { resp, json ->
            json
        }

        /**
         * 针对两次查车可能需要推两次补充信息的情况，采用autoModel拼接的形式，XX_XX
         * 精友查车只切不拼
         */
        handleResultFSRV(context, result)
    }


    protected abstract handleResultFSRV(context, result)

}
