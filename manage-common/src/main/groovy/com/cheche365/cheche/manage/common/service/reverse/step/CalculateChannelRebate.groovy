package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RebateChannel
import com.cheche365.cheche.core.service.agent.ChannelRebateService
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
class CalculateChannelRebate implements TPlaceInsuranceStep{

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------上游渠道佣金计算------")
        OrderReverse model=context.model
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder purchaseOrder=context.purchaseOrder
        ChannelRebateService channelRebateService=context.channelRebateService
        InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateManageService=context.insurancePurchaseOrderRebateManageService
        InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel = model.getInsurancePurchaseOrderRebateViewModel()
        ChannelRebate rebate = channelRebateService.getChannelRebate(quoteRecord, purchaseOrder)
        insurancePurchaseOrderRebateViewModel.setUpCommercialRebate(rebate != null ? rebate.getCommercialRebate() : 0)
        insurancePurchaseOrderRebateViewModel.setUpCompulsoryRebate(rebate != null ? rebate.getCompulsoryRebate() : 0)
        insurancePurchaseOrderRebateViewModel.setUpRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_AGENT)
        insurancePurchaseOrderRebateViewModel.setUpChannelId(quoteRecord.getChannel().getId())
        insurancePurchaseOrderRebateViewModel.setCommercialPremium(model.getCommercialPremium())
        insurancePurchaseOrderRebateViewModel.setCompulsoryPremium(model.getCompulsoryPremium())
        insurancePurchaseOrderRebateViewModel.setPurchaseOrderId(purchaseOrder.getId())
        insurancePurchaseOrderRebateManageService.savePurchaseOrderRebate(insurancePurchaseOrderRebateViewModel)
        model.setInsurancePurchaseOrderRebateViewModel(insurancePurchaseOrderRebateViewModel)
        getContinueFSRV true
    }
}
