package com.cheche365.cheche.core.repository.agent;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ChannelAgentPurchaseOrderRebate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelAgentOrderRebateRepository extends CrudRepository<ChannelAgentPurchaseOrderRebate, Long> {

    List<ChannelAgentPurchaseOrderRebate> findAllByPurchaseOrder(PurchaseOrder purchaseOrder);

    @Query(value = "select capor.* from channel_agent_purchase_order_rebate capor join purchase_order po " +
        "on capor.purchase_order = po.id where capor.channel_agent =?1 and po.`status` = 5 " +
        " and YEAR(po.update_time) =YEAR(NOW())", nativeQuery = true)
    List<ChannelAgentPurchaseOrderRebate> findAllOrderedByChannelAgent(ChannelAgent channelAgent);

    ChannelAgentPurchaseOrderRebate findByChannelAgentAndPurchaseOrder(ChannelAgent channelAgent, PurchaseOrder purchaseOrder);

    @Query(value = "SELECT  " +
        "capo.*  " +
        "FROM  " +
        "channel_agent_purchase_order_rebate capo,  " +
        "channel_agent ca,  " +
        "`user` u  " +
        "WHERE  " +
        "capo.channel_agent = ca.id  " +
        "AND ca.`user` = u.id  " +
        "AND capo.purchase_order = ?1 order by ca.agent_level desc limit 1,1", nativeQuery = true)
    ChannelAgentPurchaseOrderRebate findInviterAward(Long id);

    @Query(value = "SELECT  " +
        "capo.*  " +
        "FROM  " +
        "channel_agent_purchase_order_rebate capo,  " +
        "channel_agent ca,  " +
        "`user` u  " +
        "WHERE  " +
        "capo.channel_agent = ca.id  " +
        "AND ca.`user` = u.id  " +
        "AND capo.purchase_order = ?1 order by ca.agent_level desc limit 2,1", nativeQuery = true)
    ChannelAgentPurchaseOrderRebate findIndirectionAward(Long id);

    @Query(value = "select sum(po.paid_amount),count(1) from channel_agent ca, channel_agent_purchase_order_rebate capor" +
        " join purchase_order po on capor.purchase_order = po.id where ca.id =?1 and ca.id = capor.channel_agent  and" +
        " ca.user =po.applicant and po.status = 5 and YEAR(po.update_time) =YEAR(NOW()) " +
        "  and MONTH(po.update_time) =MONTH(NOW())", nativeQuery = true)
    List<Object[]> findUserMonthAgentOrder(Long channelAgentId);

    @Query(value = "select sum(po.paid_amount),count(1) from channel_agent_purchase_order_rebate capor" +
        " join purchase_order po on capor.purchase_order = po.id where capor.channel_agent = ?1 and" +
        "  po.status = 5 and YEAR(po.update_time) =YEAR(NOW()) " +
        "  and MONTH(po.update_time) =MONTH(NOW())", nativeQuery = true)
    List<Object[]> findAgentOrdersMonth(Long agentLevelId);

    @Query(value = "select sum(po.paid_amount),count(1) from channel_agent_purchase_order_rebate capor" +
        " join purchase_order po on capor.purchase_order = po.id where capor.channel_agent = ?1 and" +
        "  po.status = 5 and YEAR(po.update_time) =YEAR(NOW())", nativeQuery = true)
    List<Object[]> findAgentOrdersYear(Long agentLevelId);

    @Query(value = "select sum(po.paid_amount),count(1) from channel_agent_purchase_order_rebate capor" +
        " join purchase_order po on capor.purchase_order = po.id where capor.channel_agent = ?1 and" +
        "  po.status = 5 and YEAR(po.update_time) =YEAR(NOW()) and MONTH(po.update_time) =MONTH(NOW())and DAY(po.update_time) =DAY(NOW())", nativeQuery = true)
    List<Object[]> findAgentOrdersDay(Long agentLevelId);
}
