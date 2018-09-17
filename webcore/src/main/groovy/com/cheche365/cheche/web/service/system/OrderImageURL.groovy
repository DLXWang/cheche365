package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.PurchaseOrder
import org.springframework.stereotype.Service


/**
 * Created by zhengwei on 04/04/2018.
 */

@Service
class OrderImageURL extends SystemURL {

    String toServerLink(String orderNo) {

        String uuid = super.cacheUuid(orderNo)

        super.generate(
            [
                host: apiBaseUrl(),
                path: "orders/callback/detail/upload/$orderNo",
                qs: [
                        uuid: uuid
                ]
            ],
            true
        )

    }


    String toClientPage(PurchaseOrder order, String uuid) {
        super.generate(
            [
                path: WebConstants.getRootPath(order.sourceChannel),
                qs: [
                        uuid: uuid,
                        src: order.sourceChannel.isThirdPartnerChannel() ? order.sourceChannel.apiPartner.code : null,
                ],
                fragment: "mine/order/${order.orderNo}/1"
            ]
        )
    }

    @Override
    String cacheKeyPrefix() {
        'upload_image'
    }

    @Override
    String desc() {
        '订单图片上传页链接生成器'
    }
}
