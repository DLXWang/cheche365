package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftStatus
import com.cheche365.cheche.core.model.GiftType
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.SourceType
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.marketing.service.MarketingService
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service

@Service
class Service201808002 extends MarketingService{


    @Override
    void preCheck(Marketing marketing, String mobile, Channel channel) {
        List<MarketingSuccess> msList = marketingSuccessRepository.findByMarketing(marketing)
        if (msList?.size() >= 1000){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "优惠券已领完")
        }
        super.preCheck(marketing, mobile, channel)
    }

    @Override
    protected Object doAfterAttend(MarketingSuccess ms, User user, Map<String, Object> payload) {
        Gift gift = new Gift()
        Calendar calendar = Calendar.getInstance()
        Double giftAmount = 300d
        gift.setCreateTime(calendar.getTime())
        gift.setEffectiveDate(DateUtils.truncate(gift.getCreateTime(), Calendar.DAY_OF_MONTH))
        gift.setExpireDate(DateUtils.truncate(ms.marketing.endDate, Calendar.DAY_OF_MONTH))
        gift.setGiftAmount(giftAmount)
        gift.setGiftDisplay(DoubleUtils.formatStripTrailingZeros(giftAmount))
        gift.setGiftType(GiftType.Enum.GIFT_CARD_4)
        gift.setReason("货拉拉优惠券活动")
        gift.setSourceType(SourceType.Enum.PURCHASE_ORDER_1)
        gift.setApplicant(user)
        gift.setStatus(GiftStatus.Enum.CREATED_1)
        gift.setQuantity(1)
        gift.setUnit("张")
        gift.setUsageRuleParam("commercial")
        gift.setFullLimitAmount(1000d)
        gift.setUsageRuleDescription("商业险满1000元使用")

        giftRepository.save(gift)

        return super.doAfterAttend(ms, user, payload)
    }
}
