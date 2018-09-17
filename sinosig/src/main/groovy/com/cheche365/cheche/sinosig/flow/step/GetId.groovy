package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取token
 */
@Component
@Slf4j
class GetId implements IStep {

    private static final _API_PATH_SAVE_BASE_INFO = 'Net/netCarInfoControl!saveBaseInfo.action'

    @Override
    run(context) {

        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_SAVE_BASE_INFO,
            body              : [
                'paraMap.licence'   : context.auto.licensePlateNo,
                'paraMap.contactor' : context.auto.owner,
                'paraMap.mobileNum' : randomMobile,
                'paraMap.idno'      : context.auto.identity,
                'paraMap.orgID'     : context.orgId,
                'paraMap.agentCode' : context.agentCode
            ]
        ]

        def result = client.post args, { resp, json ->
            json.paraMap
        }
        def token = result.id
        log.info 'Token：{}', token
        if (token) {
            context.token = token
            if (result.frameNo) {//续保车辆一定有frameNo字段，非续保车辆，暂时没发现有
                context.selectedCarModel = getVehicleModelByResult result
                log.info "车辆信息：{}",context.selectedCarModel
            }
            getContinueFSRV context.selectedCarModel as boolean
        } else {
            getFatalErrorFSRV '获取Token失败'
        }
    }

    private static getVehicleModelByResult(paraMap) {
        paraMap.standardName = paraMap.queryVehicle
        paraMap.displacement = paraMap.exhaust
        paraMap
    }

}
