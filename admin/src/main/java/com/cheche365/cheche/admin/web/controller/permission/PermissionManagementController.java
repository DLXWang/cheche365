package com.cheche365.cheche.admin.web.controller.permission;

import com.cheche365.cheche.admin.constants.ResourceEnum;
import com.cheche365.cheche.admin.service.RoleAndPermissionChecker;
import com.cheche365.cheche.admin.service.permission.PermissionManagementService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.admin.web.model.permission.PermissionViewModel;
import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by liyh on 2015/9/15.
 */
@RestController
@RequestMapping("/admin/permission")
public class PermissionManagementController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PermissionManagementService permissionManagementService;

    @Autowired
    private RoleAndPermissionChecker checker;

    @Autowired
    private InternalUserManageService internalUserManageService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("ad0402")
    public PageViewModel<PermissionViewModel> findAll(@RequestParam(value = "currentPage", required = true) Integer currentPage,
                                                      @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        Page<Permission> page = this.permissionManagementService.findList(currentPage, pageSize, keyword);
        return createPageModelResult(page);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @VisitorPermission("ad040203")
    public ResultModel save(@RequestParam(value = "roleList", required = true) String roleList,
                            @RequestParam(value = "permissionId", required = true) Long permissionId) {

        checker.checkAssignedRolesToPermission(internalUserManageService.getCurrentInternalUser(), roleList,
            permissionManagementService.findOne(permissionId));

        boolean isSuccess = this.permissionManagementService.save(roleList, permissionId);
        if (isSuccess) {
            return new ResultModel();
        } else {
            logger.error("add roleList failed");
            return new ResultModel(false, "添加失败");
        }
    }

    @RequestMapping(value = "/{permissionId}", method = RequestMethod.GET)
    public String getRoleIdByPermission(@PathVariable Long permissionId) {
        return this.permissionManagementService.getRoleIdByPermission(permissionId);
    }

    public PageViewModel<PermissionViewModel> createPageModelResult(Page page) {
        PageViewModel model = new PageViewModel<PermissionViewModel>();

        PageInfo pageInfo = new PageInfo();
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPage(page.getTotalPages());
        model.setPageInfo(pageInfo);

        Map<Long, Resource> resourceMap = permissionManagementService.listResource();
        List<PermissionViewModel> pageViewDataList = new ArrayList<>();
        for (Permission permission : (List<Permission>) page.getContent()) {
            pageViewDataList.add(this.createViewData(permission,resourceMap));
        }
        model.setViewList(pageViewDataList);

        return model;
    }

    /**
     * 构建页面数据
     *
     * @param permission
     * @return list
     */
    public PermissionViewModel createViewData(Permission permission,Map<Long,Resource> resourceMap) {
        List<Resource> resourceList = permission.getResources();
        PermissionViewModel viewModel = new PermissionViewModel();
        viewModel.setId(permission.getId());
        viewModel.setName(permission.getName());//权限名称
        if (resourceList.size() > 0) {
            Resource resource = resourceList.get(0);
            Resource firstResource = new Resource();
            Resource secondResource = new Resource();
            Resource thirdResource = new Resource();
            int level = resource.getLevel();
            if(level == ResourceEnum.ThirdLevel.getIndex()){
                thirdResource.setName(resource.getName());
                secondResource = resourceMap.get(resource.getParent().getId());
                firstResource = resourceMap.get(secondResource.getParent().getId());
            }else if(level == ResourceEnum.SecondLevel.getIndex()){
                firstResource = resourceMap.get(resource.getParent().getId());
                secondResource.setName(resource.getName());
            }else if(level == ResourceEnum.FirstLevel.getIndex()){
                firstResource.setName(resource.getName());
            }
            viewModel.setThirdMenu(thirdResource.getName());
            viewModel.setSecondMenu(secondResource.getName());
            viewModel.setFirstMenu(firstResource.getName());
        }
        return viewModel;
    }
}
