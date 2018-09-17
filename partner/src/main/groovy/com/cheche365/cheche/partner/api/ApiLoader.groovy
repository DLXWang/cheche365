package com.cheche365.cheche.partner.api

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.partner.api.common.CommonApi
import com.cheche365.cheche.partner.api.common.CommonAgent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 5/12/16.
 */

@Service
class ApiLoader {

    @Autowired
    List<PartnerApi> apis

    @Autowired
    List<SyncOrderApi> syncOrderApis

    @Autowired
    List<SyncAgentApi> syncAgentApis

    PartnerApi findApi(ApiPartner partner) {
        apis.find { partner && it.apiPartner() == partner } ?: apis.find { it instanceof CommonApi }
    }

    SyncAgentApi findAgentApi(ApiPartner partner) {
        syncAgentApis.find { partner && it.apiPartner() == partner } ?:
            syncAgentApis.find { it instanceof CommonAgent }
    }

    def findApi(ApiPartner partner, PartnerOrder partnerOrder) {
        partner && partner.needSyncOrder() ?
            (syncOrderApis.findAll { partner == it.apiPartner() } ?: syncOrderApis.findAll { it instanceof CommonApi }).find {
                it.supportOrderStatus().contains partnerOrder.purchaseOrder.status
            } : null
    }

    SyncOrderApi findFinishedApi(ApiPartner partner) {
        syncOrderApis.reverse().find {
            partner.needSyncOrder() && partner == it.apiPartner(new PartnerOrder(ApiPartner: partner)) && it.supportOrderStatus().contains(OrderStatus.Enum.FINISHED_5)
        }
    }
}
