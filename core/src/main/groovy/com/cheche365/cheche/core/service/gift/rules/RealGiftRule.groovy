package com.cheche365.cheche.core.service.gift.rules

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.ActivityType
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftStatus
import com.cheche365.cheche.core.model.GiftType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RuleConfig
import com.cheche365.cheche.core.model.RuleParam
import com.cheche365.cheche.core.service.gift.ConfigurableRule
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat

import static com.cheche365.cheche.core.model.RuleParam.Enum.SEND_CALCULATE_PKG_LIMIT_RULE_11
import static com.cheche365.cheche.core.model.RuleParam.Enum.SEND_FULL_RULE_7
import static com.cheche365.cheche.core.model.RuleParam.Enum.SEND_IS_REGULAR_RULE_8
import static com.cheche365.cheche.core.model.RuleParam.Enum.SEND_ORDER_PKG_LIMIT_RULE_10
import static com.cheche365.cheche.core.model.RuleParam.Enum.SEND_OTHER_SEND_RULE_12
import static com.cheche365.cheche.core.model.RuleParam.Enum.SEND_TOP_RULE_9


/**
 * Created by mahong on 2016/8/5.
 * 全国通用活动-满送类型活动-优惠策略
 */
@Service("commonMarketingSend")
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Transactional
@Slf4j
@Order(2)
public class RealGiftRule extends ConfigurableRule {

    @Override
    public Map configParams(List<RuleConfig> ruleConfigs){
        [
            fullInsurance: find(ruleConfigs,SEND_CALCULATE_PKG_LIMIT_RULE_11),
            quoteFields : find(ruleConfigs,SEND_ORDER_PKG_LIMIT_RULE_10),
            isAccTotal: find(ruleConfigs,SEND_IS_REGULAR_RULE_8) ,
            sendFull: find(ruleConfigs,SEND_FULL_RULE_7) ,
            topDiscount: find(ruleConfigs,SEND_TOP_RULE_9 ),
            otherAdditionalAndRealGift : find(ruleConfigs,SEND_OTHER_SEND_RULE_12),
        ]
    }
    @Override
    boolean support(ActivityType activityType) {
        return ActivityType.Enum.FULL_SEND_5 == activityType
    }

    @Override
    public List<Gift> generateCouponGifts(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        Map<String,RuleConfig> ruleEqualsMap=configParams(ruleConfigs);
        RuleConfig sendRuleConfig = ruleEqualsMap.get('sendFull');
        if (sendRuleConfig == null) {
            return null;
        }
        RuleConfig isRegularSendRuleConfig =ruleEqualsMap.get('isAccTotal');
        RuleConfig topSendRuleConfig =ruleEqualsMap.get('topDiscount');

        Double sendAmount = 0.0;
        Double fullAmount = 0.0;
        String[] giftData = null;
        Double pkgLimitAmount = calculatePkgLimitAmount(quoteRecord, ruleConfigs);
        Boolean isRegularSend = (isRegularSendRuleConfig != null && Boolean.TRUE.toString() == isRegularSendRuleConfig.getRuleValue());
        if (isRegularSend) {
            String[] sendAmountArray = sendRuleConfig.getRuleValue().split(WebConstants.COMMON_MARKETING_SYMBOL_SPLIT);
            giftData = sendAmountArray[1].split(WebConstants.COMMON_MARKETING_SYMBOL_GIFT_SPLIT);
            sendAmount = Double.valueOf(((int) (pkgLimitAmount / Double.valueOf(sendAmountArray[0]))) * Double.valueOf(giftData[1]));
        } else {
            List<String> sendAmountPairs = Arrays.asList(sendRuleConfig.getRuleValue().split(WebConstants.COMMON_MARKETING_LOGIC_SYMBOL_AND));
            for (String sendAmountPair : sendAmountPairs) {
                String[] sendAmountArray = sendAmountPair.split(WebConstants.COMMON_MARKETING_SYMBOL_SPLIT);
                if (Double.valueOf(sendAmountArray[0]) <= pkgLimitAmount && Double.valueOf(sendAmountArray[0]) > fullAmount) {
                    giftData = sendAmountArray[1].split(WebConstants.COMMON_MARKETING_SYMBOL_GIFT_SPLIT);
                    sendAmount = Double.valueOf(giftData[1]);
                    fullAmount = Double.valueOf(sendAmountArray[0]);
                }
            }
        }
        Double amount = (topSendRuleConfig == null || sendAmount < Double.valueOf(topSendRuleConfig.getRuleValue())) ? DoubleUtils.displayDoubleValue(sendAmount) : DoubleUtils.displayDoubleValue(Double.valueOf(topSendRuleConfig.getRuleValue()));
        List<Gift> couponGifts = new ArrayList<>();
        if (amount > 0) {
            GiftType giftType = giftTypeRepo.findFirstById(Long.valueOf(giftData[0]));
            Gift newGift = getGiftTemplate(quoteRecord, giftType);
            newGift.setStatus(GiftStatus.Enum.USED_3);
            newGift.setGiftDisplay(new DecimalFormat("0.00").format(amount));
            couponGifts.add(newGift);
        }
        return couponGifts;
    }
}
