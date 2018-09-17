//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.cheche365.cheche.manage.common.service.gift

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.model.GiftType.Enum
import com.cheche365.cheche.core.repository.GiftRepository
import com.cheche365.cheche.core.repository.GiftTypeRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import com.cheche365.cheche.core.util.BigDecimalUtil
import com.cheche365.cheche.manage.common.model.PurchaseOrderExtend
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.manage.common.model.PurchaseOrderExtend.PremiumTypeEnum.PERCENT

@Service
class OrderCenterGiftService {
    private Logger logger = LoggerFactory.getLogger(OrderCenterGiftService.class)
    @Autowired
    private QuoteRecordRepository quoteRecordRepository
    @Autowired
    private GiftRepository giftRepository
    @Autowired
    private GiftTypeRepository giftTypeRepository

    OrderCenterGiftService() {
    }

    @Transactional
    void doTelMarketingGift(PurchaseOrderExtend purchaseOrder, Long quoteId) {
        Gift gift = this.createGift(purchaseOrder, quoteId)
        if (gift != null) {
            purchaseOrder.setGiftId(gift.getId())
        }

    }

    Gift createGift(PurchaseOrderExtend purchaseOrder, Long quoteId) {
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(quoteId)
        double giftAmount = 0.0D
        Gift gift = new Gift()
        if (purchaseOrder.getCommercialPercent()) {
            def commercialPercent
            if (purchaseOrder.getPremiumType() == PERCENT) {
                giftAmount += quoteRecord.getPremium() * purchaseOrder.getCommercialPercent() / 100
                commercialPercent = purchaseOrder.getCommercialPercent() / 100
            } else {
                giftAmount += purchaseOrder.getCommercialPercent()
                commercialPercent = purchaseOrder.getCommercialPercent() / quoteRecord.getPremium()
            }
            gift.setCommercialPercent(commercialPercent)
        }

        if (purchaseOrder.getCompulsoryPercent()) {
            def compulsoryPercent
            if (purchaseOrder.getPremiumType() == PERCENT) {
                giftAmount += quoteRecord.getCompulsoryPremium() * purchaseOrder.getCompulsoryPercent() / 100
                compulsoryPercent = purchaseOrder.getCompulsoryPercent() / 100
            } else {
                giftAmount += purchaseOrder.getCompulsoryPercent()
                compulsoryPercent = purchaseOrder.getCompulsoryPercent() / quoteRecord.getCompulsoryPremium()
            }
            gift.setCompulsoryPercent(compulsoryPercent)
        }

        if (giftAmount > 0.0D) {
            giftAmount = DoubleUtils.displayDoubleValue(giftAmount)
            Calendar calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            gift.setGiftAmount(giftAmount)
            gift.setGiftDisplay(BigDecimalUtil.bigDecimalValue(giftAmount, 2).toString())
            gift.setGiftType(Enum.TEL_MARKETING_DISCOUNT_27)
            gift.setEffectiveDate(calendar.getTime())
            calendar.add(Calendar.MONTH, 1)
            gift.setExpireDate(calendar.getTime())
            gift.setStatus(GiftStatus.Enum.CREATED_1)
            gift.setApplicant(quoteRecord.getApplicant())
            gift.setQuantity(1)
            gift.setUnit("张")
            gift.setSourceType(SourceType.Enum.PURCHASE_ORDER_1)
            gift.setCreateTime(new Date())
            gift.setReason("电销自营活动")
            giftRepository.save(gift)
            def premiumTypeStr = purchaseOrder.getPremiumType() == PERCENT ? "%" : "元"
            logger.debug("quoteId->{}存在电销自营优惠commercialPercent {}、compulsoryPercent {}，生成giftId->{}，电销自营活动{}元",
                    quoteId, purchaseOrder.getCommercialPercent() + premiumTypeStr, purchaseOrder.getCompulsoryPercent() + premiumTypeStr, gift.getId(), gift.giftDisplay)
            return gift
        } else {
            return null
        }
    }

    @Transactional
    void doTelMarketingResendGift(PurchaseOrderExtend purchaseOrder, Long quoteId) {
        List<Gift> giftList = this.createResendGift(purchaseOrder, quoteId)
        if (CollectionUtils.isNotEmpty(giftList)) {
            logger.debug("参加活动礼品信息为 ->{}", purchaseOrder.getGiftId())
            List<Long> list = purchaseOrder.getGiftId() != null ? new ArrayList(Arrays.asList(Long.parseLong(purchaseOrder.getGiftId().toString()))) : new ArrayList()
            logger.debug("参加活动礼品信息为 ->{}", purchaseOrder.getGiftId())
            // list.addAll(giftList.stream().map(Gift::getId).collect(Collectors.toList())) 
            for (Gift gift : giftList) {
                list.add(gift.getId())
            }
            purchaseOrder.setGiftId(list)
        }
    }

    List<Gift> createResendGift(PurchaseOrderExtend purchaseOrder, Long quoteId) {
        List<Map<String, String>> resendGiftList = purchaseOrder.getResendGiftList()
        List<Gift> giftList = new ArrayList()
        if (CollectionUtils.isNotEmpty(resendGiftList)) {
            QuoteRecord quoteRecord = (QuoteRecord) this.quoteRecordRepository.findOne(quoteId)
            Iterator var6 = resendGiftList.iterator()

            while (var6.hasNext()) {
                Map<String, String> resendGift = (Map) var6.next()
                GiftType type = (GiftType) giftTypeRepository.findOne(Long.valueOf(Long.parseLong((String) resendGift.get("type"))))
                Integer giftAmountOrQuantity = Integer.valueOf(Integer.parseInt((String) resendGift.get("amount")))
                String unit = (String) resendGift.get("unit")
                Calendar calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -1)
                Gift gift = new Gift()
                if ("元" == unit) {
                    gift.setGiftDisplay(String.valueOf(giftAmountOrQuantity))
                } else {
                    gift.setUnit(unit)
                    gift.setQuantity(giftAmountOrQuantity)
                }
                gift.setGiftType(type)
                gift.setEffectiveDate(calendar.getTime())
                calendar.add(Calendar.MONTH, 1)
                gift.setExpireDate(calendar.getTime())
                gift.setStatus(GiftStatus.Enum.CREATED_1)
                gift.setApplicant(quoteRecord.getApplicant())
                gift.setSourceType(SourceType.Enum.PURCHASE_ORDER_1)
                gift.setCreateTime(new Date())
                gift.setReason("电销额外赠送礼品")
                this.giftRepository.save(gift)
                this.logger.debug("quoteId->{}，生成giftId->{}，额外赠送礼品{}{}", quoteId, gift.getId(), giftAmountOrQuantity, unit)
                giftList.add(gift)
            }
        }

        return giftList
    }
}
