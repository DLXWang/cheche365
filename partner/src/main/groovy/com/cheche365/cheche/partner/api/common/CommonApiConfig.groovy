package com.cheche365.cheche.partner.api.common

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.repository.PartnerOrderRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class CommonApiConfig {

    @Autowired
    PartnerOrderRepository partnerOrderRepository

    /**
     * 车车&第三方订单创建关联关系后保存其他上下文信息
     * 标准接入不需要配置此项
     */
    static doAfterCreatePartnerOrder() {
        [
            (ApiPartner.Enum.XIAOMI_PARTNER_12.code): XIAOMI_OTHER_PROCESS,
        ]
    }

    private static Closure XIAOMI_OTHER_PROCESS = { partnerOrder ->
        if(partnerOrder.purchaseOrder.status==OrderStatus.Enum.PENDING_PAYMENT_1){
            partnerOrder.notifyInfo=partnerOrder.partnerUser?.notifyInfo
            partnerOrderRepository.save(partnerOrder)
        }
    }

}
