package com.cheche365.cheche.admin.service.resource;

import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import com.cheche365.cheche.admin.web.model.permission.PermissionViewModel;
import com.cheche365.cheche.core.model.Permission;
import com.cheche365.cheche.core.model.Resource;
import com.cheche365.cheche.core.model.ResourceType;
import com.cheche365.cheche.core.repository.ResourceRepository;
import com.cheche365.cheche.core.service.InternalUserService;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfei on 2015/9/11.
 */
@Component
public class MenuResource extends BaseService<Resource, Object> {
    private Logger logger = LoggerFactory.getLogger(MenuResource.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private InternalUserManageService internalUserManageService;

    public List<Resource> listAllMenus() {
        return resourceRepository.findByResourceType(ResourceType.Enum.RESOURCE_MENU);
    }


    public Resource findOne(Long resourceId) {
        return resourceRepository.findOne(resourceId);
    }

    /**
     * 获取需要刷新的菜单
     * @param current
     * @param parent
     * @return
     */
    public List<Resource> getPermissionByMenus(Resource current, Resource parent) {
        //全部
        if (null == current) {
            //全部
            if (null == parent) {
                List<Resource> resourceList = new ArrayList<>();
                //绑定一级菜单的三级目录
                List<Resource> resourceList1 = resourceRepository.findByResourceTypeAndLevel(ResourceType.Enum.RESOURCE_MENU, 1);
                if (!CollectionUtils.isEmpty(resourceList1))
                    resourceList.addAll(resourceList1);
                //三级菜单
                List<Resource> resourceList3 = resourceRepository.findByResourceTypeAndLevel(ResourceType.Enum.RESOURCE_MENU, 3);
                if (!CollectionUtils.isEmpty(resourceList3))
                    resourceList.addAll(resourceList3);

                return resourceList;
            } else {
                //指定父级目录下的三级目录
                return this.getThirdMenus(parent);
            }
        } else {
            //指定目录下的三级目录
            return this.getThirdMenus(current);
        }
    }

    /**
     * 获取指定资源下的目录
     * @param resource
     * @return
     */
    public List<Resource> getThirdMenus(Resource resource) {
        if (null == resource)
            return null;

        List<Resource> resources = new ArrayList<>();
        Level level = Level.format(resource.getLevel());
        List<Resource> secondResources;
        List<Resource> thirdResources;
        switch (level) {
            case LEVEL_1://一级菜单改变，二级和三级菜单都需要刷新
                if (!CollectionUtils.isEmpty(resource.getPermissions())) {
                    resources.add(resource);
                }
                secondResources = resourceRepository.findByParent(resource);
                if (!CollectionUtils.isEmpty(secondResources)) {
                    resources.addAll(secondResources);
                    thirdResources = resourceRepository.findByParentIn(secondResources);
                    if (!CollectionUtils.isEmpty(thirdResources)) {
                        resources.addAll(thirdResources);
                    }
                }
                break;
            case LEVEL_2://二级菜单改变，一级和二级不用变，三级菜单需要刷新
                thirdResources = resourceRepository.findByParent(resource);
                if (!CollectionUtils.isEmpty(thirdResources)) {
                    resources.addAll(thirdResources);
                }
                break;
            case LEVEL_3://三级菜单，不需要刷新
                resources.add(resource);
                break;
            default:
                throw new IllegalArgumentException("illegal level type -> " + level);
        }

        return resources;
    }

    /**
     * 获取三级目录下的权限集合
     * @param resourceList
     * @return
     */
    public List<PermissionViewModel> getThirdMenuPermissions(List<Resource> resourceList) {
        if (null == resourceList)
            return null;

        List<PermissionViewModel> models = new ArrayList<>();
        resourceList.forEach(resource -> {
            if (!CollectionUtils.isEmpty(resource.getPermissions())) {
                List<Permission> permissionList = resource.getPermissions();
                if (!CollectionUtils.isEmpty(permissionList)) {
                    permissionList.forEach(permission -> {
                        //特殊权限不在列表中显示，另外有链接触发
                        if (permission.getLevel() == 0) {
                            models.add(createPermissionView(permission, resource));
                        }
                    });
                }
            }
        });

        return models;
    }

    public PermissionViewModel createPermissionView(Permission permission, Resource resource) {
        PermissionViewModel model = new PermissionViewModel();
        model.setId(permission.getId());
        model.setName(permission.getName());
        model.setThirdMenu(resource.getLevel().equals(Level.LEVEL_3.getValue()) ? resource.getName() : "");
        model.setSecondMenu(resource.getParent() == null ? "" : resource.getParent().getName());
        model.setFirstMenu(resource.getParent() == null ? resource.getName() :
            resourceRepository.findOne(resource.getParent().getParent().getId()).getName());
        return model;
    }

    /**
     * 目录变化
     * @param newResourceId
     * @param level
     * @return
     */
    public List<Resource> changeMenu(Long newResourceId, Integer level) {
        logger.info("menu has changed, new resource id is {}, menu level is {}", newResourceId, level);
        Resource resource = resourceRepository.findOne(newResourceId);

        switch (Level.format(level)) {
            case LEVEL_1:
                if (newResourceId == 0) {
                    //全部
                    return this.listAllMenus();
                }
                return this.getResourceByFirstResource(newResourceId, null);
            case LEVEL_2:
                AssertUtil.notNull(resource, "can not find second resource by id " + newResourceId);
                AssertUtil.notNull(resource.getParent(), "second menu must has parent menu");
                return this.getResourceByFirstResource(resource.getParent().getId(), resource);
            case LEVEL_3:
                AssertUtil.notNull(resource, "can not find third resource by id " + newResourceId);
                AssertUtil.notNull(resource.getParent(), "third menu must has parent menu");
                Resource secondResource = resourceRepository.findOne(resource.getParent().getId());
                AssertUtil.notNull(secondResource, "can not find second resource by parent id " + resource.getParent().getId());
                AssertUtil.notNull(secondResource.getParent(), "second menu must has parent menu");
                return this.getResourceByFirstResource(secondResource.getParent().getId(), secondResource);
            default:
                throw new IllegalArgumentException("illegal level type -> " + level);
        }
    }

    /**
     * 查找指定一级和二级下的三级目录
     * @param resourceId
     * @param secondResource
     * @return
     */
    private List<Resource> getResourceByFirstResource(Long resourceId, Resource secondResource) {
        List<Resource> resources = new ArrayList<>();
        //第一级目录
        Resource firstResource = resourceRepository.findOne(resourceId);
        AssertUtil.notNull(firstResource, "can not find first resource by id: " + resourceId);
        resources.add(firstResource);
        //第二级目录
        List<Resource> secondResources = resourceRepository.findByParent(firstResource);
        if (!CollectionUtils.isEmpty(secondResources)) {
            resources.addAll(secondResources);
            //第三级目录
            List<Resource> thirdResources;
            if (null != secondResource) {
                //指定第二级目录查询
                thirdResources = resourceRepository.findByParent(secondResource);
            } else {
                //通过查出来的二级目查找三级目录
                thirdResources = resourceRepository.findByParentIn(secondResources);
            }
            if (!CollectionUtils.isEmpty(thirdResources)) {
                resources.addAll(thirdResources);
            }
        }

        return resources;
    }

    public enum Level {
        LEVEL_1(1),LEVEL_2(2),LEVEL_3(3);
        private final Integer value;

        public static Level format(Integer levelVal) {
            for (Level level: Level.values()) {
                if (levelVal.equals(level.getValue())) {
                    return level;
                }
            }
            return null;
        }

        Level(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

}

