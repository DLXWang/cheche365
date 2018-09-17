package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.huanong.util.BusinessUtils.populateQR



/**
 * 更新QR
 */
@Slf4j
class UpDateQuotePriceQR implements IStep {

    @Override
    run(context) {
        populateQR context, context.kindCodeConvertersConfig, context.compulsoryInsurance, context.insurance, context.seatCount, context.sumTax, context.insuranceTotalPremium
        getContinueFSRV true
    }
}
