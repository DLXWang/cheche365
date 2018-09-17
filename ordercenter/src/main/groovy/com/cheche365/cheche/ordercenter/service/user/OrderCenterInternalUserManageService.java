package com.cheche365.cheche.ordercenter.service.user;


import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.HashUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InternalUserRelationRepository;
import com.cheche365.cheche.core.repository.InternalUserRepository;
import com.cheche365.cheche.core.repository.InternalUserRoleRepository;
import com.cheche365.cheche.core.repository.RolePermissionRepository;
import com.cheche365.cheche.core.service.DoubleDBService;
import com.cheche365.cheche.core.service.InternalUserRoleService;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.core.service.SecurityService;
import com.cheche365.cheche.manage.common.security.ManageCommonSecurityUser;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.manage.common.web.model.ModelAndViewResult;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.ordercenter.constants.OrderCenterRedisConstants;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.*;
/**
 * Created by sunhuazhong on 2015/5/7.
 */
@Service("orderCenterInternalUserManageService")
@Transactional
public class OrderCenterInternalUserManageService extends InternalUserManageService implements IInternalUserManageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InternalUserRoleRepository internalUserRoleRepository;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private InternalUserRelationRepository internalUserRelationRepository;

    @Autowired
    private DoubleDBService doubleDBService;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private SecurityService securityService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private InternalUserRoleService internalUserRoleService;

    /**
     * add new internalUser and role
     *
     * @param viewData
     * @return
     */
    @Override
    public ModelAndViewResult add(InternalUserViewData viewData) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            // 判断该邮箱是否已被使用
            if (internalUserRepository.findFirstByEmailAndDisable(viewData.getEmail(), false) != null) {
                return new ModelAndViewResult(ModelAndViewResult.RESULT_FAIL, "邮箱已被注册");
            }
            if (internalUserRepository.findFirstByEmail(viewData.getEmail()) != null) {
                viewData.setId(internalUserRepository.findFirstByEmail(viewData.getEmail()).getId());
                viewData.setDisable(0);
            }
            // 保存内部用户
            InternalUser internalUser = internalUserRepository.save(this.createInternalUser(viewData));
            // 保存内部用户的角色
            this.createInternalUserRole(viewData.getRoleIds(), internalUser);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setMessage("添加成功。");
        } catch (Exception ex) {
            logger.error("add internal user has error", ex);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
        }
        return result;
    }

    /**
     * create internalUser db model
     *
     * @param viewData
     * @return
     * @throws Exception
     */
    private InternalUser createInternalUser(InternalUserViewData viewData) throws Exception {
        InternalUser internalUser = new InternalUser();
        if (viewData.getId() != null) {
            internalUser = internalUserRepository.findOne(viewData.getId());
        } else {
            internalUser.setInternalUserType(1);
        }
        internalUser.setEmail(viewData.getEmail());
        internalUser.setMobile(viewData.getMobile());
        internalUser.setName(viewData.getName());
        if (viewData.getPassword() != null && !viewData.getPassword().isEmpty()) {
            internalUser.setPassword(HashUtils.getMD5(viewData.getPassword()));
        }
        if (viewData.getGender() == 1) {
            internalUser.setGender(Gender.Enum.MALE);
        } else if (viewData.getGender() == 2) {
            internalUser.setGender(Gender.Enum.FEMALE);
        }
        if (viewData.getDisable() != null && viewData.getDisable() == 0) {
            internalUser.setDisable(false);
        } else if (viewData.getDisable() != null && viewData.getDisable() == 1) {
            internalUser.setDisable(true);
        }

        if (internalUser.getCreateTime() == null) {
            internalUser.setCreateTime(Calendar.getInstance().getTime());
        }
        internalUser.setUpdateTime(Calendar.getInstance().getTime());

        return internalUser;
    }

    private void createInternalUserRole(String roleIds, InternalUser internalUser) throws Exception {
        internalUserRoleService.saveByStrRoleIdList(internalUser, roleIds);
    }

    @Override
    public ModelAndViewResult update(InternalUserViewData internalUserViewData) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            InternalUser originalInternalUser = internalUserRepository.findOne(internalUserViewData.getId());
            InternalUser emailInternalUser = internalUserRepository.findFirstByEmail(internalUserViewData.getEmail());
            // 判断该邮箱是否已被使用
            if (emailInternalUser != null
                && !internalUserViewData.getEmail().equals(originalInternalUser.getEmail())) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("该邮箱已被使用，请重新填写。");
                return result;
            }
            // 保存内部用户
            InternalUser internalUser = internalUserRepository.save(this.createInternalUser(internalUserViewData));
            // 删除该内部用户出单中心的所有角色
            internalUserRoleService.deletePartInternalUserRoles(internalUser, Permission.Enum.ORDER_CENTER);
            // 保存内部用户的角色
            this.createInternalUserRole(
                internalUserViewData.getRoleIds(), originalInternalUser);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setMessage("更新成功。");
            return result;
        } catch (Exception e) {
            logger.error("update internal user has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
            return result;
        }
    }

    public ModelAndViewResult modifyPasswordInOrderCenter(Long id, String password) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            InternalUser internalUser = internalUserRepository.findOne(id);
            internalUser.setPassword(HashUtils.getMD5(password));
            internalUser.setChangePasswordTime(new Date());
            internalUserRepository.save(internalUser);
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(OrderCenterRedisConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
            if (isMember) {
                stringRedisTemplate.opsForSet().remove(OrderCenterRedisConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
            }

            stringRedisTemplate.opsForSet().remove(OrderCenterRedisConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
            stringRedisTemplate.opsForHash().delete(OrderCenterRedisConstants.USER_LOCK_KEY, internalUser.getEmail());
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setMessage("重置密码成功。");
            return result;
        } catch (Exception e) {
            logger.error("update internal user password has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
            return result;
        }
    }

    @Override
    public ModelAndViewResult delete(Long id) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            if (id == null) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("无效参数。");
                return result;
            }

            InternalUser internalUser = internalUserRepository.findOne(id);
            if (internalUser == null) {
                result.setResult(ModelAndViewResult.RESULT_FAIL);
                result.setMessage("该内部用户不存在。");
                return result;
            }

            // 删除内部用户，删除用户组，角色，设置不可用标记
            deleteInternalUser(internalUser);

            // 保存删除内部用户日志
            createDeleteInternalUserApplicationLog(internalUser);

            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            result.setMessage("删除成功。");
        } catch (Exception e) {
            logger.error("delete internal user by id has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
        }
        return result;
    }

    private void deleteInternalUser(InternalUser internalUser) {
        // 删除该内部用户的用户组
        List<InternalUserRelation> internalUserRelations = internalUserRelationRepository.findByCustomerUser(internalUser);
        if (!CollectionUtils.isEmpty(internalUserRelations)) {
            internalUserRelationRepository.delete(internalUserRelations);
        }

        // 删除该内部用户的角色
        List<InternalUserRole> internalUserRoles = internalUserRoleRepository.findByInternalUser(internalUser);
        if (!CollectionUtils.isEmpty(internalUserRoles)) {
            internalUserRoleRepository.delete(internalUserRoles);
        }

        // 修改该内部用户的标记
        internalUser.setDisable(true);
        if (internalUser.getCreateTime() == null) {
            internalUser.setCreateTime(Calendar.getInstance().getTime());
        }
        internalUser.setUpdateTime(Calendar.getInstance().getTime());
        internalUserRepository.save(internalUser);
    }

    private void createDeleteInternalUserApplicationLog(InternalUser internalUser) {
        MoApplicationLog applicationLog = new MoApplicationLog();
        applicationLog.setLogLevel(2);//日志的级别 1debug  2info 3warn 4error
        applicationLog.setLogMessage("删除内部用户，删除该用户的用户组和角色，设置不可用标记");//日志信息
        applicationLog.setLogType(LogType.Enum.DELETE_INTERNAL_USER_10);//删除内部用户
        applicationLog.setObjId(internalUser.getId() + "");//对象id
        applicationLog.setObjTable("internal_user");//对象表名
        applicationLog.setOpeartor(getCurrentInternalUser().getId());//操作人
        applicationLog.setCreateTime(Calendar.getInstance().getTime());//创建时间
        doubleDBService.saveApplicationLog(applicationLog);
    }

    @Override
    public ModelAndViewResult findOneById(Long id) {
        ModelAndViewResult result = new ModelAndViewResult();
        try {
            InternalUser internalUser = internalUserRepository.findOne(id);
            InternalUserViewData viewData = this.createViewData(internalUser);
            result.setResult(ModelAndViewResult.RESULT_SUCCESS);
            Map<String, Object> objects = new HashMap<>();
            objects.put("model", viewData);
            result.setObjectMap(objects);
        } catch (Exception e) {
            logger.error("find internal user by id has error", e);
            result.setResult(ModelAndViewResult.RESULT_FAIL);
            result.setMessage("系统异常。");
        }
        return result;
    }

    /**
     * find all user String <option></option>
     *
     * @return
     */
    @Override
    public Map<String, String> findAllUsers() {
        Map<String, String> map = new HashMap<>();
        try {
            List<InternalUser> internalUserList = internalUserRepository.findByDisable(false);
            map.put("options", this.createUserString(internalUserList));
            return map;
        } catch (Exception e) {
            logger.error("find all user has error", e);
        }

        return map;
    }

    /**
     * 遍历所有可用角色user
     *
     * @return
     */
    @Override
    public Map<String, String> findAllRoleUsers() {
        Map<String, String> map = new HashMap<>();
        try {
            List<Long> internalUserIdList = new ArrayList<>();
            // 具有客服角色的内部用户
            List<InternalUserRole> customerUserRoleList = internalUserRoleRepository.findByRole(Role.Enum.INTERNAL_USER_ROLE_CUSTOMER);
            List<InternalUser> customerUserList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(customerUserRoleList)) {
                customerUserRoleList.forEach(internalUserRole -> {
                    if (!internalUserRole.getInternalUser().isDisable()
                        && !internalUserIdList.contains(internalUserRole.getInternalUser().getId())) {
                        customerUserList.add(internalUserRole.getInternalUser());
                        internalUserIdList.add(internalUserRole.getInternalUser().getId());
                    }
                });
            }
            internalUserIdList.clear();
            // 具有内勤角色的内部用户
            List<InternalUserRole> internalUserRoleList = internalUserRoleRepository.findByRole(Role.Enum.INTERNAL_USER_ROLE_INTERNAL);
            List<InternalUser> internalUserList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(internalUserRoleList)) {
                internalUserRoleList.forEach(internalUserRole -> {
                    if (!internalUserRole.getInternalUser().isDisable()
                        && !internalUserIdList.contains(internalUserRole.getInternalUser().getId())) {
                        internalUserList.add(internalUserRole.getInternalUser());
                        internalUserIdList.add(internalUserRole.getInternalUser().getId());
                    }
                });
            }
            internalUserIdList.clear();
            // 具有外勤角色的内部用户
            List<InternalUserRole> externalUserRoleList = internalUserRoleRepository.findByRole(Role.Enum.INTERNAL_USER_ROLE_EXTERNAL);
            List<InternalUser> externalUserList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(externalUserRoleList)) {
                externalUserRoleList.forEach(internalUserRole -> {
                    if (!internalUserRole.getInternalUser().isDisable()
                        && !internalUserIdList.contains(internalUserRole.getInternalUser().getId())) {
                        externalUserList.add(internalUserRole.getInternalUser());
                        internalUserIdList.add(internalUserRole.getInternalUser().getId());
                    }
                });
            }
            map.put("customerOptions", this.createUserString(customerUserList));
            map.put("internalOptions", this.createUserString(internalUserList));
            map.put("externalOptions", this.createUserString(externalUserList));
            return map;
        } catch (Exception e) {
            logger.error("find all user of role has error", e);
        }

        return map;
    }

    /**
     * create user option
     *
     * @param internalUserList
     * @return
     * @throws Exception
     */
    private String createUserString(List<InternalUser> internalUserList) throws Exception {
        if (internalUserList == null) {
            return "";
        }

        StringBuffer buffer = new StringBuffer("<option value=''>请选择</option>");

        for (InternalUser internalUser : internalUserList) {
            if (!"system".equals(internalUser.getName())) {
                buffer.append("<option value='" + internalUser.getId() + "'>" + internalUser.getName() + "</option>");
            }
        }

        return buffer.toString();
    }

    @Override
    public InternalUser getCurrentInternalUserOrSystem() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return InternalUser.ENUM.SYSTEM;
        }
        ManageCommonSecurityUser user = (ManageCommonSecurityUser) authentication.getPrincipal();
        return user.getInternalUser();
    }

    @Override
    public InternalUserViewData getCurrentUser() {
        InternalUser internalUser = this.getCurrentInternalUser();
        return this.createViewDataWithPermission(internalUser);
    }

    public DataTablePageViewModel<InternalUserViewData> listInternalUser(PublicQuery query) {
        try {
            List<BigInteger> orderCenterLoginRoleIdList = rolePermissionRepository.findOrderCenterEnableLoginRole(Permission.Enum.ORDER_CENTER.getId());
            if (CollectionUtils.isEmpty(orderCenterLoginRoleIdList)) {
                return null;
            }
            Page<InternalUser> page = findBySpecAndPaginate(this.buildPageable(query.getCurrentPage(), query.getPageSize(),
                Sort.Direction.DESC, this.SORT_CREATE_TIME), query, orderCenterLoginRoleIdList);
            List<InternalUserViewData> dataList = new ArrayList<>();
            page.getContent().forEach(internalUser -> dataList.add(createViewData(internalUser)));
            PageInfo pageInfo = this.createPageInfo(page);
            return new DataTablePageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), dataList);
        } catch (Exception e) {
            logger.error("list internal user has error", e);
        }
        return null;
    }

    private Page<InternalUser> findBySpecAndPaginate(Pageable pageable, PublicQuery publicQuery, List<BigInteger> orderCenterLoginRoleIdList) {
        return internalUserRepository.findAll((root, query, cb) -> {

            Root<InternalUserRole> internalUserRoleRoot = query.from(InternalUserRole.class);
            CriteriaQuery<InternalUser> criteriaQuery = cb.createQuery(InternalUser.class);
            query.distinct(true);

            Path<Long> internalUserIdPath = internalUserRoleRoot.get("internalUser").get("id");
            Path<Long> internalUserRolePath = internalUserRoleRoot.get("role").get("id");
            Path<Long> internalUserPath = root.get("id");
            Path<Boolean> disablePath = root.get("disable");
            Path<String> namePath = root.get("name");

            //条件构造
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(publicQuery.getKeyword())) {
                Path<String> mobilePath = root.get("mobile");
                predicateList.add(cb.or(
                    cb.like(namePath, publicQuery.getKeyword() + "%"),
                    cb.like(mobilePath, publicQuery.getKeyword() + "%")
                ));
            }
            predicateList.add(cb.equal(internalUserIdPath, internalUserPath));
            CriteriaBuilder.In<Long> roleIdIn = cb.in(internalUserRolePath);
            for (BigInteger roleId : orderCenterLoginRoleIdList) {
                roleIdIn.value(roleId.longValue());
            }
            predicateList.add(roleIdIn);

            //出单中心不显示超级用户
            Path<String> emailPath = root.get("email");
            predicateList.add(cb.notEqual(emailPath, InternalUserService.EMAIL_SUPERMAN));

            predicateList.add(cb.notEqual(namePath, "system"));
            predicateList.add(cb.notEqual(disablePath, Boolean.TRUE));

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageable);
    }

    /**
     * create return view body list
     *
     * @param internalUserList
     * @return
     * @throws Exception
     */
    public List<InternalUserViewData> createList(List<InternalUser> internalUserList) throws Exception {

        super.createList(internalUserList);

        List<InternalUserViewData> internalUserViewDataList = new ArrayList<>();
        for (InternalUser internalUser : internalUserList) {
            internalUserViewDataList.add(this.createViewData(internalUser));
        }
        return internalUserViewDataList;
    }

    /**
     * organize UserViewData for show
     *
     * @param internalUser
     * @return
     * @throws Exception
     */
    private InternalUserViewData createViewData(InternalUser internalUser) {
        List<InternalUserRole> internalUserRoles = internalUserRoleRepository.findByInternalUser(internalUser);
        InternalUserViewData viewData = new InternalUserViewData();
        viewData.setId(internalUser.getId());
        viewData.setEmail(internalUser.getEmail());
        viewData.setName(internalUser.getName());
        viewData.setMobile(internalUser.getMobile());
        viewData.setRoleIds(internalUserRoleService.getStrRoleProperty(internalUserRoles, InternalUserRoleService.ROLE_PROPERTY_ID));
        viewData.setRoleName(internalUserRoleService.getStrRoleProperty(internalUserRoles, InternalUserRoleService.ROLE_PROPERTY_NAME));
        viewData.setGender(internalUser.getGender() == null ? null : internalUser.getGender().getId());
        viewData.setCreateTime(DateUtils.getDateString(
            internalUser.getCreateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        viewData.setUpdateTime(internalUser.getUpdateTime() == null ? "" : DateUtils.getDateString(
            internalUser.getUpdateTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(OrderCenterRedisConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
        viewData.setResetPasswordFlag(isMember);
        return viewData;
    }

    private InternalUserViewData createViewDataWithPermission(InternalUser internalUser) {
        InternalUserViewData viewData = new InternalUserViewData();
        viewData.setId(internalUser.getId());
        viewData.setEmail(internalUser.getEmail());
        viewData.setName(internalUser.getName());
        viewData.setMobile(internalUser.getMobile());
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(OrderCenterRedisConstants.RESET_PASSWORD_KEY, internalUser.getEmail());
        viewData.setResetPasswordFlag(isMember);
        Boolean resetLockPassword = stringRedisTemplate.opsForSet().isMember(OrderCenterRedisConstants.RESET_PASSWORD_LOCK_KEY, internalUser.getEmail());
        viewData.setResetPasswordLockFlag(resetLockPassword);
        viewData.setPermissionCode(String.join(",", listAuthority()));
        viewData.setAbleCall(isAbleCall(internalUser));
        return viewData;
    }

    @Override
    public List<String> listRole() {
        InternalUser internalUser = getCurrentInternalUser();
        List<String> roleNames = new ArrayList<>();
        List<Role> roles = securityService.getRoles(internalUser);
        for (Role role : roles) {
            if (!roleNames.contains(role.getName())) {
                roleNames.add(role.getName());
            }
        }
        return roleNames;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        List<String> permissionCodeList = listAuthority();
        return permissionCodeList.contains(permission.getCode());
    }

    @Override
    public boolean config(Long internalUserId) {
        try {
            // 内部用户
            InternalUser internalUser = internalUserRepository.findOne(internalUserId);
            // 判断该内部用户是否已经配置了角色
            Role role = Role.Enum.INTERNAL_USER_ROLE_STATUS_CHANGE;
            InternalUserRole internalUserRole = internalUserRoleRepository.findFirstByInternalUserAndRole(internalUser, role);
            if (internalUserRole == null) {
                internalUserRole = new InternalUserRole();
                internalUserRole.setInternalUser(internalUser);
                internalUserRole.setRole(role);
                internalUserRoleRepository.save(internalUserRole);
            } else {
                internalUserRoleRepository.delete(internalUserRole);
            }
            return true;
        } catch (Exception e) {
            logger.error("config user has error", e);
        }

        return false;
    }

    public boolean isAbleCall(InternalUser internalUser) {
        TelMarketer marketer = internalUser.getTelMarketer();
        return marketer != null;
    }
}
