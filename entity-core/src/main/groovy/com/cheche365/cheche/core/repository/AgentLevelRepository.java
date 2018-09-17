package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.agent.AgentLevel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentLevelRepository extends CrudRepository<AgentLevel,Long> {
}
