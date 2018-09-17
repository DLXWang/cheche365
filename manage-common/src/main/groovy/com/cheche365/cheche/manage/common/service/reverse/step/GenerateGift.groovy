package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.PurchaseOrderGiftService
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend
import com.cheche365.cheche.manage.common.service.gift.OrderCenterGiftService
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class GenerateGift implements TPlaceInsuranceStep{

    @Override
    @Transactional
    Object run(Object context) {
        log.debug("------生成礼品------")

        PurchaseOrder purchaseOrder=context.purchaseOrder
        PurchaseOrderExtend purchaseOrderExtend=new PurchaseOrderExtend()
        BeanUtil.copyPropertiesContain(purchaseOrder,purchaseOrderExtend)
        OrderReverse orderReverse=context.model
        OrderCenterGiftService orderCenterGiftService=context.orderCenterGiftService
        PurchaseOrderGiftService purchaseOrderGiftService=context.purchaseOrderGiftService
        QuoteRecord quoteRecord=context.quoteRecord
        GiftService giftService=context.giftService

        purchaseOrderExtend.setResendGiftList(orderReverse.getResendGiftList())
        purchaseOrderExtend.setCommercialPercent(orderReverse.getCommercialDiscount())
        purchaseOrderExtend.setCompulsoryPercent(orderReverse.getCompulsoryDiscount() as Double)
        purchaseOrderExtend.setPremiumType(PurchaseOrderExtend.PremiumTypeEnum.LIMIT)
        Gift couponGift =orderCenterGiftService.createGift(purchaseOrderExtend,quoteRecord.getId())
        List<Gift> realGifts= orderCenterGiftService.createResendGift(purchaseOrderExtend,quoteRecord.getId())
        List<Gift> couponGifts=couponGift == null? new ArrayList<>() : Arrays.asList(couponGift)
        if(CollectionUtils.isNotEmpty(realGifts) || CollectionUtils.isNotEmpty(couponGifts)){
            giftService.resetOrderGift(purchaseOrder)
        }
        purchaseOrderGiftService.assembleAndSavePurchaseOrderGift(purchaseOrder,couponGifts,realGifts)
        getContinueFSRV true

    }

}
