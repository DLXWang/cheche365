package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.agent.ChannelAgentRebateHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelAgentRebateHistoryRepository  extends CrudRepository<ChannelAgentRebateHistory,Long> {
}
