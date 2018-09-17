package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.agent.ChecheAgentInviteCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CcAgentInviteCodeRepository extends CrudRepository<ChecheAgentInviteCode,Long> {

    ChecheAgentInviteCode findByInviteCodeAndChannel(String inviteCode, Channel channel);

    ChecheAgentInviteCode findByInviteCode(String inviteCode);

}
