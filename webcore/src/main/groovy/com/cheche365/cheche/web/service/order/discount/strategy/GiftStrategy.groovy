package com.cheche365.cheche.web.service.order.discount.strategy

import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftTypeUseType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.UserType
import com.cheche365.cheche.core.service.AgentService
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.PurchaseOrderGiftService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.PaymentChannel.Enum.COUPONS_8

/**
 * Created by mahong on 2015/6/2.
 */
@Service
@Transactional
@Slf4j
@Order(value=5)
class GiftStrategy extends DiscountStrategy {

    @Autowired
    private GiftService giftService;

    @Autowired
    private PurchaseOrderGiftService purchaseOrderGiftService;

    @Autowired
    private  AgentService agentService

    @Override
    def applyDiscountStrategy(QuoteRecord quoteRecord, order, Long giftId) {

        def result = [paymentChannel : COUPONS_8]
        Gift gift
        if (giftId) {
            gift = giftService.useGift(quoteRecord, order, giftId)
        }

        if (GiftTypeUseType.Enum.REDUCE_1 == gift?.getGiftType()?.getUseType()) {
            result.discountAmount = giftService.calculateGiftReduceAmount(gift, quoteRecord)
        }
        if (gift) {
            purchaseOrderGiftService.assembleAndSavePurchaseOrderGift(order, [gift], null);
        }
        result
    }

    @Override
    boolean support(QuoteRecord quoteRecord, purchaseOrder, Long giftId) {

        if(quoteRecord?.applicant){
            User user =quoteRecord.applicant
            Boolean isCustomer=UserType.Enum.isCustomer(user.getUserType())
            Boolean isAgentAndDisabled=UserType.Enum.isAgent(user.getUserType())&&!agentService.checkAgent(user)
            return isCustomer||isAgentAndDisabled
        }
        return false

    }
}
