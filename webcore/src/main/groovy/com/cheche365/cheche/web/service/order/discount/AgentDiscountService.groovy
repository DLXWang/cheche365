package com.cheche365.cheche.web.service.order.discount

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate
import com.cheche365.cheche.core.repository.ChannelAgentPurchaseOrderRebateRepository
import com.cheche365.cheche.core.repository.ChannelAgentRebateRepository
import com.cheche365.cheche.core.repository.ChannelRebateRepository
import com.cheche365.cheche.core.serializer.SerializerUtil
import com.cheche365.cheche.core.service.IResourceService
import com.cheche365.cheche.web.service.ChannelAgentInfoService
import com.cheche365.cheche.web.service.shareInfo.QuoteRecordShareHandler
import com.cheche365.cheche.web.service.system.SystemUrlGenerator
import com.cheche365.cheche.web.util.ChannelAgentUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.DoubleUtils.displayDoubleValue

/**
 * Created by zhengwei on 08/02/2018.
 */

@Service
@Slf4j
class AgentDiscountService {

    @Autowired
    SystemUrlGenerator systemUrlGenerator

    @Autowired
    private IResourceService resourceService

    @Autowired
    private ChannelAgentRebateRepository channelAgentRebateRepository

    @Autowired
    private ChannelRebateRepository channelRebateRepository

    @Autowired
    private ChannelAgentInfoService channelAgentInfoService

    @Autowired
    private QuoteRecordShareHandler quoteRecordShareHandler

    @Autowired
    private ChannelAgentPurchaseOrderRebateRepository channelAgentPurchaseOrderRebateRepository

    QuoteRecord calculateDiscounts(QuoteRecord quoteRecord, ChannelRebate rebate) {
        Double discountAmount = rebate ? rebate.discountAmount(quoteRecord) : 0d
        def quoteRecordRebates = [
            commercialRebate: rebate ? rebate.commercialRebate : 0,
            compulsoryRebate: rebate ? rebate.compulsoryRebate : 0
        ]

        quoteRecord.discounts = assembleDiscount(quoteRecordRebates, discountAmount, quoteRecord)
        quoteRecord
    }

    QuoteRecord calculateDiscountsByChannelAgent(QuoteRecord quoteRecord, ChannelAgent channelAgent) {

        ChannelRebate channelRebate = channelRebateRepository.findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(
            channelAgent?.channel,
            quoteRecord.getArea(),
            quoteRecord.getInsuranceCompany(),
            ChannelRebate.Enum.EFFECTIVED_1
        )

        log.info("代理人报价可持有点位查询,报价id>>>>{},代理人id>>>>{}", quoteRecord.id, channelAgent.id)
        Map quoteRecordRebates = ChannelAgentUtil.calculateRebate(channelRebate, channelAgent, quoteRecord)
        Double totalAmount = displayDoubleValue(quoteRecord.premium * quoteRecordRebates.commercialRebate / 100 + quoteRecord.compulsoryPremium * quoteRecordRebates.compulsoryRebate / 100)

        log.info("报价预算返点金额为>>>>{}", totalAmount)

        quoteRecord.discounts = assembleDiscount(quoteRecordRebates, totalAmount, quoteRecord)
        quoteRecord
    }

    QuoteRecord loadDiscountsByOrder(QuoteRecord quoteRecord, PurchaseOrder order, ChannelAgent channelAgent) {
        ChannelAgentPurchaseOrderRebate channelAgentPurchaseOrderRebate = channelAgentPurchaseOrderRebateRepository.findByChannelAgentAndPurchaseOrder(channelAgent, order)
        def quoteRecordRebates = [
            commercialRebate: channelAgentPurchaseOrderRebate ? channelAgentPurchaseOrderRebate.commercialRebate : 0,
            compulsoryRebate: channelAgentPurchaseOrderRebate ? channelAgentPurchaseOrderRebate.compulsoryRebate : 0
        ]
        def totalAmount = 0
        if (channelAgentPurchaseOrderRebate) {
            if (channelAgentPurchaseOrderRebate.commercialAmount != null) {
                totalAmount = totalAmount + channelAgentPurchaseOrderRebate.commercialAmount
            } else if(quoteRecord.premium != null) {
                log.debug("订单{}未出单完成，使用报价计算商业险返点", order.orderNo)
                totalAmount = totalAmount + quoteRecord.premium * quoteRecordRebates.commercialRebate / 100
            }

            if (channelAgentPurchaseOrderRebate.compulsoryAmount != null) {
                totalAmount = totalAmount + channelAgentPurchaseOrderRebate.compulsoryAmount
            } else if(quoteRecord.compulsoryPremium != null) {
                log.debug("订单{}未出单完成，使用报价计算交强险返点", order.orderNo)
                totalAmount = totalAmount + quoteRecord.compulsoryPremium * quoteRecordRebates.compulsoryRebate / 100
            }
        }
        quoteRecord.discounts = assembleDiscount(quoteRecordRebates, totalAmount, quoteRecord)
        quoteRecord
    }

    private assembleDiscount(quoteRecordRebates, totalAmount, quoteRecord) {
        return [[commercialRebate: quoteRecordRebates.commercialRebate,
                 compulsoryRebate: quoteRecordRebates.compulsoryRebate,
                 amount          : DoubleUtils.displayDoubleValue(totalAmount),
                 quoteDetail     : SerializerUtil.generateQuoteDetail(quoteRecord),
                 shareInfo       : quoteRecordShareHandler.shareInfo(quoteRecord)
                ]]
    }
}
