package com.cheche365.cheche.ordercenter.service.telMarketingCenter

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.InternalUserRole
import com.cheche365.cheche.core.model.Role
import com.cheche365.cheche.core.model.TelMarketingCenterRepeat
import com.cheche365.cheche.core.repository.InternalUserRepository
import com.cheche365.cheche.core.repository.TelMarketingCenterRepeatRepository
import com.cheche365.cheche.core.service.InternalUserRoleService
import com.cheche365.cheche.core.service.InternalUserService
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.manage.common.model.TelMarketingCenter
import com.cheche365.cheche.manage.common.model.TelMarketingCenterHistory
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterHistoryRepository
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.manage.common.web.model.ResultModel
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterRequestParams
import com.cheche365.cheche.ordercenter.web.model.telMarketingCenter.TelMarketingCenterViewModel
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit
/**
 * Created by xu.yelong on 2016-04-29.
 */
@Service
public class CustomerRedistributionService {
    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TelMarketingCenterHistoryRepository telMarketingCenterHistoryRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private InternalUserRoleService internalUserRoleService;

    @Autowired
    private TelMarketingCenterManageService telMarketingCenterManageService;

    @Autowired
    private TelMarketingCenterRepeatRepository telMarketingCenterRepeatRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String CACHE_KEY = "schedules.task.customer.redistribution";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<InternalUser> getAssignersByOperatorId(Long operatorId) {
        InternalUser internalUser = internalUserRepository.findOne(operatorId);
        return internalUserService.listAllEnableTelCommissionerExceptOne(internalUser);
    }

    public List<InternalUser> listAllEnableTelCommissioner() {
        return internalUserService.listAllEnableTelCommissioner();
    }

    public Integer getCountByAssigner(Long operatorId, Long status) {
        if (status != null) {
            return telMarketingCenterRepository.countByOperatorAndStatus(operatorId, status);
        }
        return telMarketingCenterRepository.countByOperator(operatorId);
    }

    public void redistributeByMobile(String phoneNo, Long newOperatorId) {
        List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterRepository.findByMobile(phoneNo);
        if (CollectionUtils.isEmpty(telMarketingCenterList)) {
            return;
        }
        InternalUser internalUser = internalUserRepository.findOne(newOperatorId);
        if (internalUser == null) {
            return;
        }
        List<TelMarketingCenterHistory> telMarketingCenterHistoryList = new ArrayList<>();
        InternalUser operator = orderCenterInternalUserManageService.getCurrentInternalUser();
        for (TelMarketingCenter telMarketingCenter : telMarketingCenterList) {
            InternalUser oldOperator = telMarketingCenter.getOperator();
            telMarketingCenter.setOperator(internalUser);
            telMarketingCenterHistoryList.add(this.createHistory(telMarketingCenter, oldOperator, internalUser, operator));
        }
        telMarketingCenterRepository.save(telMarketingCenterList);
        telMarketingCenterHistoryRepository.save(telMarketingCenterHistoryList);

    }

    public ResultModel redistributeByOperator(Long oldOperatorId, Long newOperatorId, String distributionType, String[] checkedIds) {
        InternalUser oldInternalUser = internalUserRepository.findOne(oldOperatorId);
        InternalUser currentUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        if (oldInternalUser == null) {
            return new ResultModel(false, "当前操作人不存在！");
        }
        if ("0".equals(distributionType)) {//选定数据变更联系人
            InternalUser newOperator = internalUserRepository.findOne(newOperatorId);
            //  String[] checkedIdArr = checkedIds.split(",");
            List<TelMarketingCenter> telMarketingCenterList = telMarketingCenterRepository.findByIds(Arrays.asList(checkedIds));
            List<TelMarketingCenterHistory> telMarketingCenterHistoryList = new ArrayList<>();

            for (TelMarketingCenter telMarketingCenter : telMarketingCenterList) {
                telMarketingCenter.setOperator(newOperator);
                telMarketingCenterHistoryList.add(createHistory(telMarketingCenter, oldInternalUser, newOperator, currentUser));
            }
            telMarketingCenterRepository.save(telMarketingCenterList);
            telMarketingCenterHistoryRepository.save(telMarketingCenterHistoryList);
            logger.debug("save customer redistribution successful ,oldOperatorId:{} , newOperatorId:{} ,distributionType:{} , checkedIds:{}", oldOperatorId, newOperatorId, distributionType, checkedIds);
            return new ResultModel(true, "指定分配人成功！已生效！");
        }

        logger.debug("save customer redistribution operate to redis ,oldOperatorId:{} , newOperatorId:{} ,distributionType:{}", oldOperatorId, newOperatorId, distributionType);
        Map map = new HashMap();
        map.put("oldOperatorId", oldOperatorId);
        map.put("newOperatorId", newOperatorId);
        map.put("distributionType", distributionType);
        map.put("operator", currentUser.getId());
        stringRedisTemplate.opsForList().leftPush(CACHE_KEY, CacheUtil.doJacksonSerialize(map));
        return new ResultModel(true, "指定分配人成功！30分钟内指派生效，请勿重复指派！");
    }

    private TelMarketingCenterHistory createHistory(TelMarketingCenter telMarketingCenter, InternalUser oldOperator, InternalUser newOperator, InternalUser operator) {
        TelMarketingCenterHistory telMarketingCenterHistory = new TelMarketingCenterHistory();
        telMarketingCenterHistory.setOperator(operator);
        telMarketingCenterHistory.setTelMarketingCenter(telMarketingCenter);
        telMarketingCenterHistory.setDealResult("修改跟进人");
        telMarketingCenterHistory.setCreateTime(new Date());
        telMarketingCenterHistory.setComment((oldOperator != null ? oldOperator.getName() : "无") + "->" + newOperator.getName());
        telMarketingCenterHistory.setType(5);
        return telMarketingCenterHistory;
    }

    public DataTablePageViewModel<TelMarketingCenterViewModel> findDataByOperator(Long operatorId, int pageNo, int pageSize, Integer draw, Long status) {
        int startIndex = (pageNo - 1) * pageSize;
        List<TelMarketingCenterViewModel> normalList = new ArrayList<>();
        List<TelMarketingCenter> tmkcList = null;
        long totalElements = 0L;
        if (status != null) {
            tmkcList = telMarketingCenterRepository.findPageByOperatorIdAndStatus(operatorId, status, startIndex, pageSize);
            totalElements = telMarketingCenterRepository.countByOperatorAndStatus(operatorId, status);
        } else {
            tmkcList = telMarketingCenterRepository.findPageByOperatorId(operatorId, startIndex, pageSize);
            totalElements = telMarketingCenterRepository.countByOperator(operatorId);
        }
        tmkcList.each { tmr -> normalList.add(TelMarketingCenterViewModel.createViewModel(tmr, resourceService)) };

        return new DataTablePageViewModel<TelMarketingCenterViewModel>(totalElements, totalElements, draw, normalList);
    }

    /**
     * 获取该用户的待分配信息
     *
     * @return
     */
    public Map<String, String> getUserAssignInfo() {
        InternalUser currentInternalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        Map<String, String> resultMap = new HashMap<>();

        Role roleType = this.getRoleType(currentInternalUser);//1 电销经理, 2 电销主管 , 3 普通用户
        Integer assignNum = 0;
        if (roleType == null) {
            //普通用户
        } else if (roleType.getId().equals(Role.Enum.INTERNAL_USER_ROLE_TEL_MANAGER.getId())) {
            assignNum = telMarketingCenterRepository.countByOperatorIsNull();
        } else if (roleType.getId().equals(Role.Enum.INTERNAL_USER_ROLE_TEL_MASTER.getId())) {
            assignNum = telMarketingCenterRepository.countByOperatorAndDisplay(currentInternalUser.getId());
        }

        resultMap.put("userName", currentInternalUser.getName());
        resultMap.put("assignNum", assignNum + "");
        logger.info("获取当前用户的待分配信息,当前用户可以分配的数据 {} 条.", assignNum);
        return resultMap;
    }

    public Role getRoleType(InternalUser internalUser) {
        Role roleType = null;//1 电销经理, 2 电销主管 , 3 普通用户
        Integer masterNum = 0;
        List<InternalUserRole> rolesByInternalUser = internalUserRoleService.getRolesByInternalUser(internalUser);
        for (InternalUserRole userRole : rolesByInternalUser) {
            if (userRole.getRole().getId().equals(Role.Enum.INTERNAL_USER_ROLE_TEL_MANAGER.getId())) {
                return Role.Enum.INTERNAL_USER_ROLE_TEL_MANAGER;
            } else if (userRole.getRole().getId().equals(Role.Enum.INTERNAL_USER_ROLE_TEL_MASTER.getId())) {
                masterNum++;
            }
        }
        if (masterNum > 0) {
            roleType = Role.Enum.INTERNAL_USER_ROLE_TEL_MASTER;
        }
        return roleType;
    }

    public Map<String, String> paramSearch(TelMarketingCenterRequestParams params) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        InternalUser currentInternalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        Role roleType = this.getRoleType(currentInternalUser);
        InternalUser userTemp = currentInternalUser;
        if (roleType == null) {//是普通用户
            logger.info("普通用户没有此权限!");
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "普通用户没有此权限!");
        } else if (roleType.getId().equals(Role.Enum.INTERNAL_USER_ROLE_TEL_MANAGER.getId())) {
            userTemp = null;
        }
        logger.info("根据条件筛选数据,当前用户的角色为:[{}]", roleType.getName());
        Page<TelMarketingCenter> assignDataPage = telMarketingCenterManageService.findAssignDataByUserAndParam(userTemp, params, null);
        List<String> idList = DataPriorityFiltering(assignDataPage, params, currentInternalUser);
        resultMap.put("resultNum", idList.size() + "");
        return resultMap;
    }

    /**
     * 根据数据优先级进行筛选
     * @param assignDataPage
     * @param params
     * @param currentInternalUser
     * @return
     */
    List<String> DataPriorityFiltering(Page<TelMarketingCenter> assignDataPage, TelMarketingCenterRequestParams params, InternalUser currentInternalUser) {
        List<String> idList = new ArrayList<>(assignDataPage.getContent().size());
        if (params.getTelTypes() == null || params.getTelTypes().length == 0) {
            for (TelMarketingCenter telMarketingCenter : assignDataPage) {
                idList.add(telMarketingCenter.getId() + "")
            }
            logger.info("根据条件筛选到符合的结果数量: {}条", idList.size());
            CacheUtil.putValueWithExpire(redisTemplate, currentInternalUser.getId() + "assign", idList, 30, TimeUnit.MINUTES)
            return idList
        } else {
            for (TelMarketingCenter tmc : assignDataPage) {
                Date yearFirstDay = DateUtils.getYearFirstDay(tmc.getCreateTime())
                Date yearLastDay = getYearLast(tmc.getCreateTime())
                List<TelMarketingCenterRepeat> telMarketingCenterRepeatList = telMarketingCenterRepeatRepository.findRepeatByMobile(tmc.getMobile(), yearFirstDay, yearLastDay)
                List sourceList = new ArrayList()
                for (TelMarketingCenterRepeat tmcr : telMarketingCenterRepeatList) {
                    sourceList.add(tmcr.getSource().getType())
                }
                if (sourceList.contains(5)) {
                    if (Arrays.asList(params.getTelTypes()).contains(5 + "")) {
                        idList.add(tmc.getId() + "") //续保
                    }
                } else if (sourceList.contains(6)) {
                    if (Arrays.asList(params.getTelTypes()).contains(6 + "")) {
                        idList.add(tmc.getId() + "") //未支付订单
                    }
                } else if (sourceList.contains(8)) {
                    if (Arrays.asList(params.getTelTypes()).contains(8 + "")) {
                        idList.add(tmc.getId() + "") //未成单订单
                    }
                } else if (sourceList.contains(1)) {
                    if (Arrays.asList(params.getTelTypes()).contains(1 + "")) {
                        idList.add(tmc.getId() + "") //预约
                    }
                } else if (sourceList.contains(10)) {
                    if (Arrays.asList(params.getTelTypes()).contains(10 + "")) {
                        idList.add(tmc.getId() + "") //报价
                    }
                } else if (sourceList.contains(3)) {
                    if (Arrays.asList(params.getTelTypes()).contains(3 + "")) {
                        idList.add(tmc.getId() + "") //客服转报价
                    }
                } else if (sourceList.contains(9)) {
                    if (Arrays.asList(params.getTelTypes()).contains(9 + "")) {
                        idList.add(tmc.getId() + "") //退款订单
                    }
                } else if (sourceList.contains(2)) {
                    if (Arrays.asList(params.getTelTypes()).contains(2 + "")) {
                        idList.add(tmc.getId() + "") //活动
                    }
                } else if (sourceList.contains(11)) {
                    if (Arrays.asList(params.getTelTypes()).contains(11 + "")) {
                        idList.add(tmc.getId() + "") //登陆用户
                    }
                } else if (sourceList.contains(4)) {
                    if (Arrays.asList(params.getTelTypes()).contains(4 + "")) {
                        idList.add(tmc.getId() + "") //注册无行为
                    }
                } else {
                    if (Arrays.asList(params.getTelTypes()).contains(7 + "")) {
                        idList.add(tmc.getId() + "") //人工导入
                    }
                }
            }
            logger.info("根据条件筛选到符合的结果数量: {}条", idList.size())
            CacheUtil.putValueWithExpire(redisTemplate, currentInternalUser.getId() + "assign", idList, 30, TimeUnit.MINUTES)
            return idList
        }
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    Date getYearLast(Date createTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createTime);
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date yearlastDay = calendar.getTime();
        return yearlastDay;
    }

}
