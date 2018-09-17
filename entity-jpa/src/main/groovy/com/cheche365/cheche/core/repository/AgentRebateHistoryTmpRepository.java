package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AgentTmp;
import com.cheche365.cheche.core.model.AgentRebateHistoryTmp;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@Repository
public interface AgentRebateHistoryTmpRepository extends PagingAndSortingRepository<AgentRebateHistoryTmp,Long> {

    AgentRebateHistoryTmp findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTime(AgentTmp agent, Area area, InsuranceCompany insuranceCompany);

    AgentRebateHistoryTmp findFirstByAgentAndAreaAndInsuranceCompanyOrderByIdDesc(AgentTmp agent, Area area, InsuranceCompany insuranceCompany);

    AgentRebateHistoryTmp findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTimeDesc(AgentTmp agent, Area area, InsuranceCompany insuranceCompany);

    @Query(value="select * from agent_rebate_history_tmp where agent=?1 and area=?2 and insurance_company =?3 and operation !=3 and start_time<?4 and ( end_time>=?4 or end_time is null) order by id desc limit 1",nativeQuery=true)
    AgentRebateHistoryTmp listByAgentAndAreaAndInsuranceCompanyAndDateTime(Long agentId,Long areaId,Long insuranceCompanyId,Date applicantDate);
    List<AgentRebateHistoryTmp> findByAgentOrderByStartTime(AgentTmp agent);
}
