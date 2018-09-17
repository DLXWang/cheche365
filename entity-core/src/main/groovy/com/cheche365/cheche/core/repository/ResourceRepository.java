package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Resource;
import com.cheche365.cheche.core.model.ResourceType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangfei on 2015/9/11.
 */
@Repository
public interface ResourceRepository extends PagingAndSortingRepository<Resource,Long> {
    List<Resource> findByResourceType(ResourceType resourceType);

    List<Resource> findByParent(Resource resource);

    List<Resource> findByParentIn(List<Resource> resourceList);

    List<Resource> findByResourceTypeAndLevel(ResourceType resourceType, Integer level);
}
