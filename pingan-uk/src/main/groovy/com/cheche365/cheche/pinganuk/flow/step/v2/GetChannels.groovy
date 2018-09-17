package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._PAYMENY_CHANNEL_ONE


/**
 * lp
 * 获取支付方式
 * 平安暂时只有一种支付方式，故暂时写死
 */
@Component
@Slf4j
class GetChannels implements IStep {

    @Override
    run(context) {
        getContinueFSRV context.newPaymentChannels = [
            channelType: _PAYMENY_CHANNEL_ONE,
            channels   : []
        ]
    }

}
