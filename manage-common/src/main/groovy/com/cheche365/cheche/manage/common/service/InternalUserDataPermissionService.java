package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import com.cheche365.cheche.manage.common.repository.InternalUserDataPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yellow on 2017/6/14.
 */
@Service
public class InternalUserDataPermissionService {
    @Autowired
    private InternalUserDataPermissionRepository internalUserDataPermissionRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<InternalUserDataPermission> findByUser(InternalUser internalUser) {
        return internalUserDataPermissionRepository.findByInternalUser(internalUser);
    }

    /**
     *
     * 判断是否是第一次权限操作，如果是就是保存操作，如果不是就是更新操作
     * @param internalUserDataPermission
     * @return
     */
    public void saveOrUpdate(InternalUserDataPermission internalUserDataPermission) {
        InternalUserDataPermission internalUDP = internalUserDataPermissionRepository.findByInternalUserAndEntityAndField(internalUserDataPermission.getInternalUser(),
            internalUserDataPermission.getEntity(), internalUserDataPermission.getField());
        if (internalUDP != null) {
            internalUserDataPermission.setId(internalUDP.getId());
        }
        internalUserDataPermissionRepository.save(internalUserDataPermission);
    }

    /**
     * @param interId  内部用户的id
     * @param entity  要查询的对象
     * @param field  根据那个字段进行查询
     * @return
     */
    public InternalUserDataPermission findByCondition(Long interId, String entity, String field) {
        return internalUserDataPermissionRepository.findByIdAndEntityAndField(interId,entity,field);
    }
    /**
     *
     *
     * @param internalUser
     * @param entity  要查询的对象
     * @param field  根据那个字段进行查询
     * @return
     */
    public InternalUserDataPermission getChossedPermission(InternalUser internalUser, String entity, String field) {
        return internalUserDataPermissionRepository.findByInternalUserAndEntityAndField(internalUser, entity, field);
    }

    /**
     * 查询该内部用户所有的权限
     * @param internalUser
     * @return
     */
    public List<InternalUserDataPermission> findAllPermission(InternalUser internalUser) {
        return internalUserDataPermissionRepository.findByInternalUser(internalUser);
    }

    /**
     * 更新对象的状态
     * @param id
     * @param status
     * @return
     */
    public Boolean updateStatus(Long id, Boolean status){
        try {
            InternalUserDataPermission internalUserDataPermission = internalUserDataPermissionRepository.findOne(id);
            internalUserDataPermission.setEnable(status);
            internalUserDataPermissionRepository.save(internalUserDataPermission);
            return true;
        } catch (Exception e) {
            logger.error("update status have error!", e);
            return false;
        }
    }


}
