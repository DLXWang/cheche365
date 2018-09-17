package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.ContentType.JSON


/**
 * 平安新版uk，支付校验
 * @author: lp
 * @date: 2018/4/23 21:45
 */
@Component
@Slf4j
class VerifyPayment implements IStep {

    private static final _API_PATH_VERIFY_PAYMENT = '/icore_pnbs/do/app/collect/paymentCheck'

    @Override
    run(context) {
        RESTClient client = context.client
        def baseInfo = context.baseInfo
        def policyInfo = context.policyInfo

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_VERIFY_PAYMENT,
            body              : [
                billedVoucherArray     :
                    context.applyPolicyNos.values() - null,
                noticesArray           : [
                    policyInfo.noticeNo
                ],
                repealReason           : '手动撤单',
                departmentCode         : baseInfo.departmentCode,
                docType                : '5',
                clientName             : policyInfo.insuredName,  //当事人姓名，故猜测是被保人
                transitSystemSource    : baseInfo.transitSystemSource,
                applyflag              : 2,
                applyPersonnelName     : policyInfo.applicantName,
                noticeQueryConditionDTO: [
                    noticeType   : '01',
                    voucherType  : policyInfo.voucherType,
                    noticeNoStart: policyInfo.noticeNo,
                    noticeNoEnd  : policyInfo.noticeNo,
                    queryDocType : '5'
                ],
                needHintFlag           : '0'
            ]
        ]
        try {
            def result = client.post args, { resp, json ->
                json
            }

            if (result) {
                context.paymentInfo = result
                log.info '平安新版uk支付校验成功：applyPolicyNos：{}，noticeNo：{}', context.applyPolicyNos, result.noticeNo
                getContinueFSRV result
            }
        } catch (Exception e) {
            log.error '平安新版uk支付校验失败：applyPolicyNos：{}，错误原因：{}', context.applyPolicyNos, e
            getValuableHintsFSRV context, [mergeMaps(_VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING,
                [meta: [orderNo: context.order.orderNo]], [originalValue: context.extendedAttributes.verificationCode])]
        }
    }
}
