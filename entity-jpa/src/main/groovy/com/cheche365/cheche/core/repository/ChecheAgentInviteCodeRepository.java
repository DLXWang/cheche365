package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecheAgentInviteCodeRepository extends PagingAndSortingRepository<ChecheAgentInviteCode, Long>, JpaSpecificationExecutor<ChecheAgentInviteCode> {

    ChecheAgentInviteCode findByChannelAgentId(Long channelAgent_id);
}
