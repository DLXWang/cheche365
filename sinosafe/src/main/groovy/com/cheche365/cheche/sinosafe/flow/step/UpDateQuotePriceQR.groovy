package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.populateQR



/**
 * 更新QR
 */
@Slf4j
class UpDateQuotePriceQR implements IStep {

    @Override
    run(context) {
        populateQR context
        getContinueFSRV true
    }
}
