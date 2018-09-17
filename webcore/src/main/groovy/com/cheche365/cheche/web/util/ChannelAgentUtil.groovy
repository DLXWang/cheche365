package com.cheche365.cheche.web.util

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.constants.RebateCalculateConstants
import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate
import com.cheche365.cheche.core.util.CalendarUtil
import groovy.util.logging.Slf4j

import java.text.DecimalFormat
import static com.cheche365.cheche.core.constants.RebateCalculateConstants.CalculateType

/**
 * to-a 相关统计计算
 * Author:   shanxf
 * Date:     2018/8/8 11:39
 */
@Slf4j
class ChannelAgentUtil {

    private static final DecimalFormat DF = new DecimalFormat("0.00")

    static totalIncome(List<ChannelAgentPurchaseOrderRebate> rebateOrders) {

        return DF.format(
            rebateOrders.inject(0d) {
                totalIncome, it ->
                    totalIncome + (it.commercialAmount ?: 0d) + (it.compulsoryAmount ?: 0d)
            }
        )
    }

    static totalOrder(List<ChannelAgentPurchaseOrderRebate> allOrders) {
        List<ChannelAgentPurchaseOrderRebate> yearOrders = filterOrdersByDate(allOrders, Calendar.YEAR)
        List<ChannelAgentPurchaseOrderRebate> monthOrders = filterOrdersByDate(yearOrders, Calendar.MONTH)
        List<ChannelAgentPurchaseOrderRebate> weekOrders = filterOrdersByDate(monthOrders, Calendar.WEEK_OF_MONTH)

        [
            "order"  : [
                "yearOrder" : yearOrders.size(),
                "monthOrder": monthOrders.size(),
                "weekOrder" : weekOrders.size()
            ],
            "premium": [
                "yearPremium" : totalPremium(yearOrders*.purchaseOrder),
                "monthPremium": totalPremium(monthOrders*.purchaseOrder),
                "weekPremium" : totalPremium(weekOrders*.purchaseOrder)
            ]
        ]
    }

    static List<ChannelAgentPurchaseOrderRebate> filterOrdersByDate(List<ChannelAgentPurchaseOrderRebate> allOrders, int dateType) {
        allOrders.findAll { it ->
            CalendarUtil.dateToInt(it.purchaseOrder.updateTime, dateType) == Calendar.getInstance().get(dateType)
        }
    }

    static totalPremium(List<PurchaseOrder> orders) {

        return DF.format(
            orders.inject(0d) {
                sumPremium, it ->
                    sumPremium + it.paidAmount
            }
        )
    }

    static Map calculateRebate(ChannelRebate channelRebate, ChannelAgent channelAgent, QuoteRecord quoteRecord) {
        double commercialRebate = 0, compulsoryRebate = 0
        log.info("根据报价计算代理人点位 quote_record id >>>>{}", quoteRecord.id)
        if (DoubleUtils.isNotZero(quoteRecord.premium) && DoubleUtils.isNotZero(quoteRecord.compulsoryPremium)) {
            commercialRebate = channelAgent.commercialRebate(channelRebate, CalculateType.actualRebate)
            compulsoryRebate = channelAgent.compulsoryRebate(channelRebate, CalculateType.actualRebate)
            log.info("交强同保代理人id>>>>{},商业险点位>>>>{},交强险点位>>>>{}", channelAgent.id, commercialRebate, compulsoryRebate)
        } else if (DoubleUtils.isNotZero(quoteRecord.premium)) {
            commercialRebate = channelAgent.onlyCommercialRebate(channelRebate, CalculateType.actualRebate)
            log.info("单商业投保代理人id>>>>{},商业险点位>>>>{}", channelAgent.id, commercialRebate,)
        } else if (DoubleUtils.isNotZero(quoteRecord.compulsoryPremium)) {
            compulsoryRebate = channelAgent.onlyCompulsoryRebate(channelRebate, CalculateType.actualRebate)
            log.info("单交强投保代理人id>>>>{},交强险点位>>>>{}", channelAgent.id, compulsoryRebate)
        }
        return [
            "commercialRebate": commercialRebate,
            "compulsoryRebate": compulsoryRebate
        ]
    }
}
