package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelAgentPurchaseOrderRebateRepository extends CrudRepository<ChannelAgentPurchaseOrderRebate, Long>, JpaSpecificationExecutor<ChannelAgentPurchaseOrderRebate> {

    @Query(value ="select cp.* from channel_agent_purchase_order_rebate cp,channel_agent c where cp.channel_agent = c.id and cp.purchase_order = ?1 order by c.agent_level "  ,nativeQuery = true)
    List<ChannelAgentPurchaseOrderRebate> findByPurchaseOrder(Long purchaseOrderId);

    ChannelAgentPurchaseOrderRebate  findByChannelAgentAndPurchaseOrder(ChannelAgent channelAgent, PurchaseOrder purchaseOrder);
}
