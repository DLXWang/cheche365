package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.PurchaseOrder
import org.springframework.stereotype.Service

@Service
class PartnerOrderDetailURL extends  SystemURL {

    String toServerLink(PurchaseOrder order){

        String uuid = super.cacheUuid(order.orderNo)
        ApiPartner apiPartner = ApiPartner.toApiPartner(order.sourceChannel)

        super.generate(
            [
                    host: partner(),
                    path: "${apiPartner.code}/callback/order/detail/${order.orderNo}",
                    qs: [
                            uuid: uuid
                    ]
            ]
        )
    }

    String toClientPage(ApiPartner partner, PurchaseOrder order, String uuid, String param) {
        super.generate(
            [
                path: WebConstants.getRootPath(order.sourceChannel.parent),
                qs  : [
                    src     : partner.code,
                    uuid    : uuid,
                    param   : param ? URLEncoder.encode(param, "UTF-8") : null,
                    readonly: order.sourceChannel.parent.isStandardAgent() ? true : null
                ],
                fragment: "mine/order/${order.orderNo}"
            ]
        )
    }





    @Override
    String cacheKeyPrefix() {
        'ORDER_DETAIL_'
    }

    @Override
    String desc() {
        '对接渠道订单详情页面链接生成器'
    }
}
