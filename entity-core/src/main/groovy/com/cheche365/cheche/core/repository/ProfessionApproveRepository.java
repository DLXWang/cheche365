package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ProfessionApprove;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Author:   shanxf
 * Date:     2018/9/10 16:18
 */
@Repository
public interface ProfessionApproveRepository extends CrudRepository<ProfessionApprove,Long> {

    ProfessionApprove findByChannelAgent(ChannelAgent channelAgent);
}
