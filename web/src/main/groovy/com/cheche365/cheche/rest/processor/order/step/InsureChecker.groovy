package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.model.QuoteSource.Enum.REFERENCED_7
import static com.cheche365.cheche.core.model.QuoteSource.Enum.RULEENGINE2_8

/**
 * Created by zhengwei on 12/01/2018.
 */

@Component
@Slf4j
class InsureChecker implements TPlaceOrderStep {

    @Override
    Object run(Object context) {

        PurchaseOrder order = context.order
        QuoteRecord quoteRecord = context.quoteRecord

        if (RULEENGINE2_8 == quoteRecord.type || REFERENCED_7 == quoteRecord.type) {
            log.info("报价{}, 类型为 :{},跳过核保", quoteRecord.id, quoteRecord.type.id)
            return getContinueFSRV(false)
        }
        if (order.sourceChannel.isOrderCenterChannel() && order.skipInsure) {
            log.debug("报价{}, 出单中心相关渠道, 跳过核保", quoteRecord.getId());
            return getContinueFSRV(false)
        }
        return getContinueFSRV(true)
    }
}
