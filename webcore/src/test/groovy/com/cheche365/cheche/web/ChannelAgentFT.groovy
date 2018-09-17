package com.cheche365.cheche.web

import com.cheche365.cheche.core.constants.RebateCalculateConstants
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.agent.ChannelAgentRebate
import com.cheche365.cheche.core.repository.ChannelAgentRebateRepository
import com.cheche365.cheche.core.repository.ChannelRebateRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import com.cheche365.cheche.web.util.ChannelAgentUtil
import spock.lang.Unroll

/**
 * Author:   shanxf
 * Date:     2018/5/23 15:41
 */
class ChannelAgentFT extends ChannelAgentSpecification {



    @Unroll
    def "channel agent total income"() {

        given:
        def channelAgentOrderRebates = [
            [
                commercialAmount: commercialAmount_1,
                compulsoryAmount: compulsoryAmount_1
            ],
            [
                commercialAmount: commercialAmount_2,
                compulsoryAmount: compulsoryAmount_2
            ]
        ]

        expect:
        ChannelAgentUtil.totalIncome(channelAgentOrderRebates) == result

        where:
        commercialAmount_1 | compulsoryAmount_1 | commercialAmount_2 | compulsoryAmount_2 | result
        5                  | 0                  | 0                  | 5                  | "10.00"
        0                  | 0                  | 0                  | 0                  | "0.00"
    }

    def "channel agent total order"() {
        given:
        def channelAgentOrderRebates = initTestData()

        expect:
        def resultMap = ChannelAgentUtil.totalOrder(channelAgentOrderRebates)
        resultMap.order.weekOrder == weekOrder
        resultMap.order.monthOrder == monthOrder
        resultMap.order.yearOrder == yearOrder
        resultMap.premium.weekPremium == weekPremium
        resultMap.premium.monthPremium == monthPremium
        resultMap.premium.yearPremium == yearPremium

        where:
        weekOrder|monthOrder|yearOrder|weekPremium|monthPremium|yearPremium
        1        |1         |2        |"50.00"     |"50.00"     |"100.00"
    }

    def initTestData() {
        return [
                    [
                        purchaseOrder: [
                            updateTime: SDF.parse("2018-01-01"),
                            paidAmount: 50d
                        ]
                    ],
                    [
                        purchaseOrder: [
                            updateTime: SDF.parse("2018-06-01"),
                            paidAmount: 50d
                        ]
                    ]
               ]
    }

    @Unroll
    def "channel agent calculate rebate"() {
        setup:
        Map map = RebateCalculateConstants.REBATE_CALCULATE.commercialRebate
        ChannelRebate channelRebate = new ChannelRebate().with {
            it.commercialRebate = commercialRebate
            it
        }
        channelAgentInfoService.channelAgentRepository = Stub(ChannelAgentRepository) {
            findOne(2) >> (channelAgent_2)
            findOne(3) >> (channelAgent_3)
        }
        channelAgentInfoService.channelAgentRebateRepository = Stub(ChannelAgentRebateRepository) {
            findByAreaAndInsuranceCompanyAndChannelAgent(_, _, (channelAgent_2)) >> new ChannelAgentRebate().with {
                it.parentDetainCommercialRebate = one_level_detain_rebate
                it
            }
            findByAreaAndInsuranceCompanyAndChannelAgent(_, _, (channelAgent_3)) >> new ChannelAgentRebate().with {
                it.parentDetainCommercialRebate = two_level_detain_rebate
                it
            }
        }

        expect:
        childRebate_1 == channelAgentInfoService.actualRebate(channelRebate, channelAgent_1 ,map)
        childRebate_2 == channelAgentInfoService.actualRebate(channelRebate, channelAgent_2 ,map)
        childRebate_3 == channelAgentInfoService.actualRebate(channelRebate, channelAgent_3 ,map)

        where:
        commercialRebate | one_level_detain_rebate | two_level_detain_rebate | childRebate_1 | childRebate_2 | childRebate_3
        50d              | 10d                     | 5d                      | 50.0          | 40.0          | 35.0
       // 0                | 10d                     | 5d                      | 0.0           | 0.0           | 0.0
        //50d              | 0                       | 5d                      | 50.0          | 50.0          | 45.0
       // 50d              | 60d                     | 5d                      | 50.0          | 0             | 0
        //50d              | 10d                     | 50d                     | 50.0          | 40.0          | 0

    }

    def "channel agent order rebate record"() {
        setup:
        InsurancePurchaseOrderRebateService ipors = new InsurancePurchaseOrderRebateService()
        ipors.channelAgentOrderRebateRepository = Mock(ChannelAgentOrderRebateRepository)
        ipors.channelRebateRepository = Stub(ChannelRebateRepository) {
            findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(_, _, _, _) >> new ChannelRebate().with {
                it.commercialRebate = 50d
                it.compulsoryRebate = 50d
                it
            }
        }
        when:
        ipors.fillCapor(channelAgent_3, new QuoteRecord(channel: [id: 1],premium:11,compulsoryPremium:22), new PurchaseOrder())

        then:
        3 * ipors.channelAgentOrderRebateRepository.save(_)

    }

}
