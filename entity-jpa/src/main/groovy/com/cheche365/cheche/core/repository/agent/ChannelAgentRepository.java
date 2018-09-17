package com.cheche365.cheche.core.repository.agent;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelAgentRepository extends CrudRepository<ChannelAgent, Long>, JpaSpecificationExecutor<ChannelAgent> {

    @Query(value = "select * from channel_agent where user = ?1 and channel =?2", nativeQuery = true)
    ChannelAgent findChannelAgent(Long userId, Long channelId);

    @Query(value = "select ca from ChannelAgent ca where ca.user = ?1 and ca.channel =?2 and ca.disable = false")
    ChannelAgent findByUserAndChannel(User user, Channel channel);

    @Query(value = "select ca from ChannelAgent  ca where ca.inviteCode = ?1 and ca.channel =?2 and ca.disable = false")
    ChannelAgent findByInviteCodeAndChannel(String inviteCode, Channel channel);

    @Query(value = "select count(DISTINCT(ca.id)) from channel_agent ca join " +
            "        channel_agent_purchase_order_rebate capor on ca.id = capor.channel_agent" +
            "        join purchase_order po on capor.purchase_order = po.id" +
            "        where ca.id in (?1) and po.`status` = 5 and ca.disable = 0 and ca.user = po.applicant ", nativeQuery = true)
    Long findAgentOrder(List<Long> channelAgentIds);

    @Query(value = "select * from channel_agent where CONCAT('.',agent_code,'.') like %?1% AND disable = 0", nativeQuery = true)
    List<ChannelAgent> findByAgentCodeLike(String agentCode);

    @Query(value = "select * from channel_agent where CONCAT('.',agent_code,'.') like %?1% and agent_level = ?2 and disable = 0", nativeQuery = true)
    List<ChannelAgent> findByAgentCodeLikeAndLevel(String agentCode, Long agentLevelId);

    @Query(value = "select * from channel_agent  where id in (?1) and parent is not null and disable = 0", nativeQuery = true)
    List<ChannelAgent> findParentChannel(List<Long> channelAgentIds);

    @Query(value = "SELECT sum(po.paid_amount),count(1) from  channel_agent ca join  channel_agent_purchase_order_rebate capo join  purchase_order po on ca.id = capo.channel_agent and capo.purchase_order = po.id and (po.status = 4 or po.status = 5) and ca.id = ?1", nativeQuery = true)
    List<Object[]> findOrderCountAndTotleAmount(Long channelAgentId);

    @Query(value = "select id from ChannelAgent where parent.id = ?1")
    List<Long> findChildrenIdsByParentId(Long parentId);

    @Query(value = "select channel from ChannelAgent group by channel")
    List<Long> getChannels();

    @Query(value = "select ca from ChannelAgent ca where ca.user = ?1 and ca.disable = false")
    List<ChannelAgent> findByUser(User user);

    @Query(value = "SELECT ca.* from channel_agent ca, purchase_order po where ca.user = po.applicant and ca.channel =?1 and po.id =?2", nativeQuery = true)
    ChannelAgent findByChannelAndOrderId(Long channel, Long tradeSourceId);

    ChannelAgent findFirstByUserMobileOrderByIdDesc(String mobile);

    @Query(value = "" +
            "select distinct u2.mobile " +
            "from channel_agent ca2,user u2 where ca2.user = u2.id and  ca2.parent in ( " +
            "  select ca.id " +
            "  from channel_agent ca " +
            "    join user u on ca.user = u.id " +
            "  where u.mobile = ?1 " +
            ")and ca2.agent_level = ?2 order by ca2.id ", nativeQuery = true)
    List<String> findSubLevelMobileByMobile(String mobile, Long agentLevel);
}
