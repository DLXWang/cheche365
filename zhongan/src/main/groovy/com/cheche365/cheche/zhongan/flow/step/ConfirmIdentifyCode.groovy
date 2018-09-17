package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * 身份验证码回填
 * create by sufc
 */
@Component
@Slf4j
class ConfirmIdentifyCode implements IStep {


    private static final _SERVICE_NAME = 'zhongan.castle.policy.confirmIdentifyCode'

    @Override
    Object run(context) {

        if (!context.verificationCode) {

            return getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        }

        log.info "进入身份验证码回填步骤,验证码为 : {}", context?.verificationCode
        def params = [
            insureFlowCode: context.insureFlowCode,
            identifyCode  : context?.verificationCode
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.debug '身份验证码回填result : {}', result

        if ('0' == result.result) {
            log.info '身份验证码回填正确，流程完成'
            getContinueFSRV false
        } else {
            log.info '身份验证码回填失败,发起身份验证码获取请求'
            getContinueFSRV true
        }
    }
}
