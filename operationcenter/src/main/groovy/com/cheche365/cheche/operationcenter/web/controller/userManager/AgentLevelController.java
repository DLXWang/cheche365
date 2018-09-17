package com.cheche365.cheche.operationcenter.web.controller.userManager;

import com.cheche365.cheche.core.model.agent.AgentLevel;
import com.cheche365.cheche.core.repository.AgentLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by liulu on 2018/3/15.
 */
@RestController
@RequestMapping("/operationcenter/agentLevel")
public class AgentLevelController {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @RequestMapping(value = "/agentLevelList", method = RequestMethod.GET)
    public List<AgentLevel> activityTypeList() {
        List<AgentLevel> list = new ArrayList<AgentLevel>();
        agentLevelRepository.findAll().forEach((it )->list.add(it));
        return list;

    }

}
