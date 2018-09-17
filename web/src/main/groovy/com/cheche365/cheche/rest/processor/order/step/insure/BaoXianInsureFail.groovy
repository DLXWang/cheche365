package com.cheche365.cheche.rest.processor.order.step.insure

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.rest.processor.order.step.TPlaceOrderStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by zhengwei on 20/03/2018.
 */

@Component
@Slf4j
class BaoXianInsureFail implements TPlaceOrderStep {


    @Override
    Object run(Object context) {

        Exception e = context.insureFailException
        PurchaseOrder order = context.order

        if(e.getCode().codeValue == 5001){
            context.additionalParameters << [doInuranceMessage : '努力核保中，大约10分钟后查看结果']
        } else {
            context.additionalParameters << [doInuranceMessage : '努力核保中，大约10分钟后，请点击"继续核保"或进入订单详情页点击"继续核保"']
            context.additionalParameters  << [reInsure: true]
        }

        if(e.message?.contains('您的订单需要人工处理')){
            order.statusDisplay = null
        } else if (e.getCode().codeValue != BusinessException.Code.QUOTE_NEED_SUPPLY_INFO.codeValue) {
            order.statusDisplay = '核保中'
        }

        log.info("baoxian taskId : ${context.additionalParameters?.persistentState?.taskId} ,insure exception code is : ${e.getCode().codeValue}, hint :${ context.additionalParameters?.doInuranceMessage}")

        return getContinueFSRV(true)

    }
}
