package com.cheche365.cheche.web.service.order.discount.strategy

import com.cheche365.cheche.core.model.ChannelRebate
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.ChannelRebateRepository
import com.cheche365.cheche.web.service.ChannelAgentService
import com.cheche365.cheche.core.service.agent.ChannelRebateService
import com.cheche365.cheche.web.service.order.discount.AgentDiscountService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.PaymentChannel.Enum.AGENTREBATE_9

/**
 * Created by mahong on 14/06/2017.
 * 标准代理人渠道计算返点策略。渠道总返点配置在渠道上(channel_rebate表)。
 * 如果渠道支持多级管理，则下级代理人返点由上级代理人设置。顶级代理人返点用渠道的返点。
 */
@Service
@Transactional
@Slf4j
@Order(value = 1)
class ChannelAgentStrategy extends DiscountStrategy {
    @Autowired
    public ChannelRebateService channelRebateService

    @Autowired
    private ChannelRebateRepository rebateRepository

    @Autowired
    private AgentDiscountService agentDiscountService

    @Autowired
    private ChannelAgentService channelAgentService

    @Override
    def applyDiscountStrategy(QuoteRecord quoteRecord, order, Long giftId) {
        ChannelAgent channelAgent = channelAgentService.getCurrentChannelAgent(quoteRecord)
        def result = [paymentChannel: AGENTREBATE_9]
        if(channelAgent){
            agentDiscountService.calculateDiscountsByChannelAgent(quoteRecord,channelAgent)
        } else {
            ChannelRebate rebate = channelRebateService.getChannelRebate(quoteRecord, order)
            agentDiscountService.calculateDiscounts(quoteRecord, rebate);
        }
        result.discountAmount = 0d
        return result
    }

    @Override
    boolean support(QuoteRecord quoteRecord, order, Long giftId) {
        return quoteRecord.channel.parent.isStandardAgent()
    }

    @Override
    int belongsToGroup() {
        return NON_USER_INVOLVED
    }
}
