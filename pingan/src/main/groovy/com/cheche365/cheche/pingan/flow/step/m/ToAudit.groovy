package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static java.lang.System.currentTimeMillis as ts

/**
 * 查询商业险交强险审计结果
 * @author wangxiaofei on 2016.8.24
 */
@Component
@Slf4j
class ToAudit implements IStep {

    private static final _API_PATH_TO_AUDIT = 'autox/do/api/to-audit'

    @Override
    run(context) {
        def client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_TO_AUDIT,
            query             : [
                _: ts()
            ],
            body              : [
                flowId                   : context.flowId,
                'forceInfo.isApplyForce' : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage),
                responseProtocol         : 'json',
                'bizInfo.isApplyBiz'     : isCommercialQuoted(context.accurateInsurancePackage),
                'bizInfo.isNeedRuleCheck': context.bizQuoteArgs.'bziInfo.isNeedRuleCheck',
                __xrc                    : context.__xrc,
            ]
        ]

        def result = client.post args, { resp, json ->
           json
        }

        if ('C0000' == result.resultCode) {
            if ('C0000' == result.auditInfo.resultCode) {
                log.info '核保通过'
                getContinueFSRV result
            } else if ('C6001' == result.auditInfo.resultCode) {
                getKnownReasonErrorFSRV '商交险核保失败,需要调整套餐或其他原因'
            } else {
                log.error '未知类型的业务错误：{}', result
                getFatalErrorFSRV '未知类型的业务错误导致的核保失败'
            }
        } else {
            log.error '其他错误：{}', result
            getFatalErrorFSRV '其他错误导致的核保失败'
        }
    }
}
