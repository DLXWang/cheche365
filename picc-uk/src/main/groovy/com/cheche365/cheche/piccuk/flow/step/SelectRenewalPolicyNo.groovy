package com.cheche365.cheche.piccuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 检查是否续保
 */
@Slf4j
class SelectRenewalPolicyNo implements IStep {

    private static final _API_PATH_SELECT_RENEWAL = '/prpall/business/selectRenewalPolicyNo.do'

    @Override
    run(context) {

        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_SELECT_RENEWAL,
            body              : [
                'licenseType': '02',
                'licenseFlag': '1',
                'licenseNo'  : context.auto.licensePlateNo,
            ]
        ]

        def result = client.post args, { resp, json ->
            if (json.totalRecords) {
                json.data[0]
            }
        }
        //续保标志  新版本续保查车下一版本添加
        context.renewable = false
        //过户车标识
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate
        if (result) {
            //过户车不走续保
            if (result.renewalFlag in ['1', '2'] && !transferFlag && result.policyNo) {
                context.bizNo = result.policyNo
                context.renewable = true
                log.info '是否可以续保：{}，保单号：{}', context.renewable, result.policyNo
                getContinueFSRV '续保'
            } else {
                log.info '是否可以续保：{}', context.renewable
                getContinueFSRV context.renewable
            }
        } else {
            getContinueWithIgnorableErrorFSRV true, '检查是否续保异常'
        }

    }

}
