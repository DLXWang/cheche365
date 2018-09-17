package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.AppidMapping;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppidMappingRepository extends PagingAndSortingRepository<AppidMapping, Long>, JpaSpecificationExecutor<AppidMapping> {

    public AppidMapping findFirstByUserAppid(String appId);
}
