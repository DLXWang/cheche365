package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.model.OrderTransmissionStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository
import com.cheche365.cheche.core.service.OrderOperationInfoService
import com.cheche365.cheche.manage.common.service.InternalUserManageService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/9.
 */
@Service
@Slf4j
class GenerateOrderOperationInfo implements  TPlaceInsuranceStep{

    @Override
    @Transactional
    Object run(Object context) {
        log.debug("------生成出单------")
        PurchaseOrder purchaseOrder=context.purchaseOrder
        InternalUserManageService internalUserManageService=context.internalUserManageService
        OrderOperationInfoRepository operationInfoRepository=context.orderOperationInfoRepository
        OrderOperationInfoService operationInfoService=context.orderOperationInfoService
        InternalUser currentUser = internalUserManageService.getCurrentInternalUserOrSystem()

        OrderOperationInfo orderOperationInfo = operationInfoRepository.findFirstByPurchaseOrder(purchaseOrder)

        if (null != orderOperationInfo) {
            orderOperationInfo.setUpdateTime(new Date())
        } else {
            orderOperationInfo = operationInfoService.createOperationInfo(purchaseOrder, orderOperationInfo)
        }
        orderOperationInfo.setCurrentStatus(OrderTransmissionStatus.Enum.ORDER_INPUTED)
        orderOperationInfo.setInsuranceInputter(currentUser)
        orderOperationInfo.setOperator(currentUser)
        orderOperationInfo.setConfirmOrderDate(purchaseOrder.getCreateTime())
        operationInfoRepository.save(orderOperationInfo)
        getContinueFSRV true
    }
}
