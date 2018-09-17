package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.baoxian.flow.step.AUpdateQuoteInfo
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV

/**
 * 修改数据接口：传配送信息
 * @author wangxin
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class UpdateQuoteInfoForDeliveryBeforePayments extends AUpdateQuoteInfo {

    @Override
    protected getParams(context) {
        def deliveryAddress = context.deliveryAddress
        [
            taskId     : context.taskId,
            prvId      : context.provider.prvId,
            delivery     : [
                name        : deliveryAddress?.name,
                phone       : deliveryAddress?.mobile,
                province    : deliveryAddress?.province,
                city        : deliveryAddress?.city,
                area        : deliveryAddress?.district,
                //和泛华确认过，address只用到街道的名称，在泛华的订单里会自动的拼写省市的名称
                address     : deliveryAddress?.street,
                deliveryType: '1'
            ]
        ]
    }

    @Override
    protected getResultFSRV(result,context) {
        if ('00' == result.respCode) {
            log.info '支付前提交配送地址成功'
            getContinueFSRV result
        } else {
            log.error '支付前提交配送地址失败：{}', result.erroeMsg
            getKnownReasonErrorFSRV result.erroeMsg
        }
    }
}
