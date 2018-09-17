package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV



/**
 * 验证码录入校验接口
 * Created by LIU GUO on 2018/6/7.
 */
@Slf4j
class IssueCodeCheck implements IStep {

    @Override
    run(Object context) {
        log.info '开始执行验证码校验接口'
        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode
        if (verificationCode) {
            log.info '验证码已录入：{}', verificationCode
            getContinueFSRV '验证码已录入'
        } else {
            log.error '需身份验证码核实信息'
            getSupplementInfoFSRV(
                [mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])]
            )
        }
    }

}
