package com.cheche365.cheche.misc.controller

import com.cheche365.cheche.core.repository.PurchaseOrderSummaryRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

/**
 * purchase_order_summary
 */
@Controller
@Slf4j
class PosDataExporter extends PoFakeDataExporter {

    static final _ID = 'pos'

    @Autowired
    private PurchaseOrderSummaryRepository purchaseOrderSummaryRepo

    @Override
    getOrderList() {
        purchaseOrderSummaryRepo.findAllValidData()
    }

    @Override
    getOrderInfo() {
        def c = {  orderInfo ->
            [
                randomTime(orderInfo.orderDate),
                orderInfo.licensePlateNo,
                orderInfo.user,
                orderInfo.premium,
                orderInfo.compulsoryPremium,
                orderInfo.autoTax,
                orderInfo.icShortName,
                rChannel() ?: orderInfo.channel ,
                orderInfo.bank,
                orderInfo.coupon,
                null,
                null,
                null
            ]
        }
    }

    @Override
    checkId(id) {
        _ID == id
    }

    // 世纪通宝和人人优品  2017 04月
    private rChannel() {
        '自有'

    }

}
