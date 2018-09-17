package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PermissionType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangfei on 2015/9/11.
 */
@Repository
public interface PermissionTypeRepository extends PagingAndSortingRepository<PermissionType,Long> {
    PermissionType findFirstByName(String name);
}
