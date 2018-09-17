package com.cheche365.cheche.core.service;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AgentRebateHistoryRepository;
import com.cheche365.cheche.core.repository.AgentRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/***************************************************************************/
/*                              AgentRebateHistoryService.java                 */
/*   文   件 名: AgentRebateHistoryService.java                                  */
/*   模  块： 动态佣金管理系统                                              */
/*   功  能:  历史佣金管理服务                           */
/*   初始创建:2016/5/20                                            */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */

/***************************************************************************/

@Service
public class AgentRebateHistoryService {

    @Autowired
    private AgentRebateHistoryRepository agentRebateHistoryRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;


    @Autowired
    private AgentRepository agentRepository;

    public void save(AgentRebate agentRebate, InternalUser internalUser, Integer operation) {
        AgentRebateHistory agentRebateHistory = createAgentRebateHistory(agentRebate, internalUser);
        refreshPrev(agentRebateHistory);
        agentRebateHistory.setStartTime(new Date());
        agentRebateHistory.setOperation(operation);
        agentRebateHistoryRepository.save(agentRebateHistory);
    }

    private AgentRebateHistory createAgentRebateHistory(AgentRebate agentRebate, InternalUser internalUser) {
        AgentRebateHistory agentRebateHistory = new AgentRebateHistory();
        String[] properties = new String[]{
                "agent", "area", "insuranceCompany", "compulsoryRebate", "commercialRebate"};
        BeanUtil.copyPropertiesContain(agentRebate, agentRebateHistory, properties);
        agentRebateHistory.setOperator(internalUser);
        return agentRebateHistory;
    }

    //刷新上一条历史记录的结束时间
    private void refreshPrev(AgentRebateHistory agentRebateHistory) {
        AgentRebateHistory prevHistory = agentRebateHistoryRepository.findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTimeDesc(agentRebateHistory.getAgent(), agentRebateHistory.getArea(), agentRebateHistory.getInsuranceCompany());
        if (prevHistory != null) {
            prevHistory.setEndTime(new Date());
            agentRebateHistoryRepository.save(prevHistory);
        }
    }

    public AgentRebateHistory listByAgentAndAreaAndInsuranceCompanyAndDateTime(Agent agent, Area area, Long insuranceCompanyId, Date scopeDate) {
        return agentRebateHistoryRepository.listByAgentAndAreaAndInsuranceCompanyAndDateTime(agent.getId(), area.getId(), insuranceCompanyId, scopeDate);

    }

    public List<AgentRebateHistory> findByAgentId(Long agentId) {
        Agent agent = agentRepository.findOne(agentId);
        return agentRebateHistoryRepository.findByAgentOrderByStartTime(agent);
    }
}
