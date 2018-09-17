package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.picc.util.BusinessUtils.isBZSingle

/**
 * 检查险别
 */
@Slf4j
class CheckInsuranceItemSingle implements IStep {

    @Override
    run(context) {

        def isQuotedCommercial = isCommercialQuoted(context.accurateInsurancePackage)
        def isQuotedBZ = context.accurateInsurancePackage.compulsory
        def bzSingleFlag = isBZSingle(context)

        if (!isQuotedCommercial && isQuotedBZ && !bzSingleFlag) {
            log.error '不能单投交强险'
            getFatalErrorFSRV '不能单投交强险'
        } else {
            getContinueFSRV null
        }
    }

}
