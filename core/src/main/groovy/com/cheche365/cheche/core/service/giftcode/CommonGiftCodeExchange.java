package com.cheche365.cheche.core.service.giftcode;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.service.giftcode.convertor.FullLimitAmountConvertor;
import com.cheche365.cheche.core.service.giftcode.convertor.GiftAmountConvertor;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by mahong on 2015/6/26.
 */
public class CommonGiftCodeExchange implements GiftCodeExchange {
    @Override
    public List<Gift> exchangeGiftCode(GiftCode giftCode, User user) {
        List<Gift> gifts = new ArrayList<>();

        List<Double> amountList = GiftAmountConvertor.getGiftAmounts(giftCode.getExchangeWay().getAmountParam());
        Map<Double, Double> fullLimitMap = FullLimitAmountConvertor.getGiftFullLimits(giftCode.getExchangeWay().getFullLimitParam());

        if (amountList == null) {
            return gifts;
        }

        for (Double amount : amountList) {
            Gift gift = new Gift();

            Calendar calendar = Calendar.getInstance();
            gift.setCreateTime(calendar.getTime());

            if (giftCode.getExchangeWay().getEffectiveDate() == null) {
                gift.setEffectiveDate(DateUtils.truncate(gift.getCreateTime(), Calendar.DAY_OF_MONTH));
            } else {
                gift.setEffectiveDate(giftCode.getExchangeWay().getEffectiveDate());
            }

            if (giftCode.getExchangeWay().getExpireDate() == null) {
                calendar.add(Calendar.YEAR, 1);
                gift.setExpireDate(DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH));
            } else {
                gift.setExpireDate(giftCode.getExchangeWay().getExpireDate());
            }

            gift.setGiftAmount(amount);
            gift.setGiftDisplay(DoubleUtils.formatStripTrailingZeros(amount));
            gift.setGiftType(GiftType.Enum.GIFT_CARD_4);
            gift.setReason(giftCode.getExchangeWay().getName());
            gift.setSourceType(SourceType.Enum.GIFT_CODE_4);
            gift.setSource(giftCode.getId());
            gift.setApplicant(user);
            gift.setStatus(GiftStatus.Enum.CREATED_1);
            gift.setQuantity(1);
            gift.setUnit("张");
            gift.setUsageRuleParam(giftCode.getExchangeWay().getRuleParam());

            if (fullLimitMap != null && fullLimitMap.get(amount) != null) {
                gift.setFullLimitAmount(fullLimitMap.get(amount));
                gift.setUsageRuleDescription(gift.usageRuleDescPrefix() + "满" + fullLimitMap.get(amount).intValue() + "元使用");
            }
            gifts.add(gift);
        }
        return gifts;
    }
}
