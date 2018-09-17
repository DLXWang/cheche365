package com.cheche365.cheche.admin.web.controller.resource;

import com.cheche365.cheche.admin.service.permission.PermissionManagementService;
import com.cheche365.cheche.admin.service.resource.MenuResource;
import com.cheche365.cheche.admin.web.model.permission.PermissionViewModel;
import com.cheche365.cheche.admin.web.model.resource.MenusViewModel;
import com.cheche365.cheche.admin.web.model.resource.ResourceViewModel;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by wangfei on 2015/6/8.
 */
@RestController
@RequestMapping("/admin/resources")
public class ResourceController {

    @Autowired
    private MenuResource menuResource;

    @Autowired
    private PermissionManagementService permissionManagementService;

    @RequestMapping(value = "/menus",method = RequestMethod.GET)
    public MenusViewModel getAllMenus() {
        List<Resource> resourceList = menuResource.listAllMenus();
        return createViewModel(resourceList, null, null);
    }

    @RequestMapping(value = "/menus/{resourceId}",method = RequestMethod.GET)
    public MenusViewModel getChangedMenus(@PathVariable Long resourceId, Integer level) {
        Resource current = menuResource.findOne(resourceId);
        Resource parent = null;
        if (current != null)
            parent = current.getParent();

        List<Resource> resourceList = menuResource.changeMenu(resourceId, level);
        return createViewModel(resourceList, current, parent);
    }

    @RequestMapping(value = "/{level}/permission",method = RequestMethod.GET)
    public MenusViewModel getPermissionsByLevel(@PathVariable Integer level) {
        MenusViewModel model = new MenusViewModel();
        List<Permission> permissionList = permissionManagementService.findByLevel(level);
        List<PermissionViewModel> permissionViewModelList = new LinkedList<>();
        permissionList.forEach(permission ->
            permissionViewModelList.add(menuResource.createPermissionView(permission, permission.getResources().get(0)))
        );
        model.setPermissions(permissionViewModelList);
        return model;
    }

    public MenusViewModel createViewModel(List<Resource> resourceList, Resource current, Resource parent) {
        if (null == resourceList)
            return null;

        MenusViewModel menusModel = new MenusViewModel();
        List<ResourceViewModel> firstMenuList = new ArrayList<>();
        List<ResourceViewModel> secondMenuList = new ArrayList<>();
        List<ResourceViewModel> thirdMenuList = new ArrayList<>();

        resourceList.forEach(resource -> {
            ResourceViewModel model = this.copyResourceProperties(resource);
            MenuResource.Level level = MenuResource.Level.format(resource.getLevel());
            switch (level) {
                case LEVEL_1:
                    firstMenuList.add(model);
                    break;
                case LEVEL_2:
                    secondMenuList.add(model);
                    break;
                case LEVEL_3:
                    thirdMenuList.add(model);
                    break;
                default:
                    throw new IllegalArgumentException("illegal level type -> " + level);
            }
        });

        menusModel.setFirstMenus(firstMenuList);
        menusModel.setSecondMenus(secondMenuList);
        menusModel.setThirdMenus(thirdMenuList);
        menusModel.setCurrent(this.copyResourceProperties(current));
        menusModel.setParent(this.copyResourceProperties(parent));
        menusModel.setPermissions(menuResource.getThirdMenuPermissions(menuResource.getPermissionByMenus(current, parent)));

        return menusModel;
    }
    private ResourceViewModel copyResourceProperties(Resource resource) {
        if (null == resource)
            return null;

        ResourceViewModel target = new ResourceViewModel();
        String[] contains = new String[]{"id", "name", "level"};
        BeanUtil.copyPropertiesContain(resource, target, contains);
        return target;
    }
}
