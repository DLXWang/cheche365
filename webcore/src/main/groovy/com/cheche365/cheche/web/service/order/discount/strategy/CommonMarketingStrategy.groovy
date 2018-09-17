package com.cheche365.cheche.web.service.order.discount.strategy

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.BusinessActivityRepository
import com.cheche365.cheche.core.repository.MarketingRuleRepository
import com.cheche365.cheche.core.service.GiftService
import com.cheche365.cheche.core.service.gift.ConfigurableRule
import com.cheche365.cheche.core.service.gift.RuleFactory
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.PaymentChannel.Enum.COUPONS_8

/**
 * Created by mahong on 2016/8/5.
 * 全国通用活动-优惠策略
 */
@Service
@Transactional
@Slf4j
@Order(value=4)
public class CommonMarketingStrategy extends DiscountStrategy {


    @Autowired
    protected GiftService giftService;

    @Autowired
    protected MarketingRuleRepository marketingRuleRepository;

    @Autowired
    protected RuleFactory ruleFactory;

    @Autowired
    private BusinessActivityRepository activityRepository;

    @Override
    def applyDiscountStrategy(QuoteRecord quoteRecord, order, Long giftId) {

        def result = [paymentChannel : COUPONS_8]
        MarketingRule marketingRule = marketingRuleRepository.findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(quoteRecord.getArea(), quoteRecord.getChannel(), quoteRecord.getInsuranceCompany(), MarketingRuleStatus.Enum.EFFECTIVE_2);
        if (marketingRule == null) {
            log.error("根据报价ID:{} 未找到生效中活动", quoteRecord.getId());
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "未找到生效中活动");
        }

        ConfigurableRule commonMarketingRuleDealClass = ruleFactory.findRuleService(marketingRule.getActivityType().getId());
        commonMarketingRuleDealClass.saveCommonMarketingDiscount(quoteRecord, order);

        List<Gift> couponGifts = this.giftService.getGiftByOrder(order);
        Boolean genReducePayment = (couponGifts != null && !couponGifts.isEmpty() && GiftTypeUseType.Enum.REDUCE_1.getId().equals(couponGifts.get(0).getGiftType().getUseType().getId()));
            if (genReducePayment) {
                Double reduceAmount = couponGifts.get(0).getGiftAmount();
                if (reduceAmount > 0) {
                    result.discountAmount = reduceAmount
                }
        }
        result
    }

    @Override
    boolean support(QuoteRecord quoteRecord, order, Long giftId) {
        return Marketing.isCommonMarketing(giftId, order.getApplicant())
    }

}
