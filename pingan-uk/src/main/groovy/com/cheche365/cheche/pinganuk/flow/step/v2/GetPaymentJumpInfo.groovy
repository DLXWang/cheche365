package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 平安新版uk，获取跳转支付页面信息
 * @author: lp
 * @date: 2018/4/23 21:45
 */
@Component
@Slf4j
class GetPaymentJumpInfo implements IStep {

    private static final _API_PATH_GET_PAYMENT_JUMP_INFO = '/icore_pnbs/do/app/collect/ePOSPaymentForNotice'

    @Override
    run(context) {
        RESTClient client = context.client
        def applyPolicyNo = context.applyPolicyNo
        def paymentInfo = context.paymentInfo

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_GET_PAYMENT_JUMP_INFO,
            body              : [
                noticeNo    : paymentInfo.noticeNo,
                businessNo  : paymentInfo.noticeNo,
                amount      : paymentInfo.collectAmount,
                insuredName : paymentInfo.insuredName,
                customerName: paymentInfo.clientName,
                currencyNo  : paymentInfo.currencyCode,
                deptCode    : context.baseInfo.departmentCode,
                userName    : context.baseInfo.umCode
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            context.jumpPaymentPageInfo = result
            log.info '平安新uk获取跳转支付页面成功：businessNo:{}', applyPolicyNo
            getContinueFSRV result
        } else {
            log.error '平安新uk获取跳转支付页面失败：businessNo:{}', applyPolicyNo
            getFatalErrorFSRV '平安新uk获取跳转支付页面失败'
        }

    }
}
