package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.getStandardHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * Created by sufc on 2017/11/22.
 */
@Component
@Slf4j
class VehicleConfirm implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.vehicleModelConfirm'

    @Override
    def run(context) {

        def params = [
            flowid   : context.flowid,
            checkNo  : context.checkNo,
            checkCode: context.captchaText
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        if ('0' == result.result) {
            log.debug '江苏地区车型确认：result = {}', result
            getLoopBreakFSRV result
        } else if ("A12477" == result.result) {
            log.info "识别验证码失败"
            getLoopContinueFSRV(true, '江苏地区车型确认重试')
        } else {
            getStandardHintsFSRV result
        }
    }
}
