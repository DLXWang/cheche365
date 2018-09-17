package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.InstitutionRebateHistory
import com.cheche365.cheche.core.model.OrderOperationInfo
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RebateChannel
import com.cheche365.cheche.core.repository.OrderOperationInfoRepository
import com.cheche365.cheche.core.service.InstitutionRebateHistoryService
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend
import com.cheche365.cheche.manage.common.service.InsurancePurchaseOrderRebateManageService
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import com.cheche365.cheche.manage.common.web.model.InsurancePurchaseOrderRebateViewModel
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class CalculateInstitutionRebate implements TPlaceInsuranceStep{

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------下游佣金计算------")
        OrderReverse model=context.model
        InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateManageService=context.insurancePurchaseOrderRebateManageService
        PurchaseOrder purchaseOrder=context.purchaseOrder
        QuoteRecord quoteRecord=context.quoteRecord
        InstitutionRebateHistoryService institutionRebateHistoryService = context.institutionRebateHistoryService
        OrderOperationInfoRepository orderOperationInfoRepository = context.orderOperationInfoRepository
        OrderOperationInfo orderOperationInfo =orderOperationInfoRepository.findFirstByPurchaseOrder(purchaseOrder)
        InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel = model.getInsurancePurchaseOrderRebateViewModel()
        InstitutionRebateHistory rebate=institutionRebateHistoryService.findByInstitutionAndDateTimeAndAreAndCompany(model.getInstitution(),orderOperationInfo.getConfirmOrderDate(),purchaseOrder.getArea().getId(),quoteRecord.getInsuranceCompany().getId())
        insurancePurchaseOrderRebateViewModel.setDownCommercialRebate(rebate != null?rebate.getCommercialRebate():0)
        insurancePurchaseOrderRebateViewModel.setDownCompulsoryRebate(rebate != null?rebate.getCompulsoryRebate():0)
        insurancePurchaseOrderRebateViewModel.setDownRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_INSTITUTION)
        insurancePurchaseOrderRebateViewModel.setDownChannelId(model.getInstitution())
        insurancePurchaseOrderRebateViewModel.setCommercialPremium(model.getCommercialPremium())
        insurancePurchaseOrderRebateViewModel.setCompulsoryPremium(model.getCompulsoryPremium())
        insurancePurchaseOrderRebateViewModel.setPurchaseOrderId(purchaseOrder.getId())
        model.setInsurancePurchaseOrderRebateViewModel(insurancePurchaseOrderRebateViewModel)
        insurancePurchaseOrderRebateManageService.savePurchaseOrderDownRebate(insurancePurchaseOrderRebateViewModel)
        getContinueFSRV true
    }
}
