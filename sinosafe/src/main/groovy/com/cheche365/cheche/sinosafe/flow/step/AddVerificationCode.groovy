package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV

/**
 * 补充短信验证码
 */
@Slf4j
class AddVerificationCode implements IStep {

    @Override
    run(context) {
        if (!context.verificationCode) {
            getSupplementInfoFSRV(
                [mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        } else {
            getContinueFSRV('短信验证码录入成功')
        }
    }
}
