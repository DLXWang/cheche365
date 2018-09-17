package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.URLENC

/**
 * 确认车型信息
 */
@Component
@Slf4j
class SelectCarModel implements IStep {

    private static final _API_PATH_CHOOSE_CAR_MODE = 'Net/netCarInfoControl!nCarInfo_selectCar.action'

    @Override
    run(context) {
        def selectCar = context.selectCar
        def newCarString = [
            selectCar.vehicleFgwCode,
            selectCar.brandName,
            selectCar.seat,
            selectCar.displacement,
            selectCar.remark,
            selectCar.price ?: context.selectedCarModel.price
        ].join('#')

        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : ANY,
            path              : _API_PATH_CHOOSE_CAR_MODE,
            body              : [
                'paraMap.id'       : context.token,
                'paraMap.agentCode': context.agentCode,
                'paraMap.licence'  : context.auto.licensePlateNo,
                'paraMap.orgID'    : context.orgId,
                'paraMap.rbCode'   : selectCar.vehicleFgwCode,
                'paraMap.newCar'   : newCarString,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if ('1' == result.paraMap.suc) {
            log.info '确认车型成功:{}', result
            getContinueFSRV result
        } else {
            def message = result.paraMap?.result ?: result.paraMap?.message
            log.error '确认车型失败：{}', message
            getFatalErrorFSRV message
        }
    }

}
