package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Agent;
import com.cheche365.cheche.core.model.AgentRebate;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu.yelong on 2016/2/17.
 */
@Repository
@Profile("!test")
public interface AgentRebateRepository extends PagingAndSortingRepository<AgentRebate, Long> {
    List<AgentRebate> findByAgent(Agent agent);

    AgentRebate findFirstByAgentAndAreaAndInsuranceCompany(Agent agent, Area area, InsuranceCompany insuranceCompany);
}
