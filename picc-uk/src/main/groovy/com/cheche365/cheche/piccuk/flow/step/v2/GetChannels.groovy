package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.piccuk.flow.Constants._PAYMENT_CHANNELS_MAPPINGS



/**
 * 获取支付方式
 * 暂时只支持太平洋的划卡支付方式
 */
@Component
@Slf4j
class GetChannels implements IStep {

    @Override
    run(context) {
        context.newPaymentChannels = [
            channels: getObjectByCityCode(context.additionalParameters.quoteRecord?.area, _PAYMENT_CHANNELS_MAPPINGS)
        ]
        log.debug "获取支付方式为：{}", context.newPaymentChannels.channels
        getContinueFSRV context.newPaymentChannels
    }
}
