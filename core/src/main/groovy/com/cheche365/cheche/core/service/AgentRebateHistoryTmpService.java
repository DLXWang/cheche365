package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AgentRebateHistoryTmpRepository;
import com.cheche365.cheche.core.repository.AgentTmpRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@Service
public class AgentRebateHistoryTmpService {

    @Autowired
    private AgentRebateHistoryTmpRepository agentRebateHistoryRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;


    @Autowired
    private AgentTmpRepository agentRepository;

    public void save(AgentRebateTmp agentRebate, InternalUser internalUser, Integer operation) {
        AgentRebateHistoryTmp agentRebateHistory = createAgentRebateHistory(agentRebate, internalUser);
        refreshPrev(agentRebateHistory);
        agentRebateHistory.setStartTime(new Date());
        agentRebateHistory.setOperation(operation);
        agentRebateHistoryRepository.save(agentRebateHistory);
    }

    private AgentRebateHistoryTmp createAgentRebateHistory(AgentRebateTmp agentRebate, InternalUser internalUser) {
        AgentRebateHistoryTmp agentRebateHistory = new AgentRebateHistoryTmp();
        String[] properties = new String[]{
            "agent", "area", "insuranceCompany", "compulsoryRebate", "commercialRebate"};
        BeanUtil.copyPropertiesContain(agentRebate, agentRebateHistory, properties);
        agentRebateHistory.setOperator(internalUser);
        return agentRebateHistory;
    }

    //刷新上一条历史记录的结束时间
    private void refreshPrev(AgentRebateHistoryTmp agentRebateHistory) {
        AgentRebateHistoryTmp prevHistory = agentRebateHistoryRepository.findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTimeDesc(agentRebateHistory.getAgent(), agentRebateHistory.getArea(), agentRebateHistory.getInsuranceCompany());
        if (prevHistory != null) {
            prevHistory.setEndTime(new Date());
            agentRebateHistoryRepository.save(prevHistory);
        }
    }

    public AgentRebateHistoryTmp listByAgentAndAreaAndInsuranceCompanyAndDateTime(AgentTmp agent, Area area, Long insuranceCompanyId, Date scopeDate){
        InsuranceCompany insuranceCompany=insuranceCompanyRepository.findOne(insuranceCompanyId);
        return agentRebateHistoryRepository.listByAgentAndAreaAndInsuranceCompanyAndDateTime(agent.getId(),area.getId(),insuranceCompany.getId(),scopeDate);

    }

    public List<AgentRebateHistoryTmp> findByAgentId(Long agentId){
        AgentTmp agent=agentRepository.findOne(agentId);
        return agentRebateHistoryRepository.findByAgentOrderByStartTime(agent);
    }
}
