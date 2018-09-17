package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV

/**
 * 发起支付请求，发起请求前请先调用修改数据接口提交人员信息和配送信息。 渠道收银台地址的链接有效时间是 15 分钟
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class Pay extends ABaoXianCommonStep {

    private static final _API_PATH_PAY = '/pay'

    @Override
    run(context) {

        def params = [
            taskId : context.taskId,
            prvId  : context.provider.prvId,
            retUrl : context.additionalParameters.paymentReturnUrl,
        ]

        def result = send context,prefix + _API_PATH_PAY, params

        if (('0' == result.code || '00' == result.respCode) && result.payUrl) {
            context.payUrl = result.payUrl
            context.additionalParameters.payUrl = context.payUrl
            log.info '发起支付请求成功，渠道收银台地址为：{}', result.payUrl
            getContinueFSRV result.payUrl
        } else if (('-1' == result.code || '01' == result.code) && result.msg && result.insureSupplys){
            getNeedSupplementInfoFSRV { result.insureSupplys.itemName.join('，') }
        } else {
            log.error '发起支付请求失败，{}', result.msg
            getFatalErrorFSRV result.msg
        }
    }
}
