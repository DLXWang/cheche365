package com.cheche365.cheche.ordercenter.service.agent;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.AgentRebateHistoryService;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/***************************************************************************/
/*                              AgentManageService.java                 */
/*   文   件 名: AgentManageService.java                                  */
/*   模  块： 动态佣金管理系统                                              */
/*   功  能:  动态佣金管理服务类                            */
/*   初始创建:2015/5/15                                            */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */

/***************************************************************************/

@Service
public class AgentManageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private IdentityTypeRepository identityTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private AgentRebateRepository agentRebateRepository;

    @Autowired
    private AgentRebateHistoryService agentRebateHistoryService;

    @Autowired
    private AgentRebateHistoryRepository agentRebateHistoryRepository;

    @Autowired
    private BaseService baseService;

    public Page<Agent> getAgentByPage(PublicQuery query) {
        return findBySpecAndPaginate(baseService.buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, baseService.SORT_CREATE_TIME), query);
    }

    /**
     * 根据手机号获取用户信息，如果获取到多个用户则取绑定微信号的用户
     *
     * @param agentMobile
     * @return
     * @throws Exception
     */
    public User getUserByMobile(String agentMobile) {
        User user = null;
        List<User> userList = userRepository.findUsersByMobile(agentMobile);
        if (userList != null && userList.size() > 0) {
            if (userList.size() == 1) {
                user = userList.get(0);
            } else {
                for (User u : userList) {
                    if (Boolean.TRUE.equals(u.isBound())) {
                        return u;
                    }
                }
            }
        }
        return user;
    }

    /**
     * delete agent by id
     *
     * @param id id long agent(id)
     */
    public boolean deleteAgent(Long id) {
        try {
            logger.debug("delete agent by id start...");
            Agent agent = agentRepository.findOne(id);
            // 修改用户类型为：消费者
            if (agent.getUser() != null) {
                User user = agent.getUser();
                user.setUserType(UserType.Enum.Customer);
                user.setUpdateTime(Calendar.getInstance().getTime());
                userRepository.save(user);
            }
            // 删除代理人
            agentRepository.delete(agent);
            logger.debug("delete agent by id start...");
            return true;
        } catch (Exception e) {
            logger.error("delete agent by id has error", e);
        }

        return false;
    }

    /**
     * find one agent by id
     *
     * @param id Long agent(id)
     * @return Agent
     */
    public Agent findOneById(Long id) {
        return agentRepository.findOne(id);
    }

    public IdentityType findIdentityType(String identityType) {
        return identityTypeRepository.findFirstByName(identityType);
    }

    public InternalUser findOperator() {
        return orderCenterInternalUserManageService.getCurrentInternalUser();
    }

    public Agent findByMobile(String mobile) {
        return agentRepository.findFirstByMobile(mobile);
    }

    public Agent findByIdentity(String identityNumber) {
        return agentRepository.findByIdentity(identityNumber);
    }


    public void saveAgent(Agent agent, List<AgentRebate> agentRebateList) {
        agent = this.addAgent(agent);
        this.saveAgentRebate(agent, agentRebateList);
    }

    public Agent addAgent(Agent agent) {
        return agentRepository.save(agent);
    }

    public Agent findByUserId(Long userId) {
        return agentRepository.findFirstByUser(userRepository.findOne(userId));
    }

    public Page<Agent> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery) {
        return agentRepository.findAll((root, query, cb) -> {
            CriteriaQuery<Agent> criteriaQuery = cb.createQuery(Agent.class);

            //条件构造
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                Path<String> namePath = root.get("name");
                Path<String> mobilePath = root.get("mobile");
                predicateList.add(cb.or(
                    cb.like(namePath, publicQuery.getKeyword() + "%"),
                    cb.like(mobilePath, publicQuery.getKeyword() + "%")
                ));
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }

    public List<AgentRebate> findRebateByAgent(Agent agent) {
        return agentRebateRepository.findByAgent(agent);
    }

    public void setEnable(Long id) {
        Agent agent = agentRepository.findOne(id);
        agent.setEnable(!agent.getEnable() ? true : false);
        agentRepository.save(agent);
    }

    /**
     * 保存代理人返点信息及代理人返点历史数据
     *
     * @param agent
     * @param newRebateList
     */
    private void saveAgentRebate(Agent agent, List<AgentRebate> newRebateList) {
        List<AgentRebate> oldRebateList = agentRebateRepository.findByAgent(agent);
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        int i = 0;
        while (i < oldRebateList.size()) {
            AgentRebate oldAgentRebate = oldRebateList.get(i);
            Boolean del = false;
            for (AgentRebate newAgentRebate : newRebateList) {
                newAgentRebate.setAgent(agent);
                if (oldAgentRebate.getArea().getId().equals(newAgentRebate.getArea().getId())
                    && oldAgentRebate.getInsuranceCompany().getId().equals(newAgentRebate.getInsuranceCompany().getId())) {
                    if (!oldAgentRebate.getCommercialRebate().equals(newAgentRebate.getCommercialRebate())
                        || !oldAgentRebate.getCompulsoryRebate().equals(newAgentRebate.getCompulsoryRebate())) {
                        newAgentRebate.setId(oldAgentRebate.getId());
                        agentRebateRepository.save(newAgentRebate);
                        agentRebateHistoryService.save(newAgentRebate, internalUser, AgentRebateHistory.OPERATION.UPD);
                    }
                    newRebateList.remove(newAgentRebate);
                    del = true;
                    break;
                }
            }
            if (del) {
                oldRebateList.remove(oldAgentRebate);
            } else {
                i++;
            }
        }
        for (AgentRebate agentRebate : oldRebateList) {
            agentRebateRepository.delete(agentRebate);
            agentRebateHistoryService.save(agentRebate, internalUser, AgentRebateHistory.OPERATION.DEL);
        }
        for (AgentRebate agentRebate : newRebateList) {
            agentRebate.setAgent(agent);
            agentRebateRepository.save(agentRebate);
            agentRebateHistoryService.save(agentRebate, internalUser, AgentRebateHistory.OPERATION.ADD);
        }
    }

    /**
     * 根据代理人，城市，保险公司，验证开始时间的范围
     * 开始时间要小于当前时间并且小于相应城市和保险公司的历史费率的最小开始时间
     *
     * @param history
     * @return
     */
    public Date checkAgentRebateHistoryStartTime(AgentRebateHistory history) {
        logger.debug("验证回录代理人历史费率的开始时间，代理人:{}，城市:{}，保险公司:{}，开始时间:{}",
            history.getAgent().getName(), history.getArea().getName(), history.getInsuranceCompany().getName(),
            DateUtils.getDateString(history.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        AgentRebateHistory firstHistory = agentRebateHistoryRepository.findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTime(
            history.getAgent(), history.getArea(), history.getInsuranceCompany());
        if (firstHistory == null) {
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            return DateUtils.dateDiff(history.getStartTime(), currentTime, DateUtils.INTERNAL_DATE_SECOND) < 0 ?
                currentTime : null;
        } else {
            return DateUtils.dateDiff(history.getStartTime(), firstHistory.getStartTime(), DateUtils.INTERNAL_DATE_SECOND) < 0 ?
                firstHistory.getStartTime() : null;
        }
    }

    /**
     * 增加代理人的历史费率信息
     *
     * @param history
     */
    public void addAgentRebateHistory(AgentRebateHistory history) {
        AgentRebateHistory firstHistory = agentRebateHistoryRepository.findFirstByAgentAndAreaAndInsuranceCompanyOrderByStartTime(
            history.getAgent(), history.getArea(), history.getInsuranceCompany());
        if (firstHistory != null) {
            history.setEndTime(firstHistory.getStartTime());
        }
        history.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        history.setOperation(AgentRebateHistory.OPERATION.ADD);
        agentRebateHistoryRepository.save(history);
    }
}
