package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * Created by sufc on 2017/11/22.
 */
@Component
@Slf4j
class GetCaptcha implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.vehicleModelCheck'

    @Override
    run(context) {

        def params = [
            insurePlaceCode      : context.districtCode,
            vehicleLicencePlateNo: context.auto.licensePlateNo,
            vehicleFrameNo       : context.auto.vinNo
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)

        if ('1' == result.checkFlag) {
            log.info '众安验证码已经识别并校验，无需我们自己识别'
            getLoopBreakFSRV result
        } else {
            log.info '众安验证码无法识别并校验'
            context.imageBase64 = result.checkCode
            context.flowid = result.flowid
            context.checkNo = result.checkNo
            getContinueFSRV result
        }
    }
}
