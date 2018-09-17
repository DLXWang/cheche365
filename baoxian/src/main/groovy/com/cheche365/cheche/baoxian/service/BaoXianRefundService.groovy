package com.cheche365.cheche.baoxian.service

import com.cheche365.cheche.baoxian.model.RefundInfo
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.service.IRefundService
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import static com.cheche365.cheche.baoxian.flow.FlowMappings._FLOW_CATEGORY_REFUNDING_FLOW_MAPPINGS
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.flow.core.util.ServiceUtils.getOperationId



/**
 * 泛华退款服务实现
 */
@Service
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class BaoXianRefundService extends ABaoXianBusinessService implements IRefundService<Void, RefundInfo> {

    @Override
    Void refund(RefundInfo refundInfo) {
        def env = [env: env]
        doBusinessService env, refundInfo, 'refunding', '泛华退款服务'
    }

    @Override
    protected createBusinessSpecialContext(env, requestObj) {
        [
            auto                     : new Auto(),
            insuranceCompany         : requestObj.insuranceCompany,
            area                     : requestObj.area,
            cityRefundingFlowMappings: _FLOW_CATEGORY_REFUNDING_FLOW_MAPPINGS,
        ]
    }

    @Override
    def handleException(context, businessObjects, ex) {
        if (ex instanceof BusinessException) {
            throw ex
        } else {
            log.error "${getOperationId(context)}服务操作非预期异常", ex
            throw new BusinessException(INTERNAL_SERVICE_ERROR, -1, ex.message, ex)
        }
    }

}
