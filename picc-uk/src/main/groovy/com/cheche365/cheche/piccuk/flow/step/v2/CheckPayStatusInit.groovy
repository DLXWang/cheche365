package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 初始化支付检查步骤
 */
@Component
@Slf4j
class CheckPayStatusInit implements IStep {

    @Override
    Object run(Object context) {
        log.debug '查询微信支付结果'

        def paymentInfos = context.paymentInfos

        if (!paymentInfos || paymentInfos.size() < 1) {
            log.debug '请求参数为空paymentInfos: {}', paymentInfos
            return getKnownReasonErrorFSRV('请求参数为空')
        }

        def checkIndex = context.checkIndex ?: 0
        log.debug 'total size:{}', paymentInfos.size()
        log.debug 'index num :{}', checkIndex
        if (checkIndex >= paymentInfos.size()) {
            log.debug '查询完成'
            return getLoopBreakFSRV('查询完成')
        }

        context.checkIndex = checkIndex
        def paymentInfo = paymentInfos[checkIndex]
        context.processNo = paymentInfo?.commercial ?: paymentInfo.compulsory
        context.indexPaymentInfo = paymentInfo
        getContinueFSRV '开始查询'
    }
}
