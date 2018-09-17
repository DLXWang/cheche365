package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.model.PartnerUser
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import com.cheche365.cheche.core.repository.PartnerUserRepository
import com.cheche365.cheche.partner.api.common.CommonApiConfig
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_PARTNER_STATE

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class HandleApiPartner implements TPlaceOrderStep {

    @Override
    def run(Object context) {

        PurchaseOrder order = context.order

        if (order.getSourceChannel().isThirdPartnerChannel()) {
            PartnerOrderRepository partnerOrderRepository = context.partnerOrderRepository
            PartnerUserRepository partnerUserRepository = context.partnerUserRepository

            PartnerOrder partnerOrder = partnerOrderRepository.findFirstByPurchaseOrderId(order.getId());
            if (!partnerOrder) {

                PartnerUser partnerUser = partnerUserRepository.findFirstByPartnerAndUser(order.getSourceChannel().getApiPartner(), order.getApplicant())

                partnerOrder = new PartnerOrder(
                    purchaseOrder: order,
                    apiPartner: order.getSourceChannel().getApiPartner(),
                    createTime: Calendar.getInstance().getTime(),
                    updateTime: Calendar.getInstance().getTime(),
                    partnerUser: partnerUser,
                    state: partnerUser?.state ?: context.request.session.getAttribute(SESSION_KEY_PARTNER_STATE)?.toString()

                )
                CommonApiConfig.doAfterCreatePartnerOrder().get(order.sourceChannel.apiPartner.code)?.call(partnerOrder)
                partnerOrderRepository.save(partnerOrder)
            }
        }

        getContinueFSRV true
    }

}
