package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AgentTmp;
import com.cheche365.cheche.core.model.AgentRebateTmp;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@Repository
@Profile("!test")
public interface AgentRebateTmpRepository extends PagingAndSortingRepository<AgentRebateTmp, Long> {
    List<AgentRebateTmp> findByAgent(AgentTmp agent);

    AgentRebateTmp findFirstByAgentAndAreaAndInsuranceCompany(AgentTmp agent, Area area, InsuranceCompany insuranceCompany);
}
