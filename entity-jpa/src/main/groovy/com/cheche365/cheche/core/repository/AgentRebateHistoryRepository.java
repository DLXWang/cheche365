package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Agent;
import com.cheche365.cheche.core.model.AgentRebateHistory;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016-05-20.
 */
@Repository
public interface AgentRebateHistoryRepository extends PagingAndSortingRepository<AgentRebateHistory, Long> {

    AgentRebateHistory findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTime(Agent agent, Area area, InsuranceCompany insuranceCompany);

    AgentRebateHistory findFirstByAgentAndAreaAndInsuranceCompanyOrderByIdDesc(Agent agent, Area area, InsuranceCompany insuranceCompany);

    AgentRebateHistory findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTimeDesc(Agent agent, Area area, InsuranceCompany insuranceCompany);

    @Query(value = "select * from agent_rebate_history where agent=?1 and area=?2 and insurance_company =?3 and operation !=3 " +
            " and start_time<= ?4 and ( end_time>?4 or end_time is null) " +
            " order by id desc limit 1 ", nativeQuery = true)
    AgentRebateHistory listByAgentAndAreaAndInsuranceCompanyAndDateTime(Long agentId, Long areaId, Long insuranceCompanyId, Date applicantDate);

    List<AgentRebateHistory> findByAgentOrderByStartTime(Agent agent);
}
