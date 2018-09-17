package com.cheche365.cheche.manage.common.service.gift

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.manage.common.app.config.ManageCommonConfig
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by yinJianBin on 2018/7/25.
 */
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(classes = [CoreConfig, ManageCommonConfig])
class OrderCenterGiftServiceTest {

    @Autowired
    OrderCenterGiftService orderCenterGiftService

    @Test
    void "testCreateGift"() {
        def quoteRecordIds = [15057L, 16565L, 16566L, 14773L, 15056L, 209661L]
        def purchaseOrder = new PurchaseOrderExtend(
                compulsoryPercent: 12,
                commercialPercent: 12,
                premiumType: PurchaseOrderExtend.PremiumTypeEnum.PERCENT
        )
        def purchaseOrder2 = new PurchaseOrderExtend(
                compulsoryPercent: 123,
                commercialPercent: 67,
                premiumType: PurchaseOrderExtend.PremiumTypeEnum.LIMIT
        )


        quoteRecordIds.eachWithIndex { it, index ->
            def gift = orderCenterGiftService.createGift(index % 2 == 0 ? purchaseOrder : purchaseOrder2, it)
        }

    }
}
