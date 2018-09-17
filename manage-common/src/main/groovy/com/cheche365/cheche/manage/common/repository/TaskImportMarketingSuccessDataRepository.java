package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TaskImportMarketingSuccessData;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu.yelong on 2016-03-25.
 */
@Repository
public interface TaskImportMarketingSuccessDataRepository extends PagingAndSortingRepository<TaskImportMarketingSuccessData, Long>, JpaSpecificationExecutor<TaskImportMarketingSuccessData> {

    List<TaskImportMarketingSuccessData> findByEnable(Boolean enable);

    TaskImportMarketingSuccessData findByCacheKey(String cacheKey);
}
