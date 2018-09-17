package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 核保基类
 * Created by wangmz on 2016-09-9.
 */
@Component
@Slf4j
class ApplyPolicy implements IStep {

    private static final _API_PATH_APPLY_POLICY = '/icore_pnbs/do/app/apply/applyPolicy'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            headers           : [
                dataSource: context.baseInfo?.systemId,
                userId    : context.baseInfo?.umCode

            ],
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_APPLY_POLICY,
            body              : new JsonBuilder(generateRequestParameters(context, this)).toString()
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            def applyPolicyList = result.applyResultList.first().singleDocumentResultList.first()
            //B4 代表核保状态为待修改
            if ('applyEnd' == result.finalResultCode &&
                'B4' == applyPolicyList?.finalStatus) {
                context.applyPolicyList = applyPolicyList
                log.error '核保失败：{}', applyPolicyList.message
                context.lastDoneFSRV = getKnownReasonErrorFSRV applyPolicyList.message
                getContinueFSRV '其他状态'
            } else if ('auditMaterlalError' == result.finalResultCode) {
                //TODO： 目前观察发现pinganUK官网无‘事中审核’状态了，如能找到请备注，处理事中审核状态下删除保单号问题
                def errorMsg = '您还需上传以下资料： 行驶证 （需要事中审核）'
                log.error '核保失败：{}', errorMsg
                context.lastDoneFSRV = getKnownReasonErrorFSRV errorMsg
                getContinueFSRV '其他状态'
            } else if ('applyEnd' == result.finalResultCode) {
                context.applyPolicyList = applyPolicyList
                log.info '核保成功; 核保单号为： {}', context.applyPolicyList.applyPolicyNo
                getContinueFSRV 'B2' == applyPolicyList.finalStatus ? '代缴费状态' : '其他状态'
            } else {
                log.error '核保失败：{}', result
                context.lastDoneFSRV = getFatalErrorFSRV result
                getContinueFSRV '其他状态'
            }
        } else {
            log.error '核保非预期异常:{}', result
            context.lastDoneFSRV = getFatalErrorFSRV result
            getContinueFSRV '其他状态'
        }
    }

}
