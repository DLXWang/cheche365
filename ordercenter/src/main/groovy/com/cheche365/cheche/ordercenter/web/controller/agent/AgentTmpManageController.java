package com.cheche365.cheche.ordercenter.web.controller.agent;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.service.AgentRebateHistoryTmpService;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.ordercenter.service.agent.AgentTmpManageService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.web.model.agent.AgentRebateHistoryTmpViewModel;
import com.cheche365.cheche.ordercenter.web.model.agent.AgentRebateTmpViewData;
import com.cheche365.cheche.ordercenter.web.model.agent.AgentTmpViewData;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by wangshaobin on 2017/5/5.
 */
@RestController
@RequestMapping("/orderCenter/agentTmp")
public class AgentTmpManageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AgentTmpManageService agentManageService;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AgentRebateHistoryTmpService agentRebateHistoryService;

    @Autowired
    private BaseService baseService;

    private static final String AGENT_OPERATE_TAG = "agent.rebate.operate.id";


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VisitorPermission("or0303")
    public DataTablePageViewModel list(PublicQuery query) {
        Page<AgentTmp> page = agentManageService.getAgentByPage(query);
        List<AgentTmpViewData> dataList = new ArrayList<>();
        page.getContent().forEach(agent -> dataList.add(AgentTmpViewData.createViewData(agent)));
        PageInfo pageInfo = baseService.createPageInfo(page);
        return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), dataList);
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResultModel check(@Valid AgentTmpViewData viewData, BindingResult bindingResult) {
        User user = agentManageService.getUserByMobile(viewData.getAgentMobile());
        if (bindingResult.hasErrors()) {
            return new ResultModel(false, "请将信息填写完整");
        } else if (user != null) {
            return new ResultModel(true, "检测到该号码之前已出现过，是否还要绑定");
        } else {
            return new ResultModel(true, "success");
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @VisitorPermission("or030201")
    @Transactional
    public ResultModel add(@Valid AgentTmpViewData viewData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.debug("add new agent, validation has error");
            return new ResultModel(false, "请将信息填写完整");
        }
        /*AgentTmp agentMobile = agentManageService.findFirstByMobile(viewData.getAgentMobile());
        if (agentMobile != null) {
            return new ResultModel(false, "手机号已被使用");
        }
        AgentTmp agentIdentity = agentManageService.findByIdentity(viewData.getIdentityNumber());
        if (agentIdentity != null) {
            return new ResultModel(false, "证件号已被使用");
        }
        User user = agentManageService.getUserByMobile(viewData.getAgentMobile());
        //如果代理人用户不存在则添加用户（类型为代理）
        if (user == null) {
            user = new User();
            user.setName(viewData.getAgentName());
            user.setMobile(viewData.getAgentMobile());
            user.setIdentityType(agentManageService.findIdentityType(viewData.getIdentityType()));
            user.setIdentity(viewData.getIdentityNumber());
            user.setUserType(UserType.Enum.Agent);//用户类型为代理
            user.setUpdateTime(Calendar.getInstance().getTime());
            user.setCreateTime(Calendar.getInstance().getTime());
            user = agentManageService.addUser(user);
        } else {//如果代理人用户已存在、则修改此用户类型为代理
            if (!UserType.Enum.isAgent(user.getUserType())) {//如果当前用户的用户类型不是代理
                user.setUserType(UserType.Enum.Agent);
                user.setUpdateTime(Calendar.getInstance().getTime());
                agentManageService.addUser(user);
            }
        }*/

        AgentTmp agent = this.createAgent(viewData);
        /*agent.setUser(user);*/
        List<AgentRebateTmp> agentRebateList = createAgentRebate(viewData);
        agentManageService.saveAgent(agent, agentRebateList);
        return new ResultModel(true, "保存成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    @VisitorPermission("or030201")
    @Transactional
    public ResultModel update(@Valid AgentTmpViewData viewData, BindingResult bindingResult) {
        logger.debug("update agent, agent id :{}", viewData.getId());
        if (viewData.getId() != null) {
            boolean notExist = stringRedisTemplate.opsForHash().putIfAbsent(AGENT_OPERATE_TAG, viewData.getId().toString(), "");
            if (!notExist) {
                return new ResultModel(false, "系统正在处理当前代理人数据，请稍候再操作");
            }
        }
        try {
            if (bindingResult.hasErrors()) {
                logger.debug("update agent, validation has error");
                return new ResultModel(false, "请将信息填写完整");
            }

            /*if (viewData.getId() == null || viewData.getId() < 1) {
                logger.debug("update agent, id can not be null or less than 1");
                return new ResultModel(false, "请求参数异常");
            }
            AgentTmp oriAgent = agentManageService.findOneById(viewData.getId());
            AgentTmp agentMobile = agentManageService.findFirstByMobile(viewData.getAgentMobile());
            if (agentMobile != null && !viewData.getAgentMobile().equals(oriAgent.getMobile())) {
                return new ResultModel(false, "手机号已被使用");
            }
            AgentTmp agentIdentity = agentManageService.findByIdentity(viewData.getIdentityNumber());
            if (agentIdentity != null && viewData.getIdentityNumber()!=null  && !viewData.getIdentityNumber().equals(oriAgent.getIdentity())) {
                return new ResultModel(false, "证件号已被使用");
            }

            if (!viewData.getAgentMobile().equals(oriAgent.getMobile())) {//修改了手机号
                User user = agentManageService.getUserByMobile(viewData.getAgentMobile());
                if (user == null) {//修改后的手机号不存在 修改用户手机号
                    User oriUser = oriAgent.getUser();
                    oriUser.setMobile(viewData.getAgentMobile());
                    oriUser.setUpdateTime(new Date());
                    agentManageService.addUser(oriUser);
                } else {//修改后的手机号已存在，不允许修改
                    return new ResultModel(false, "检测到该号码之前已出现过，请更换手机号");
                }
            }*/
            AgentTmp agent = this.createAgent(viewData);
            List<AgentRebateTmp> agentRebateList = createAgentRebate(viewData);
            agentManageService.saveAgent(agent, agentRebateList);
            stringRedisTemplate.opsForHash().delete(AGENT_OPERATE_TAG, viewData.getId().toString());
            return new ResultModel(true, "更新成功");
        } catch (Exception e) {
            logger.error("update agent error ,agent id :{}", viewData.getId(),e);
            return new ResultModel(false, "更新失败");
        } finally {
            stringRedisTemplate.opsForHash().delete(AGENT_OPERATE_TAG, viewData.getId().toString());
        }
    }

    @RequestMapping(value = "/{agentId}", method = RequestMethod.DELETE)
    public boolean delete(@PathVariable(value = "agentId") Long agentId) {
        if (agentId == null || agentId < 1) {
            logger.debug("delete agent by id, id can not be null or less than 1.");
            return false;
        }
        return agentManageService.deleteAgent(agentId);
    }

    @RequestMapping(value = "/{agentId}", method = RequestMethod.PUT)
    public AgentTmpViewData findOne(@PathVariable Long agentId) {
        return AgentTmpViewData.createViewData(agentManageService.findOneById(agentId));
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public Map<String, Object> findAgentByUser(@PathVariable Long userId) {
        AgentTmp agent = agentManageService.findByUserId(userId);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("agent", agent);
        return objectMap;
    }

    @RequestMapping(value = "/rebate/{agentId}", method = RequestMethod.GET)
    public PageViewModel<AgentRebateTmpViewData> getAgentRebate(@PathVariable(value = "agentId") Long agentId) {
        AgentTmp agent = agentManageService.findOneById(agentId);
        PageViewModel<AgentRebateTmpViewData> model = new PageViewModel<AgentRebateTmpViewData>();
        List<AgentRebateTmp> agentRebateList = agentManageService.findRebateByAgent(agent);
        List<AgentRebateTmpViewData> agentRebateViewDataList = new ArrayList<>();
        for (AgentRebateTmp agentRebate : agentRebateList) {
            agentRebateViewDataList.add(new AgentRebateTmpViewData(agentRebate));
        }
        model.setViewList(agentRebateViewDataList);
        return model;
    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    @VisitorPermission("or030201")
    public ResultModel enable(@PathVariable Long id) {
        try {
            agentManageService.setEnable(id);
            return new ResultModel(true, "保存成功！");
        } catch (Exception e) {
            logger.error("enable agent has error", e);
            return new ResultModel(false, "保存失败");
        }
    }

    @RequestMapping(value = "/rebate", method = RequestMethod.PUT)
    @VisitorPermission("or030201")
    @Transactional
    public ResultModel addAgentRebateHistory(@Valid AgentRebateHistoryTmpViewModel viewModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.debug("update agent rebate history, validation has error");
            return new ResultModel(false, "请将信息填写完整");
        }
        // 验证代理人
        AgentTmp agent = agentManageService.findOneById(viewModel.getAgent());
        if (agent == null) {
            return new ResultModel(false, "该代理人不存在");
        }
        // 验证历史费率的开始时间
        AgentRebateHistoryTmp history = createAgentRebateHistory(viewModel);
        Date checkDate = agentManageService.checkAgentRebateHistoryStartTime(history);
        if (checkDate != null) {
            return new ResultModel(false, "开始时间不能大于" + DateUtils.getDateString(checkDate, DateUtils.DATE_LONGTIME24_PATTERN));
        }
        agentManageService.addAgentRebateHistory(history);
        return new ResultModel(true, "保存成功");
    }

    /**
     * create agent db model
     *
     * @param viewData
     * @return
     * @throws Exception
     */
    public AgentTmp createAgent(AgentTmpViewData viewData) {
        AgentTmp agent = new AgentTmp();
        if (viewData.getId() != null)
            agent = agentManageService.findOneById(viewData.getId());

        agent.setName(viewData.getAgentName());
        agent.setMobile(viewData.getAgentMobile());
        agent.setIdentityType(agentManageService.findIdentityType(viewData.getIdentityType()));
        agent.setIdentity(viewData.getIdentityNumber());
        agent.setCardNumber(viewData.getCardNumber());
        agent.setOpeningBank(viewData.getOpeningBank());
        agent.setBankBranch(viewData.getBankBranch());
        agent.setBankAccount(viewData.getBankAccount());
        agent.setComment(viewData.getComment());
        agent.setAgentCompany(null);
        agent.setOperator(agentManageService.findOperator());
        agent.setRebate(Double.parseDouble(viewData.getRebate()));
        agent.setUpdateTime(new Date());
        agent.setCreateTime(viewData.getId() == null ? new Date() : agent.getCreateTime());
        return agent;
    }

    public List<AgentRebateTmp> createAgentRebate(AgentTmpViewData agentViewData) {
        List<AgentRebateTmpViewData> AgentRebateViewDataList = agentViewData.getAgentRebate();
        List<AgentRebateTmp> agentRebateList = new ArrayList<>();
        if (CollectionUtils.isEmpty(AgentRebateViewDataList)) {
            return agentRebateList;
        }
        for (AgentRebateTmpViewData agentRebateViewData : AgentRebateViewDataList) {
            if (agentRebateViewData.getArea() == null) {
                continue;
            }
            AgentRebateTmp agentRebate = new AgentRebateTmp();
            agentRebate.setArea(areaRepository.findById(agentRebateViewData.getArea()));
            agentRebate.setInsuranceCompany(insuranceCompanyRepository.findOne(agentRebateViewData.getInsuranceCompany()));
            agentRebate.setCommercialRebate(agentRebateViewData.getCommercialRebate());
            agentRebate.setCompulsoryRebate(agentRebateViewData.getCompulsoryRebate());
            agentRebateList.add(agentRebate);
        }
        return agentRebateList;
    }

    private AgentRebateHistoryTmp createAgentRebateHistory(AgentRebateHistoryTmpViewModel viewModel) {
        AgentRebateHistoryTmp history = new AgentRebateHistoryTmp();
        history.setAgent(agentManageService.findOneById(viewModel.getAgent()));
        history.setArea(areaRepository.findOne(viewModel.getArea()));
        history.setInsuranceCompany(insuranceCompanyRepository.findOne(viewModel.getInsuranceCompany()));
        history.setCommercialRebate(viewModel.getCommercialRebate());
        history.setCompulsoryRebate(viewModel.getCompulsoryRebate());
        history.setStartTime(DateUtils.getDate(viewModel.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        history.setEndTime(DateUtils.getDate(viewModel.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        return history;
    }

    /**
     * 通过出单机构查询历史费率
     *
     * @param agentId
     * @return
     */
    @RequestMapping(value = "/rebate/historyList", method = RequestMethod.GET)
//    @VisitorPermission("op0102")
    public List<AgentRebateHistoryTmpViewModel> getRebateHistoryList(@RequestParam(value = "agentId", required = false) Long agentId) {
        List<AgentRebateHistoryTmp> rebateHistoryList = agentRebateHistoryService.findByAgentId(agentId);
        List<AgentRebateHistoryTmpViewModel> rebateHistoryViewModels = new ArrayList<AgentRebateHistoryTmpViewModel>();
        rebateHistoryList.forEach(rebateHistory -> {
            rebateHistoryViewModels.add(AgentRebateHistoryTmpViewModel.createViewModel(rebateHistory));
        });
        return rebateHistoryViewModels;
    }
}
