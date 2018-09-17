package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.util.CacheUtil
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

/**
 * Created by zhengwei on 04/04/2018.
 */

@Service
class SelfPaymentURL extends SystemURL {

    String toServerLink(String orderNo) {

        String uuid = super.cacheUuid(orderNo)

        return super.generate(
            [
                host: apiBaseUrl(),
                path: "orders/$orderNo/prepay",
                qs: [
                        uuid: uuid
                ]
            ]
        )
    }

    String toClientPage(PurchaseOrder order, String uuid) {
        super.generate(
            [
                path: WebConstants.getIndexPath(order.sourceChannel),
                qs: [
                    src: 'smspay',
                    uuid: uuid,
                    readonly: order.sourceChannel.parent.isStandardAgent() ? true : null
                ],
                fragment: "pay&${order.orderNo}"
            ]
        )
    }

    @Override
    String cachedValue(String uuid) {  //重写方法只是为了返回String而不是Object，否则java调用处理很麻烦
        return super.cachedValue(uuid)
    }

    @Override
    String cacheKeyPrefix() {
        'SMS_PAYMENT_'
    }

    @Override
    String desc() {
        '车车自有渠道支付页面链接生成器'
    }
}
