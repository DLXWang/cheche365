package com.cheche365.cheche.admin.web.controller.role;

import com.cheche365.cheche.admin.service.RoleAndPermissionChecker;
import com.cheche365.cheche.admin.service.role.RoleManagementService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.admin.web.model.role.RoleViewModel;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.RolePermission;
import com.cheche365.cheche.core.model.RoleType;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by guoweifu on 2015/9/11.
 */
@RestController
@RequestMapping("/admin/roles")
public class RoleManagementController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String INTERNAL_USER = "INTERNAL_USER";
    private final static String EXTERNAL_USER = "EXTERNAL_USER";
    @Autowired
    private RoleManagementService roleManagementService;

    @Autowired
    private RoleAndPermissionChecker checker;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VisitorPermission("ad0401")
    public PageViewModel<RoleViewModel> findAll(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                @RequestParam(value = "keyword", required = false) String keyword) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list role info, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list role info, pageSize can not be null or less than 1");
        }

        Page<Role> page = this.roleManagementService.findAllRoles(currentPage, pageSize, keyword);
        return createPageViewResult(page);
    }

    /**
     * 启用或禁用角色
     *
     * @param roleId
     * @param disable，0-启用，1-禁用
     * @return
     */
    @RequestMapping(value = "/{roleId}/{disable}", method = RequestMethod.PUT)
    @VisitorPermission("ad040103")
    public ResultModel changeStatus(@PathVariable Long roleId, @PathVariable Integer disable) {
        if (roleId == null || roleId < 1)
            throw new FieldValidtorException("changeStatus role, id can not be null or less than 1");

        roleManagementService.changeStatus(roleId, disable);
        return new ResultModel();
    }

    /**
     * 获取执行角色类型的所有角色
     *
     * @param userType
     * @return
     */
    @RequestMapping(value = "/userType", method = RequestMethod.GET)
    public List<RoleViewModel> listRoleByUserType(@RequestParam(value = "userType", required = false) Integer userType) {
        RoleType roleType = null;
        // 内部用户角色
        if (userType == null || userType == 1) {
            roleType = RoleType.Enum.INTERNAL_USER;
        }
        // 外部用户角色
        else {
            roleType = RoleType.Enum.EXTERNAL_USER;
        }
        List<Role> roleList = roleManagementService.listRoleByUserType(roleType);
        return createRoleViewModelList(roleList, roleType);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @VisitorPermission("ad040101")
    public ResultModel addRole(@Valid RoleViewModel model, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return new ResultModel(false, "请将信息填写完整");
        }
        Role role = createRole(model);
        InternalUser currentInternalUser = internalUserManageService.getCurrentInternalUser();
        //新建特殊角色需要超级用户
        checker.checkNewRole(currentInternalUser, role);
        //角色分配权限加校验
        checker.checkAssignedPermissionsToRole(currentInternalUser, model.getPermissions(), role);

        roleManagementService.addRole(model.getPermissions(), role);
        return new ResultModel();
    }

    @RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
    public RoleViewModel getRole(@PathVariable Long roleId) {
        AssertUtil.notNull(roleId, "illegal role id:" + roleId);
        return this.getRoleById(roleId);
    }

    @RequestMapping(value = "/{roleId}", method = RequestMethod.PUT)
    @VisitorPermission("ad040102")
    public ResultModel updateRole(@PathVariable Long roleId, @Valid RoleViewModel model, BindingResult bindingResult) {
        AssertUtil.notNull(roleId, "illegal role id:" + roleId);
        if (bindingResult.hasErrors()) {
            return new ResultModel(false, "请将信息填写完整");
        }
        InternalUser currentInternalUser = internalUserManageService.getCurrentInternalUser();
        Role newRole = createRole(model);

        //新建特殊角色需要超级用户
        checker.checkNewRole(currentInternalUser, newRole);
        //角色分配权限加校验
        checker.checkAssignedPermissionsToRole(currentInternalUser, model.getPermissions(), roleManagementService.findRoleById(roleId));

        roleManagementService.updateRole(model.getPermissions(), newRole);
        return new ResultModel();
    }

    @RequestMapping(value = "/{roleId}/permissions", method = RequestMethod.PUT)
    @VisitorPermission("ad040102")
    public ResultModel updateRolePermissions(@PathVariable Long roleId, String permissions) {
        Role role = roleManagementService.findRoleById(roleId);
        AssertUtil.notNull(role, "can not find role by id:" + roleId);
        //更新角色权限加校验
        checker.checkAssignedPermissionsToRole(internalUserManageService.getCurrentInternalUser(), permissions, role);

        roleManagementService.saveRolePermissions(permissions, role);
        return new ResultModel();
    }

    /**
     * 封装展示层实体
     *
     * @param page 分页信息
     * @return PageViewModel<PageViewData>
     */
    public PageViewModel<RoleViewModel> createPageViewResult(Page page) {
        PageViewModel model = new PageViewModel<RoleViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);

        List<RoleViewModel> pageViewDataList = new ArrayList<>();
        for (Role role : (List<Role>) page.getContent()) {
            RoleViewModel viewData = getViewModel(role);
            pageViewDataList.add(viewData);
        }
        model.setViewList(pageViewDataList);

        return model;
    }

    private RoleViewModel getViewModel(Role role) {
        if (role == null)
            return null;
        RoleViewModel viewModel = new RoleViewModel();
        viewModel.setId(role.getId());
        viewModel.setName(role.getName());//角色名称
        if (role.getType() != null) {
            viewModel.setRoleType(role.getType().getName());//角色分组
        }
        viewModel.setDescription(role.getDescription());//备注
        viewModel.setDisable(role.isDisable());

        return viewModel;
    }

    public List<RoleViewModel> createRoleViewModelList(List<Role> roleList, RoleType roleType) {
        List<RoleViewModel> roleViewModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleList)) {
            for (Role role : roleList) {
                RoleViewModel viewModel = new RoleViewModel();
                viewModel.setId(role.getId());
                viewModel.setName(role.getName());
                viewModel.setRoleType(roleType.getName());
                roleViewModelList.add(viewModel);
            }
        }
        return roleViewModelList;
    }

    public RoleViewModel getRoleById(Long roleId) {
        Role role = roleManagementService.findRoleById(roleId);
        AssertUtil.notNull(role, "can not find role by id:" + roleId);

        RoleViewModel model = new RoleViewModel();
        model.setId(role.getId());
        model.setName(role.getName());
        model.setDescription(role.getDescription());
        model.setPermissions(this.getStrRolePermissions(role));
        model.setRoleType(role.getType().getId() == 2 ? INTERNAL_USER : EXTERNAL_USER);

        return model;
    }

    private Role createRole(RoleViewModel model) {
        Role newRole = new Role();
        if(model.getId() != null) {
            newRole.setId(model.getId());
        }
        newRole.setName(model.getName());
        newRole.setDescription(model.getDescription());
        if (INTERNAL_USER.equals(model.getRoleType())) {
            newRole.setType(RoleType.Enum.INTERNAL_USER);
        } else if (EXTERNAL_USER.equals(model.getRoleType())) {
            newRole.setType(RoleType.Enum.EXTERNAL_USER);
        } else {
            throw new RuntimeException("illegal roleType -> " + model.getRoleType());
        }
        newRole.setLevel(model.getLevel());
        return newRole;
    }

    private String getStrRolePermissions(Role role) {
        if (null == role)
            return "";

        List<RolePermission> rolePermissionList = roleManagementService.findByRole(role);
        if (!CollectionUtils.isEmpty(rolePermissionList)) {
            StringBuffer permissions = new StringBuffer();
            rolePermissionList.forEach(rolePermission -> {
                if (null != rolePermission) {
                    permissions.append(rolePermission.getPermission().getId()).append(",");
                }
            });
            if (StringUtils.isNotBlank(permissions.toString())) {
                return permissions.toString().substring(0, permissions.toString().length() - 1);
            }
        }

        return "";
    }
}
