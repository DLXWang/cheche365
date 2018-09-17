package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取保单号
 */
@Slf4j
class SelectRenewal implements IStep {

    private static final _API_PATH_SELECT_RENEWAL = '/prpall/business/selectRenewal.do'

    @Override
    run(context) {

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_SELECT_RENEWAL,
            body              : [
                'prpCrenewalVo.licenseType': '02',
                'prpCrenewalVo.licenseNo'  : context.auto.licensePlateNo,
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.totalRecords) {
            def biPolicies = result.data.findAll {
                it.riskCode in ['DAA', 'DAT']
            }
            if (biPolicies) {
                def biLatestPolicy = biPolicies.sort {
                    it.policyNo
                }.last()
                context.bizNo = biLatestPolicy.policyNo
                log.info '续保车辆，商业险保单号：{}', biLatestPolicy.policyNo
                getContinueFSRV biLatestPolicy
            } else {
                def ciLatestPolicy = result.data.sort {
                    it.policyNo
                }.last()
                context.bizNo = ciLatestPolicy.policyNo
                log.info '续保车辆，交强险保单号：{}', ciLatestPolicy.policyNo
                getContinueFSRV ciLatestPolicy
            }

        } else {
            getFatalErrorFSRV '检查续保单号异常'
        }
    }

}
