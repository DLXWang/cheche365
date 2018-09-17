package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.service.AgentRebateHistoryService
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
class CalculateAgentRebate implements TPlaceInsuranceStep {

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------上游代理佣金计算------")
        OrderReverse model = context.model
        AgentRepository agentRepository = context.agentRepository
        AreaRepository areaRepository = context.areaRepository
        AgentRebateHistoryService agentRebateHistoryService = context.agentRebateHistoryService
        InsurancePurchaseOrderRebateManageService insurancePurchaseOrderRebateManageService = context.insurancePurchaseOrderRebateManageService
        PurchaseOrder purchaseOrder = context.purchaseOrder
        InsurancePurchaseOrderRebateViewModel insurancePurchaseOrderRebateViewModel = model.getInsurancePurchaseOrderRebateViewModel()


        Agent agent = agentRepository.findOne(model.getRecommender())
        Area area = areaRepository.findOne(model.getArea())
        Date scopeDate = model.getApplicantDate()

        AgentRebateHistory agentRebateHistory = agentRebateHistoryService.listByAgentAndAreaAndInsuranceCompanyAndDateTime(agent, area, model.getInsuranceCompany(), scopeDate)
        if (agentRebateHistory != null) {
            insurancePurchaseOrderRebateViewModel.setUpCommercialRebate(agentRebateHistory.getCommercialRebate())
            insurancePurchaseOrderRebateViewModel.setUpCompulsoryRebate(agentRebateHistory.getCompulsoryRebate())
        } else {
            insurancePurchaseOrderRebateViewModel.setUpCommercialRebate(agent.getRebate())
            insurancePurchaseOrderRebateViewModel.setUpCompulsoryRebate(agent.getRebate())
        }
        insurancePurchaseOrderRebateViewModel.setUpRebateChannel(RebateChannel.Enum.REBATE_CHANNEL_AGENT)
        insurancePurchaseOrderRebateViewModel.setUpChannelId(agent.getId())
        insurancePurchaseOrderRebateViewModel.setCommercialPremium(model.getCommercialPremium())
        insurancePurchaseOrderRebateViewModel.setCompulsoryPremium(model.getCompulsoryPremium())
        insurancePurchaseOrderRebateViewModel.setPurchaseOrderId(purchaseOrder.getId())
        insurancePurchaseOrderRebateManageService.savePurchaseOrderRebate(insurancePurchaseOrderRebateViewModel)
        model.setInsurancePurchaseOrderRebateViewModel(insurancePurchaseOrderRebateViewModel)
        getContinueFSRV true
    }
}
