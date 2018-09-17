package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive
import static com.cheche365.flow.core.util.ServiceUtils.persistState



/**
 * 获取身份验证码
 * create by sufc
 */
@Component
@Slf4j
class GetIdentifyCode implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.getIdentifyCode'

    @Override
    Object run(context) {

        log.info "进入获取身份验证码步骤"
        def params = [
            insureFlowCode: context.insureFlowCode,
            outTradeNo    : context.outTradeNo
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.debug '获取身份验证码result：{}', result

        if ('0' == result.result) {
            log.info '成功获取身份验证码, 应该推送补充信息'
            persistState context
            getSupplementInfoFSRV(
                [
                    mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])
                ])
        } else {
            log.info '获取身份验证码失败'
            getFatalErrorFSRV("调用获取身份验证码接口失败")
        }
    }
}
