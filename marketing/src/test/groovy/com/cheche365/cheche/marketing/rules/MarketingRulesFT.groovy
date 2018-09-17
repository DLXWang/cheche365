package com.cheche365.cheche.marketing.rules

import com.cheche365.cheche.core.model.ActivityType
import com.cheche365.cheche.core.model.MarketingRule
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.RuleConfig
import com.cheche365.cheche.core.model.RuleParam
import com.cheche365.cheche.core.repository.MarketingRuleRepository
import com.cheche365.cheche.core.repository.RuleConfigRepository
import com.cheche365.cheche.core.service.gift.RuleFactory
import com.cheche365.cheche.core.service.gift.rules.DiscountRule
import com.cheche365.cheche.core.service.gift.rules.DiscountSendRule
import com.cheche365.cheche.core.service.gift.rules.FreeQuoteFieldRule
import com.cheche365.cheche.core.service.gift.rules.RealGiftRule
import com.cheche365.cheche.marketing.service.activity.Service201608002
import spock.lang.Specification

/**
 * Created by zhengwei on 5/15/17.
 */
class MarketingRulesFT extends Specification {

    def "满减规则测试"() {
        given:
        def mrRepo = Stub(MarketingRuleRepository){
            findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(_,_,_,_) >> new MarketingRule(activityType: ActivityType.Enum.FULL_REDUCE_4)
        }

        def discountRule = new DiscountRule(null, null, null, null, null,null)
        def ruleFactory = new RuleFactory([discountRule])

        def rcRepo = Stub(RuleConfigRepository){
            findByMarketingRule(_) >> rules
        }

        def service = new Service201608002(mrRepo, rcRepo, ruleFactory)

        expect:
        service.attend(null, null, null, qr)[0].giftAmount == expected


        where:
        rules                                                                                  |  qr                               | expected
        [ new RuleConfig(ruleParam: new RuleParam(id: 1l), ruleValue: '400.0_100.0')]       | new QuoteRecord(premium: 4000l)   | 100d


    }





    static ruleParamList=[new RuleConfig(ruleParam: new RuleParam(id: 19), ruleValue: '1'),new RuleConfig(ruleParam: new RuleParam(id: 20), ruleValue: '1000'),
                          new RuleConfig(ruleParam: new RuleParam(id: 22), ruleValue: '1_10&&3_20'), new RuleConfig(ruleParam: new RuleParam(id: 23), ruleValue: '500')]
    def "折扣赠送测试"() {
        given:
        def mrRepo = Stub(MarketingRuleRepository){
            findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(_,_,_,_) >> new MarketingRule(activityType: ActivityType.Enum.DISCOUNT_SEND_7)
        }

        def discountSendRule = new DiscountSendRule(null, null, null, null, null,null)
        def ruleFactory = new RuleFactory([discountSendRule])

        def rcRepo = Stub(RuleConfigRepository){
            findByMarketingRule(_) >> rules
        }

        def service = new Service201608002(mrRepo, rcRepo, ruleFactory)

        expect:
        service.attend(null, null, null, qr)[0].giftDisplay == expected

        where:
        rules                                         |  qr                                         | expected
        ruleParamList                               | new QuoteRecord(compulsoryPremium: 2000l)   | '200.00'
    }






    static  discountSendRuleParamList=[new RuleConfig(ruleParam: new RuleParam(id:7), ruleValue: '1000.0_31|400.0'),new RuleConfig(ruleParam: new RuleParam(id:8), ruleValue: 'true'),
                                       new RuleConfig(ruleParam: new RuleParam(id:9), ruleValue: '300'),new RuleConfig(ruleParam: new RuleParam(id:10), ruleValue: '2'),
                                       new RuleConfig(ruleParam: new RuleParam(id:11), ruleValue: '2'), new RuleConfig(ruleParam: new RuleParam(id:12), ruleValue: '2000.0_30|100.0')]
    def "满送规则测试"() {
        given:
        def mrRepo = Stub(MarketingRuleRepository){
            findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(_,_,_,_) >> new MarketingRule(activityType: ActivityType.Enum.FULL_SEND_5)
        }

        def realGift = new RealGiftRule(null, null, null, null, null,null)
        def ruleFactory = new RuleFactory([realGift])

        def rcRepo = Stub(RuleConfigRepository){
            findByMarketingRule(_) >> rules
        }

        def service = new Service201608002(mrRepo, rcRepo, ruleFactory)

        expect:
        service.attend(null, null, null, qr)[0].giftDisplay == expected   //礼品展示金额

        where:
        rules                               |  qr                                 | expected
        discountSendRuleParamList       | new QuoteRecord(premium: 4000l)     | '300.00'
    }







    static  freeRuleParamList=[new RuleConfig(ruleParam: new RuleParam(id:13), ruleValue: '2'),new RuleConfig(ruleParam: new RuleParam(id:14), ruleValue: 'false'),
                               new RuleConfig(ruleParam: new RuleParam(id:15), ruleValue: '10'),new RuleConfig(ruleParam: new RuleParam(id:16), ruleValue: '2'),
                               new RuleConfig(ruleParam: new RuleParam(id:17), ruleValue: '2')/*,new RuleConfig(ruleParam: new RuleParam(id:18), ruleValue: '1000.0_5|100.0')*/]
    def "抵扣规则测试"() {
        given:
        def mrRepo = Stub(MarketingRuleRepository){
            findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(_,_,_,_) >> new MarketingRule(activityType: ActivityType.Enum.INSURANCE_PACKAGE_DEDUCT_6)
        }

        def freeQuotefiled = new FreeQuoteFieldRule(null, null, null, null, null,null)
        def ruleFactory = new RuleFactory([freeQuotefiled])

        def rcRepo = Stub(RuleConfigRepository){
            findByMarketingRule(_) >> rules
        }

        def service = new Service201608002(mrRepo, rcRepo, ruleFactory)

        expect:
        service.attend(null, null, null, qr)[0].giftDisplay == expected

        where:
        rules                      |  qr                                 | expected
        freeRuleParamList       | new QuoteRecord(premium: 4000l)     | '400.00'
    }

}
