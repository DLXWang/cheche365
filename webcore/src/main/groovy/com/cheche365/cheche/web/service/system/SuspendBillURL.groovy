package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.util.CacheUtil.doJacksonSerialize

/**
 * Created by zhengwei on 04/04/2018.
 * 停复驶账单活动页
 */

@Service
class SuspendBillURL extends SystemURL {

    @Autowired
    PurchaseOrderRepository poRepo

    String toClientPage(String orderNo) {

        String uuid = super.cacheUuid(doJacksonSerialize(poRepo.findFirstByOrderNo(orderNo)?.applicant))

        formatResponse(
            super.generate(
                [
                    host: mMarketing(),
                    path: 'suspendBill/index.html',
                    qs: [
                            orderNo: orderNo,
                            uuid: uuid
                    ]
                ],
                true
            )
        )
    }

    static String formatResponse(String url) { //加空格是为了解决短信时显示问题
        " $url "
    }

    @Override
    String cacheKeyPrefix() {
        'suspend-bill-'
    }

    @Override
    String desc() {
        '停复驶账单链接生成器'
    }
}
