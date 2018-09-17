package com.cheche365.cheche.core.service.gift.rules

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.ActivityType
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.model.GiftStatus
import com.cheche365.cheche.core.model.GiftType
import com.cheche365.cheche.core.model.MarketingInsuranceType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RuleConfig
import com.cheche365.cheche.core.service.gift.ConfigurableRule
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.DecimalFormat
import static com.cheche365.cheche.core.model.RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20
import static com.cheche365.cheche.core.model.RuleParam.Enum.DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19
import static com.cheche365.cheche.core.model.RuleParam.Enum.DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22
import static com.cheche365.cheche.core.model.RuleParam.Enum.DISCOUNT_SEND_OTHER_SEND_RULE_24
import static com.cheche365.cheche.core.model.RuleParam.Enum.DISCOUNT_SEND_TOP_PKG_RULE_23

/**
 * Created by wenling on 2017/5/8.
 * 全国通用活动-折扣赠送类型活动-优惠策略
 */
@Service("discountSendRule")
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
@Transactional
@Slf4j
@Order(4)
public class DiscountSendRule extends ConfigurableRule {

    @Override
    public Map<String,RuleConfig> configParams(List<RuleConfig> ruleConfigs){
        [
            fullInsurance: find(ruleConfigs,DISCOUNT_SEND_CALCULATE_PKG_LIMIT_TYPE_RULE_19),
            discountFull: find(ruleConfigs,DISCOUNT_SEND_CALCULATE_PKG_LIMIT_RULE_20) ,
            insurancePercent: find(ruleConfigs,DISCOUNT_SEND_INSURANCE_TYPE_PERCENT_RULE_22) ,
            topDiscount: find(ruleConfigs,DISCOUNT_SEND_TOP_PKG_RULE_23 ),
            otherAdditionalAndRealGift : find(ruleConfigs,DISCOUNT_SEND_OTHER_SEND_RULE_24),
        ]
    }

    @Override
    boolean support(ActivityType activityType) {
        return ActivityType.Enum.DISCOUNT_SEND_7 == activityType
    }

    public List<Gift> generateCouponGifts(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {
        Double amount = calculateReduceAmount(quoteRecord, ruleConfigs);
        List<Gift> couponGifts = new ArrayList<>();
        if (amount > 0) {
            couponGifts.add(this.getCouponGift(quoteRecord, amount));
        }
        return couponGifts;
    }

    public Gift getCouponGift(QuoteRecord quoteRecord, Double amount) {
        Gift newGift = getGiftTemplate(quoteRecord, GiftType.Enum.CASH_37);
        newGift.setStatus(GiftStatus.Enum.USED_3);
        newGift.setGiftAmount(0.0);
        newGift.setGiftDisplay(new DecimalFormat("0.00").format(amount));
        return newGift;
    }
    int conditions(){
        CONDITION_NONE
    }

    private Double calculateReduceAmount(QuoteRecord quoteRecord, List<RuleConfig> ruleConfigs) {

        Map<String,RuleConfig> ruleEqualsMap=configParams(ruleConfigs);
        RuleConfig discountSendCalculatePkgLimitConfig =ruleEqualsMap.get('discountFull');
        RuleConfig percentConfig = ruleEqualsMap.get('insurancePercent');
        RuleConfig discountSendTopPkgConfig =ruleEqualsMap.get('topDiscount');

        //满额条件
        if(null !=ruleEqualsMap.get('quoteFields')  && null != discountSendCalculatePkgLimitConfig){
            if( super.calculatePkgLimitAmount(quoteRecord,ruleConfigs)< Double.valueOf(discountSendCalculatePkgLimitConfig.getRuleValue()) ){
                return  DoubleUtils.displayDoubleValue(0);  //不满足优惠条件
            }
        }

        Double discountSendCalculateAmount = 0.0;
        //赠送
        if(null != percentConfig){
            percentConfig.getRuleValue().split(WebConstants.COMMON_MARKETING_LOGIC_SYMBOL_AND)
                .collect {it.split(WebConstants.COMMON_MARKETING_SYMBOL_SPLIT)}
                .findAll {it.size() == 2 && it[0] && it[1]}
                .each { percents ->
                if( percents[0] == String.valueOf(MarketingInsuranceType.Enum.COMPULSORY_1.id)){  //交强险
                    discountSendCalculateAmount+=quoteRecord.getCompulsoryPremium() .multiply(Double.valueOf(percents[1]).div(100d)) ;
                }
                else if(percents[0] == String.valueOf(MarketingInsuranceType.Enum.COMMERCIAL_2.id)){//商业险
                    discountSendCalculateAmount+=quoteRecord.getPremium().multiply( Double.valueOf(percents[1]).div(100d));
                }
                else{//车船税
                    discountSendCalculateAmount+=quoteRecord.getAutoTax().multiply( Double.valueOf(percents[1]).div(100d));
                }
            }
        }
        //最高满额赠送条件
        return (discountSendTopPkgConfig == null || discountSendCalculateAmount < Double.valueOf(discountSendTopPkgConfig.getRuleValue())) ? DoubleUtils.displayDoubleValue(discountSendCalculateAmount) : DoubleUtils.displayDoubleValue(Double.valueOf(discountSendTopPkgConfig.getRuleValue()));
    }

}
