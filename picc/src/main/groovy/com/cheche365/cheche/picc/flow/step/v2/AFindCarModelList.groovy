package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取车型列表01(北京)
 */
@Component
@Slf4j
abstract class AFindCarModelList implements IStep {

    private static final _API_PATH_FIND_CAR_MODEL = '/newecar/car/findCarModel'

    @Override
    run(context) {
        RESTClient client = context.client

        Auto auto = context.auto
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_FIND_CAR_MODEL,
            body              : getBodyRequestParameters(context)
        ]

        log.info '用如下信息查找车型：{}、{}、{}、{}、{}', auto.licensePlateNo, auto.owner, auto.vinNo, auto.engineNo, auto.identity

        def result = client.post args, { resp, json ->
            json
        }

        context.vehicleListInfo = result.body
        if (context.vehicleListInfo?.carModels) {
            getSelectedCarModelFSRV context, context.vehicleListInfo?.carModels, result, [updateContext: { ctx, res, fsrv ->
                ctx.carInfo = fsrv[2]
            }]
        } else {
            log.warn '无法获得车型，通常是车辆/人员信息有误导致的：{}', result
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
                ])
        }
    }

    /**
     * 获取请求参数body部分
     */
    abstract protected getBodyRequestParameters(context)
}
