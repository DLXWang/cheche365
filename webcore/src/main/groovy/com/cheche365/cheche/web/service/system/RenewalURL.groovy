package com.cheche365.cheche.web.service.system

import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 04/04/2018.
 */

@Service
class RenewalURL extends SystemURL {

    String toServerLink(String orderNo) {

        String uuid = super.cacheUuid(orderNo)

        super.generate(
            [
                host: apiBaseUrl(),
                path: "orders/$orderNo/renewal",
                qs: [
                        uuid: uuid
                ]
            ],
            true
        )
    }

    String toClientPage(String orderNo) {
        super.generate(
            [
                host: mRoot(),
                fragment: "quote/order/$orderNo"

            ]
        )

    }

    @Override
    String cacheKeyPrefix() {
        'renewal_page_url_'
    }


    @Override
    String desc() {
        '一键续保链接生成器'
    }
}
