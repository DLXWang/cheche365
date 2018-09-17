package com.cheche365.cheche.core.service.gift.rules

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.GiftCodeRepository
import com.cheche365.cheche.core.repository.MarketingSuccessRepository
import org.apache.commons.lang.math.NumberUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

import static com.cheche365.cheche.core.model.GiftArea.Enum.containsArea
import static com.cheche365.cheche.core.model.GiftChannel.Enum.containsChannel
import static com.cheche365.cheche.core.model.GiftInsuranceCompany.Enum.containsCompany
import static com.cheche365.cheche.common.util.DoubleUtils.displayDoubleValue
import static com.cheche365.cheche.core.model.InsuranceCompany.apiQuoteCompanies

@Component('defaultGiftRule')
public class DefaultGiftRule {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MarketingSuccessRepository marketingSuccessRepository;

    @Autowired
    GiftCodeRepository giftCodeRepository;

    public boolean check(Gift gift, QuoteRecord quoteRecord) {
        if (GiftType.Enum.TEL_MARKETING_DISCOUNT_27 == (gift.getGiftType()) || gift.giftType.useType == GiftTypeUseType.Enum.GIVENAFTERORDER_3) {
            return true
        }

        Double payableAmount = gift.commercialOnly() ? displayDoubleValue(quoteRecord.premium) : quoteRecord.calculateRebateablePremium()
        Boolean pkgLimit = gift.commercialOnly() ? (quoteRecord.premium > 0) : (quoteRecord.premium && quoteRecord.compulsoryPremium)

        Long sourceId = this.getActivityId(gift)
        return validateDate(gift) && validateFullReduce(gift, payableAmount) &&
            containsCompany(gift.sourceType, sourceId, quoteRecord.getInsuranceCompany()) &&
            containsChannel(gift.sourceType, sourceId, quoteRecord.getChannel()) &&
            containsArea(gift.sourceType, sourceId, quoteRecord.getArea()) &&
            validateApiQuote(gift, quoteRecord) &&
            pkgLimit
    }

    public void beforePlaceOrder(Gift gift, QuoteRecord quoteRecord){
        if (GiftType.Enum.COUPON_3 == gift.getGiftType()) {
            Double payableAmount = displayDoubleValue(quoteRecord.getTotalPremium() - DoubleUtils.doubleValue(quoteRecord.getAutoTax()));
            gift.setGiftAmount(payableAmount < gift.getGiftAmount() ? payableAmount : gift.getGiftAmount());
        }
    }

    public void processAfterReleaseGift(Gift gift){
        if (GiftType.Enum.COUPON_3 == gift.getGiftType() && gift.getGiftDisplay() && NumberUtils.isNumber(gift.getGiftDisplay())) {
            gift.setGiftAmount(NumberUtils.toDouble(gift.getGiftDisplay()));
        }
    }

    private static boolean validateDate(Gift gift) {
        Date now = Calendar.getInstance().getTime();
        return (now.after(gift.getEffectiveDate()) && now.before(DateUtils.addDays(gift.getExpireDate(), 1)));
    }

    private static boolean validateFullReduce(Gift gift, Double payableAmount) {
        return null==gift.getFullLimitAmount() || payableAmount >= gift.getFullLimitAmount();
    }

    private static boolean validateApiQuote(Gift gift, QuoteRecord quoteRecord) {
        (GiftTypeUseType.Enum.REDUCE_1 != gift.giftType?.useType) ||
            (!quoteRecord.apiQuote() && !apiQuoteCompanies().contains(quoteRecord.insuranceCompany))
    }

    boolean checkGiftChannel(Gift gift, Channel channel) {
        if (GiftType.Enum.TEL_MARKETING_DISCOUNT_27 == gift.getGiftType() && Channel.orderCenterChannels().contains(channel)) {
            return true;
        }
        return containsChannel(gift.sourceType, this.getActivityId(gift), channel);
    }

    private Long getActivityId(Gift gift) {
        if (SourceType.Enum.WECHATRED_2 == gift.getSourceType()) {
           return marketingSuccessRepository.findOne(gift.source)?.marketing?.id
        } else if (SourceType.Enum.GIFT_CODE_4 == gift.getSourceType()) {
            return giftCodeRepository.findOne(gift.source)?.exchangeWay?.id
        }
        return null;
    }


}
