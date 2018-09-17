package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.pinganuk.util.BusinessUtils.resolveAutoLicensePlate
import static groovyx.net.http.ContentType.JSON

/**
 * 车型检索
 * Created by wangxiaofei on 2016.9.9
 */
@Component
@Slf4j
class AutoModelCodeQuery implements IStep {

    private static final _API_TO_AUTO_MODEL_CODE_QUERY = '/icore_pnbs/do/app/quotation/autoModelCodeQuery'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_TO_AUTO_MODEL_CODE_QUERY,
            body              : [
                vehicleFrameNo    : context.auto.vinNo,
                departmentCode    : context.baseInfo.departmentCode,
                vehicleLicenceCode: resolveAutoLicensePlate(context.auto.licensePlateNo),
                insuranceType     : '1'
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result.encodeDict) {
            getSelectedCarModelFSRV context, result.encodeDict, result
        } else {
            getFatalErrorFSRV '没有能够查询到车型'
        }
    }

}
