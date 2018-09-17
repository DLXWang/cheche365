package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.agent.ChannelAgent;
import com.cheche365.cheche.core.model.agent.ChannelAgentRebate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelAgentRebateRepository extends CrudRepository<ChannelAgentRebate,Long> {
    ChannelAgentRebate findByAreaAndInsuranceCompanyAndChannelAgent(Area area, InsuranceCompany insuranceCompany, ChannelAgent channelAgent);

    List<ChannelAgentRebate> findByAreaAndChannelAgent(Area area, ChannelAgent channelAgent);
}
