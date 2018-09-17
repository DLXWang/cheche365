package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.Agent;
import com.cheche365.cheche.core.repository.AgentRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.web.model.agent.AgentViewData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class AgentResource extends BaseService<Agent, Agent> {

    @Autowired
    private AgentRepository agentRepository;

    public List<Agent> listAll() {
        return super.getAll(agentRepository);
    }

    public List<Agent> listEnable() {
        return agentRepository.findByUserIsNotNull();
    }

    public List<Agent> listEnableByKeyWord(String name){
        return agentRepository.listByKeywod(name);
    }

    public List<AgentViewData> createViewData(List<Agent> agentList) {
        if (agentList == null)
            return null;

        List<AgentViewData> viewDataList = new ArrayList<>();
        agentList.forEach(agent -> {
            AgentViewData viewData = new AgentViewData();
            viewData.setId(agent.getId());
            viewData.setAgentName(agent.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
