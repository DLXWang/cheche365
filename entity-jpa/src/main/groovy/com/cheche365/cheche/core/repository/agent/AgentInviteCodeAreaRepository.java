package com.cheche365.cheche.core.repository.agent;

import com.cheche365.cheche.core.model.agent.AgentInviteCodeArea;
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheEvict(value = "areaGroupIncludeSI", allEntries = true,condition = "#root.methodName eq 'save'")
public interface AgentInviteCodeAreaRepository extends CrudRepository<AgentInviteCodeArea,Long> {

    List<AgentInviteCodeArea> findByChecheAgentInviteCode(ChecheAgentInviteCode inviteCode);
    @Query(value = "select aica.* from channel_agent ca,cheche_agent_invite_code caic,agent_invite_code_area aica " +
        "   where ca.id = caic.channel_agent" +
        "   and aica.cheche_agent_invite_code = caic.id" +
        "   and ca.id = ?1",nativeQuery = true)
    List<AgentInviteCodeArea> findAgentInviteCodeAreaByChannelAgent(Long id);

    <S extends AgentInviteCodeArea> S save(S entity);

    <S extends AgentInviteCodeArea> List<S> save(Iterable<S> entities);
}
