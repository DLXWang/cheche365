package com.cheche365.cheche.web.service.order.discount.strategy

import com.cheche365.cheche.core.model.Agent
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.AgentRepository
import com.cheche365.cheche.core.service.AgentService
import com.cheche365.cheche.web.service.InsurancePurchaseOrderRebateService
import com.cheche365.cheche.core.service.QuoteConfigService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.model.PaymentChannel.Enum.AGENTREBATE_9

/**
 * Created by mahong on 2015/6/2.
 * 个人代理人返点策略。个人是相对于渠道代理人。
 * 返点配置在每一个代理人上。目前貌似已经没有新的个人代理人注册功能。
 */
@Service
@Transactional
@Slf4j
@Order(value=2)
class IndividualAgentStrategy extends DiscountStrategy {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private AgentService agentService;

    @Autowired
    private InsurancePurchaseOrderRebateService orderRebateService;

    @Autowired
    private QuoteConfigService quoteConfigService

    @Override
    def applyDiscountStrategy(QuoteRecord quoteRecord, order, Long giftId) {

        def result = [paymentChannel : AGENTREBATE_9]

        Boolean rebateNotSupport = quoteRecord.apiQuote()

        if(rebateNotSupport) {
            result.discountAmount = 0d
            return result
        }

        Agent agent = agentRepository.findFirstByUser(quoteRecord.getApplicant());
        result.discountAmount = agentService.calculateRebateAmount(quoteRecord, agent)
        result.persistentCallback = {
            orderRebateService.savePurchaseOrderRebate(quoteRecord, order, agent);
        }
        result
    }

    @Override
    boolean support(QuoteRecord quoteRecord, purchaseOrder, Long giftId) {
        return quoteRecord.applicant && agentService.checkAgent(quoteRecord.applicant)
    }

    @Override
    int belongsToGroup(){
        return NON_USER_INVOLVED;
    }
}
