package com.cheche365.cheche.web

import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.core.repository.ChannelAgentRebateRepository
import com.cheche365.cheche.core.repository.ChannelRebateRepository
import com.cheche365.cheche.core.repository.ChecheAgentInviteCodeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentOrderRebateRepository
import com.cheche365.cheche.core.repository.agent.ChannelAgentRepository
import com.cheche365.cheche.web.service.ChannelAgentInfoService
import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat

/**
 * Author:   shanxf
 * Date:     2018/5/29 17:16
 */
abstract class ChannelAgentSpecification extends Specification{

    static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd")

    ChannelAgentInfoService channelAgentInfoService

    @Shared
    static ChannelAgent channelAgent_1, channelAgent_2, channelAgent_3

    def setup() {
        ChannelAgentOrderRebateRepository channelAgentOrderRebateRepository = Mock()
        ChannelAgentRepository channelAgentRepository = Mock()
        PurchaseOrderRepository purchaseOrderRepository = Mock()
        ChannelAgentRebateRepository channelAgentRebateRepository = Mock()
        ChannelRebateRepository channelRebateRepository = Mock()
        QuoteFlowConfigRepository quoteFlowConfigRepository = Mock()
        ChecheAgentInviteCodeRepository checheAgentInviteCodeRepository = Mock()
        channelAgentInfoService = new ChannelAgentInfoService(
            channelAgentOrderRebateRepository,
            channelAgentRepository,
            purchaseOrderRepository,
            channelAgentRebateRepository,
            channelRebateRepository,
            quoteFlowConfigRepository,
            checheAgentInviteCodeRepository
        )
        initData()
    }

    private void initData() {
        channelAgent_1 = Stub(ChannelAgent){
            getId() >> 1
            getAgentLevel() >> [id : 1]
            getAgentCode() >> null
            getParent() >> null
            commercialRebate(_) >> 50d
        }
        channelAgent_2 = Stub(ChannelAgent){
            getId() >> 2
            getAgentLevel() >> [id : 2]
            getAgentCode() >> '1'
            getParent() >> channelAgent_1
            commercialRebate(_) >> 40d
        }
        channelAgent_3 = Stub(ChannelAgent){
            getId() >> 3
            getAgentLevel() >> [id : 3]
            getParent() >> channelAgent_2
            getAgentCode() >> '1.2'
            commercialRebate(_) >> 35d
        }
        /*channelAgent_1 = new ChannelAgent().with {
            it.id = 1
            it.agentLevel = [
                id: 1
            ]
            it
        }
        channelAgent_2 = new ChannelAgent().with {
            it.id = 2
            it.agentLevel = [
                id: 2
            ]
            it.parent = channelAgent_1
            it.agentCode = '1'
            it
        }
        channelAgent_3 = new ChannelAgent().with {
            it.id = 3
            it.agentLevel = [
                id: 3
            ]
            it.parent = channelAgent_2
            it.agentCode = '1.2'
            it
        }*/
    }
}
