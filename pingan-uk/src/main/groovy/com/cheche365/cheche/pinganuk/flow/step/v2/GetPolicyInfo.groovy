package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 平安新版uk，获取保单信息
 * @author: lp
 * @date: 2018/4/23 21:45
 */
@Component
@Slf4j
class GetPolicyInfo implements IStep {

    private static final _API_PATH_GET_PAY_NOTICE_ID = '/icore_pnbs/do/app/workbench/qtWaitTaskInfof66a85'

    @Override
    run(context) {
        RESTClient client = context.client
        def applyPolicyNo = context.applyPolicyNos.commercial ?: context.applyPolicyNos.compulsory
        def baseInfo = context.baseInfo

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_GET_PAY_NOTICE_ID,
            body              : [
                applySeletemodel: 0,  // 投保进度查询
                departmentCode  : baseInfo.departmentCode,
                inputById       : baseInfo.umCode,
                lowDepartment   : 'true',// 是否包含下级机构
                userId          : baseInfo.umCode,
                voucherType     : 1, // 单证号类型：投保单号
                voucherNo       : applyPolicyNo
            ]
        ]

        def result = client.post args, { resp, json ->
            json
        }

        if (result) {
            def policyInfo = result.datalist.first()
            context.policyInfo = policyInfo
            log.info '平安新uk获取保单信息成功：applyPolicyNo：{}', applyPolicyNo
            getContinueFSRV result
        } else {
            log.error '平安新uk获取保单信息失败：applyPolicyNo：{}', applyPolicyNo
            getFatalErrorFSRV '平安新uk获取保单信息失败'
        }

    }
}
