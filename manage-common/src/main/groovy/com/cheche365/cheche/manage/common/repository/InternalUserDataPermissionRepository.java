package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.manage.common.model.InternalUserDataPermission;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yellow on 2017/6/14.
 */
@Repository
public interface InternalUserDataPermissionRepository extends PagingAndSortingRepository<InternalUserDataPermission, Long>, JpaSpecificationExecutor<InternalUserDataPermission> {

    List<InternalUserDataPermission> findByInternalUser(InternalUser internalUser);

    //通过id 对象 字段进行查找
    InternalUserDataPermission findByIdAndEntityAndField(Long id, String entity, String field);
    //通过内部用户 对象 字段进行查找
    InternalUserDataPermission findByInternalUserAndEntityAndField(InternalUser InternalUser, String entity, String field);
}
